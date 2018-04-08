package com.staygrounded.httpstubby.handler;

import com.staygrounded.httpstubby.history.History;
import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.request.HttpRequestFactory;
import com.staygrounded.httpstubby.response.HttpResponseSelector;
import com.staygrounded.httpstubby.response.Response;
import com.staygrounded.httpstubby.response.ResponseExecutor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpRequestHandler implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final HttpRequestFactory httpRequestFactory;
    private final ResponseExecutor responseExecutor;
    private final HttpResponseSelector responseSelector;
    private final History history;
    private final RequestResponseHandlerListener requestResponseHandlerListener;

    HttpRequestHandler(HttpRequestFactory httpRequestFactory,
                       ResponseExecutor responseExecutor,
                       HttpResponseSelector responseSelector,
                       History history,
                       RequestResponseHandlerListener requestResponseHandlerListener) {
        this.httpRequestFactory = httpRequestFactory;
        this.responseExecutor = responseExecutor;
        this.responseSelector = responseSelector;
        this.history = history;
        this.requestResponseHandlerListener = requestResponseHandlerListener;
    }

    public static HttpRequestHandler aStubbableHttpHandler(HttpResponseSelector responseSelector,
                                                           History history,
                                                           RequestResponseHandlerListener requestResponseHandlerListener) {
        return new HttpRequestHandler(
                new HttpRequestFactory(),
                new ResponseExecutor(),
                responseSelector,
                history,
                requestResponseHandlerListener);
    }

    @Override
    public final void handle(HttpExchange httpExchange) {
        try {
            final HttpRequest httpRequest = httpRequestFactory.createHttpRequest(httpExchange);
            history.newRequest(httpRequest);
            requestResponseHandlerListener.newRequest(httpRequest);

            final Response response = responseSelector.selectResponse(httpRequest);
            history.newResponse(response);
            requestResponseHandlerListener.newResponse(response);

            responseExecutor.sendResponse(httpExchange, response);
        } catch (Throwable t) {
            LOG.error("An error occurred while handling: " + httpExchange.getRequestURI().toString(), t);
        } finally {
            httpExchange.close();
        }
    }

}
