package com.data_management;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class WebSocketDataReaderTest {

    private WebSocketServer server;

    @AfterEach
    public void tearDown() throws Exception {
        if (server != null) server.stop(100);
    }

    @Test
    void receivesAndParsesMessage() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        server = new WebSocketServer(new InetSocketAddress(0)) {
            @Override
            public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
                long now = System.currentTimeMillis();
                conn.send(String.format("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s", 1, now, "Saturation", "95.0"));
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) { }

            @Override
            public void onMessage(WebSocket conn, String message) { }

            @Override
            public void onError(WebSocket conn, Exception ex) { }

            @Override
            public void onStart() { latch.countDown(); }
        };
        server.start();
        assertTrue(latch.await(2, TimeUnit.SECONDS));

        int port = server.getPort();
        DataStorage storage = DataStorage.getInstance();
        storage.clear();

        WebSocketDataReader reader = new WebSocketDataReader();
        reader.connectWebSocket("ws://localhost:" + port, storage);

        // wait for the message to be processed
        TimeUnit.MILLISECONDS.sleep(200);

        List<PatientRecord> records = storage.getRecords(1, 0, System.currentTimeMillis());
        assertFalse(records.isEmpty());
        assertEquals(95.0, records.get(0).getMeasurementValue(), 0.001);
    }
}
