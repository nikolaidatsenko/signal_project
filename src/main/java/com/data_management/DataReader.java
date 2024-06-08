package com.data_management;

import java.io.IOException;
import java.net.URISyntaxException;

public interface DataReader {
    /**
     * Connect to the data source.
     *
     * @param serverUri the URI of the WebSocket server
     * @throws URISyntaxException if the URI is incorrect
     * @throws IOException if there is an error connecting to the server
     */
    void connect(String serverUri) throws URISyntaxException, IOException;

    /**
     * Reads data from a specified source and stores it in the data storage.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;
}
