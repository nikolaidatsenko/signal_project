package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Represents an output strategy that sends patient data over TCP sockets.
 * This class initializes a TCP server socket on the specified port and accepts client connections.
 * Once a client is connected, it sends patient data to the client.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a new TcpOutputStrategy with the specified port.
     * Initializes a TCP server socket on the given port and accepts client connections.
     *
     * @param port the port number for the TCP server socket
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Outputs the patient data over the established TCP connection.
     * If a client is connected, it sends the patient data in the format: "patientId,timestamp,label,data".
     *
     * @param patientId the ID of the patient whose data is being output
     * @param timestamp the timestamp of the patient data
     * @param label     the label associated with the patient data
     * @param data      the patient data to be output
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
