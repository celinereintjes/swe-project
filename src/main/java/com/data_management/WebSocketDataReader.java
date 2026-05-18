package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * DataReader implementation that connects to a WebSocket endpoint and forwards
 * incoming simulator messages into DataStorage.
 */
public class WebSocketDataReader implements DataReader {

    private WebSocketClientImpl client;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        throw new UnsupportedOperationException("readData is not supported for WebSocketDataReader; use connectWebSocket instead");
    }

    @Override
    public void connectWebSocket(String url, DataStorage dataStorage) throws IOException {
        try {
            URI uri = new URI(url);
            client = new WebSocketClientImpl(uri, dataStorage, scheduler);
            client.connect();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URL: " + url, e);
        }
    }

    public void stop() {
        if (client != null) client.close();
        scheduler.shutdownNow();
    }
}
