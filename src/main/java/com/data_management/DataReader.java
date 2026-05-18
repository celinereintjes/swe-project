package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;
     /**
     * connects to a WebSocket server and receives data in real time
     * 
     * @param url the url of the WebSocket server
     * @param dataStorage place to store data
     * @throws IOException if there is an error connecting to the server
     */
    void connectWebSocket(String url, DataStorage dataStorage) throws IOException;
}
