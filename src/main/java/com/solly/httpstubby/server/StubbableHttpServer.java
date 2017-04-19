package com.solly.httpstubby.server;

import com.solly.httpstubby.handler.DoNothingRequestResponseHandlerListener;
import com.solly.httpstubby.handler.RequestResponseHandlerListener;
import com.solly.httpstubby.history.History;
import com.solly.httpstubby.request.HttpRequest;
import com.solly.httpstubby.response.HttpResponseSelector;
import com.solly.httpstubby.response.ResponseBuilder;
import org.hamcrest.Matcher;

import static com.solly.httpstubby.handler.HttpRequestHandler.aStubbableHttpHandler;
import static com.solly.httpstubby.response.HttpResponseSelector.stubbableResponseSelector;
import static org.hamcrest.core.AllOf.allOf;

public class StubbableHttpServer {

    private final HttpServer server;
    private final HttpResponseSelector responseSelector;
    private final History history;

    public StubbableHttpServer(HttpServer server) {
        this.server = server;
        this.responseSelector = stubbableResponseSelector();
        this.history = new History();
    }

    public void startWithContext(String context) {
        this.startWithContext(context, new DoNothingRequestResponseHandlerListener());
    }

    public void startWithContext(String context, RequestResponseHandlerListener requestResponseHandlerListener) {
        server.addContext(context, aStubbableHttpHandler(responseSelector, history, requestResponseHandlerListener));
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