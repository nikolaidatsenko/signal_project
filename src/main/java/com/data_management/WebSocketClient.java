package com.data_management;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The WebSocketClient class implements the DataReader interface to handle
 * WebSocket connections, message handling, and error management.
 */
public class WebSocketClient implements DataReader {
    private org.java_websocket.client.WebSocketClient client;
    private DataStorage dataStorage;
    private Consumer<Exception> onErrorCallback;

    /**
     * Constructs a WebSocketClient with the specified DataStorage.
     * @param dataStorage The DataStorage instance for storing patient data.
     */
    public WebSocketClient(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Sets a callback function to handle errors.
     * @param onErrorCallback The callback function for error handling.
     */
    public void setOnErrorCallback(Consumer<Exception> onErrorCallback) {
        this.onErrorCallback = onErrorCallback;
    }

    /**
     * Connects to the WebSocket server specified by the URI.
     * @param serverUri The URI of the WebSocket server.
     * @throws IOException If an error occurs during connection.
     */
    @Override
    public void connect(String serverUri) throws IOException {
        try {
            URI uri = new URI(serverUri);  // Validate URI here
            client = new org.java_websocket.client.WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to WebSocket server");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Received message: " + message);
                    handleMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected from WebSocket server with code " + code + ", reason: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("WebSocket error occurred: ");
                    ex.printStackTrace();
                    if (onErrorCallback != null) {
                        onErrorCallback.accept(ex);
                    }
                }
            };
            boolean connected = client.connectBlocking(5, TimeUnit.SECONDS);  // Ensure blocking connect with timeout
            if (!connected) {
                throw new IOException("Connection timed out");
            }
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI: " + serverUri, e);
        } catch (InterruptedException e) {
            throw new IOException("Connection interrupted", e);
        }
    }

    /**
     * Not used in this context.
     * @param dataStorage The DataStorage instance.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        // Method not used in this context
    }

    /**
     * Handles incoming messages by parsing and storing patient data.
     * @param message The received message.
     */
    private void handleMessage(String message) {
        System.out.println("Handling message: " + message);
        try {
            String[] recordParts = message.split(",");
            if (recordParts.length != 4) {
                System.err.println("Invalid message format: " + message);
                return;
            }
            int patientId = Integer.parseInt(recordParts[0].trim());
            long timestamp = Long.parseLong(recordParts[1].trim());
            String label = recordParts[2].trim();
            double measurementValue = Double.parseDouble(recordParts[3].trim());

            dataStorage.addPatientData(patientId, measurementValue, label, timestamp);
        } catch (NumberFormatException e) {
            System.err.println("An error occurred while parsing the message: " + message);
            e.printStackTrace();
        }
    }

    /**
     * Checks if the WebSocket client is currently open.
     * @return True if the client is open, false otherwise.
     */
    public boolean isClientOpen() {
        return client != null && client.isOpen();
    }

    /**
     * Closes the WebSocket connection if it is open.
     */
    public void close() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Simulates receiving a message for testing purposes by directly calling handleMessage.
     * @param message The message to simulate.
     */
    public void simulateMessage(String message) {
        handleMessage(message);
    }
}
