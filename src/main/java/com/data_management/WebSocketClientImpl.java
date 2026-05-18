package com.data_management;

import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Lightweight WebSocket client that parses simulator messages and forwards
 * them to a DataStorage instance. Implements exponential-backoff reconnection
 * on unexpected closes/errors and tolerates malformed messages.
 */
public class WebSocketClientImpl extends WebSocketClient {

    private static final Logger LOGGER = Logger.getLogger(WebSocketClientImpl.class.getName());

    private final DataStorage dataStorage;
    private final ScheduledExecutorService scheduler;
    private final AtomicInteger attempt = new AtomicInteger(0);
    private final int maxBackoffSeconds = 60;

    public WebSocketClientImpl(URI serverUri, DataStorage dataStorage, ScheduledExecutorService scheduler) {
        super(serverUri);
        this.dataStorage = dataStorage;
        this.scheduler = scheduler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        LOGGER.log(Level.INFO, "WebSocket opened: {0}", getURI());
        attempt.set(0);
    }

    @Override
    public void onMessage(String message) {
        // Expected format: "Patient ID: %d, Timestamp: %d, Label: %s, Data: %s"
        try {
            ParsedRecord record = parseMessage(message);
            if (record != null) {
                dataStorage.addPatientData(record.patientId, record.value, record.label, record.timestamp);
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Malformed WebSocket message: {0}", new Object[] { message });
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOGGER.log(Level.INFO, String.format("WebSocket closed (remote=%s): %s - %s", remote, code, reason));
        scheduleReconnect();
    }

    @Override
    public void onError(Exception ex) {
        LOGGER.log(Level.SEVERE, "WebSocket error", ex);
        scheduleReconnect();
    }

    private void scheduleReconnect() {
        int att = attempt.incrementAndGet();
        long delay = Math.min((1L << Math.min(att, 6)), maxBackoffSeconds); // cap exponential backoff
        LOGGER.log(Level.INFO, "Scheduling reconnect in {0}s (attempt={1})", new Object[] { delay, att });
        if (scheduler != null) {
            scheduler.schedule(() -> {
                try {
                    if (!this.isOpen()) {
                        this.reconnect();
                    }
                } catch (RuntimeException e) {
                    LOGGER.log(Level.WARNING, "Reconnect attempt failed", e);
                    scheduleReconnect();
                }
            }, delay, TimeUnit.SECONDS);
        }
    }

    private ParsedRecord parseMessage(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = line.split(",");
        if (parts.length < 4) return null;
        try {
            String p0 = parts[0].trim();
            int patientId = Integer.parseInt(p0.substring(p0.indexOf(':') + 1).trim());
            String p1 = parts[1].trim();
            long timestamp = Long.parseLong(p1.substring(p1.indexOf(':') + 1).trim());
            String p2 = parts[2].trim();
            String label = p2.substring(p2.indexOf(':') + 1).trim();
            StringBuilder dataBuilder = new StringBuilder(parts[3].trim());
            for (int i = 4; i < parts.length; i++) dataBuilder.append(",").append(parts[i]);
            String dataPart = dataBuilder.toString();
            String valueStr = dataPart.substring(dataPart.indexOf(':') + 1).trim();
            double value = Double.parseDouble(valueStr);
            return new ParsedRecord(patientId, timestamp, label, value);
        } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException e) {
            throw new IllegalArgumentException("Unable to parse message", e);
        }
    }

    private static final class ParsedRecord {
        final int patientId;
        final long timestamp;
        final String label;
        final double value;

        ParsedRecord(int patientId, long timestamp, String label, double value) {
            this.patientId = patientId;
            this.timestamp = timestamp;
            this.label = label;
            this.value = value;
        }
    }
}
