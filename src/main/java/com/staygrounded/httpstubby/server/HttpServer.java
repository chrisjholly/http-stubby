package com.staygrounded.httpstubby.server;

import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);

    private final int port;
    private ExecutorService executorService;

    private com.sun.net.httpserver.HttpServer server;

    HttpServer(int port, com.sun.net.httpserver.HttpServer server) {
        this.port = port;
        this.server = server;
    }

    public void start() {
        executorService = Executors.newFixedThreadPool(30);
        server.setExecutor(executorService);
        server.start();
        LOG.info("Started HTTP server on port: {}", port);
    }

    public void stop() {
        if (server != null) {
            server.stop(1);
        }
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
        LOG.info("Stopped HTTP server on port: {}", port);
    }

    public int port() {
        return port;
    }

    public void addContext(String context, HttpHandler httpHandler) {
        server.createContext(context, httpHandler);
    }
}
