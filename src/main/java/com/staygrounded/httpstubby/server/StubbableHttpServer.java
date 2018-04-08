package com.staygrounded.httpstubby.server;

import com.staygrounded.httpstubby.handler.DoNothingRequestResponseHandlerListener;
import com.staygrounded.httpstubby.handler.RequestResponseHandlerListener;
import com.staygrounded.httpstubby.history.History;
import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.HttpResponseSelector;
import com.staygrounded.httpstubby.response.ResponseBuilder;
import com.staygrounded.httpstubby.handler.HttpRequestHandler;
import org.hamcrest.Matcher;

import static org.hamcrest.core.AllOf.allOf;

public class StubbableHttpServer {

    private final HttpServer server;
    private final HttpResponseSelector responseSelector;
    private final History history;

    public StubbableHttpServer(HttpServer server) {
        this.server = server;
        this.responseSelector = HttpResponseSelector.stubbableResponseSelector();
        this.history = new History();
    }

    public void start() {
        this.start(new DoNothingRequestResponseHandlerListener());
    }

    public void start(RequestResponseHandlerListener requestResponseHandlerListener) {
        server.addContext("/", HttpRequestHandler.aStubbableHttpHandler(responseSelector, history, requestResponseHandlerListener));
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
        responseSelector.clearResponses();
    }

    public <T extends ResponseBuilder> T willReturn(T responseBuilder, Matcher<HttpRequest>... matchers) {
        responseSelector.addResponse(allOf(matchers), responseBuilder);
        return responseBuilder;
    }

    public <T extends ResponseBuilder> T willReturnWhenNoResponseFound(T responseBuilder) {
        responseSelector.setDefaultResponse(responseBuilder);
        return responseBuilder;
    }

}