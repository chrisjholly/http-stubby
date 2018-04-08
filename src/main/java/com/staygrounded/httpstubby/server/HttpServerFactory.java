package com.staygrounded.httpstubby.server;

import com.staygrounded.httpstubby.server.ssl.SelfSignedSSLContextFactory;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServerFactory {

    private final int port;
    private final ExecutorService executorService = Executors.newFixedThreadPool(30);

    private HttpServerFactory(int port) {
        this.port = port;
    }

    public static HttpServer createHttpServer(int port) {
        return new HttpServerFactory(port).safeCreateHttpServer();
    }

    public static HttpServer createHttpsServer(int port) {
        final SSLContext sslContext = new SelfSignedSSLContextFactory()
                .createContext("/https-keystore.jks", "password");
        return new HttpServerFactory(port)
                .safeCreateHttpsServer(sslContext);
    }

    public static HttpServer createHttpsServer(int port, SSLContext sslContext) {
        return new HttpServerFactory(port)
                .safeCreateHttpsServer(sslContext);
    }

    private HttpServer safeCreateHttpServer() {
        try {
            final com.sun.net.httpserver.HttpServer httpServer =
                    com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 100);
            httpServer.setExecutor(executorService);
            return new HttpServer(port, httpServer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpServer safeCreateHttpsServer(SSLContext sslContext) {
        try {
            final HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(port), 100);
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    params.setSSLParameters(getSSLContext().getDefaultSSLParameters());
                }
            });
            httpsServer.setExecutor(executorService);
            return new HttpServer(port, httpsServer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
