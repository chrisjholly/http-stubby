package com.staygrounded.httpstubby.server;

import com.staygrounded.httpstubby.auditor.HttpRequestResponseAuditor;
import com.staygrounded.httpstubby.auditor.HttpRequestResponseEventListener;
import com.staygrounded.httpstubby.auditor.HttpRequestResponseHistory;
import com.staygrounded.httpstubby.server.request.HttpRequest;
import com.staygrounded.httpstubby.server.response.HttpResponseBuilder;
import com.staygrounded.httpstubby.matchers.response.HttpResponseMatcher;
import org.hamcrest.Matcher;

import static com.staygrounded.httpstubby.server.handler.HttpRequestHandler.httpRequestHandler;
import static org.hamcrest.core.AllOf.allOf;

class HttpStubbyServer {

    private final HttpServer server;
    private final HttpResponseMatcher httpResponseMatcher;
    private final HttpRequestResponseHistory httpRequestResponseHistory;
    private final HttpRequestResponseAuditor httpRequestResponseAuditor;

    private HttpStubbyServer(HttpServer server) {
        this.server = server;
        this.httpResponseMatcher = new HttpResponseMatcher();
        this.httpRequestResponseHistory = new HttpRequestResponseHistory();
        this.httpRequestResponseAuditor = new HttpRequestResponseAuditor(httpRequestResponseHistory);
    }

    public static HttpStubbyServer stubbyServerWith(HttpServer httpServer) {
        return new HttpStubbyServer(httpServer);
    }

    public void registerHttpRequestResponseEventListener(HttpRequestResponseEventListener httpRequestResponseEventListener) {
        httpRequestResponseAuditor.registerHttpRequestResponseEventListener(httpRequestResponseEventListener);
    }

    public void start() {
        this.server.addContext(httpRequestHandler(httpResponseMatcher, httpRequestResponseAuditor));
        this.httpRequestResponseHistory.clear();
        this.server.start();
    }

    public void stop() {
        server.stop();
    }

    public int httpPort() {
        return server.port();
    }

    public HttpRequestResponseHistory httpRequestResponseHistory() {
        return httpRequestResponseHistory;
    }

    public void clearHistoryAndResponses() {
        httpRequestResponseHistory.clear();
        httpResponseMatcher.clearResponses();
    }

    public <T extends HttpResponseBuilder> T willReturn(T responseBuilder, Matcher<HttpRequest>... matchers) {
        httpResponseMatcher.addResponse(allOf(matchers), responseBuilder);
        return responseBuilder;
    }

    public <T extends HttpResponseBuilder> T willReturnWhenNoResponseFound(T responseBuilder) {
        httpResponseMatcher.setDefaultResponse(responseBuilder);
        return responseBuilder;
    }

}