package com.staygrounded.httpstubby.server;

import com.staygrounded.httpstubby.handler.DoNothingRequestResponseHandlerListener;
import com.staygrounded.httpstubby.handler.RequestResponseHandlerListener;
import com.staygrounded.httpstubby.history.History;
import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.HttpResponseMatcher;
import com.staygrounded.httpstubby.response.HttpResponseBuilder;
import com.staygrounded.httpstubby.handler.HttpRequestHandler;
import org.hamcrest.Matcher;

import static org.hamcrest.core.AllOf.allOf;

public class HttpStubbyServer {

    private final HttpServer server;
    private final HttpResponseMatcher httpResponseMatcher;
    private final History history;

    protected HttpStubbyServer(HttpServer server) {
        this.server = server;
        this.httpResponseMatcher = new HttpResponseMatcher();
        this.history = new History();
    }

    public void start() {
        this.start(new DoNothingRequestResponseHandlerListener());
    }

    public void start(RequestResponseHandlerListener requestResponseHandlerListener) {
        this.server.addContext(HttpRequestHandler.httpRequestHandler(httpResponseMatcher, history, requestResponseHandlerListener));
        this.history.clear();
        this.server.start();
    }

    public void stop() {
        server.stop();
    }

    public int httpPort() {
        return server.port();
    }

    public History history() {
        return history;
    }

    public void clearHistoryAndResponses() {
        history.clear();
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