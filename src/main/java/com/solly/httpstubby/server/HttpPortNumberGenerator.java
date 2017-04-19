package com.solly.httpstubby.server;

import java.io.IOException;
import java.net.ServerSocket;

public class HttpPortNumberGenerator {

    public static int nextAvailablePortNumber() {
        try {
            ServerSocket e = new ServerSocket(0);
            int port = e.getLocalPort();
            e.close();
            return port;
        } catch (IOException var2) {
            throw new RuntimeException("Failed to get the next available port number", var2);
        }
    }
}
