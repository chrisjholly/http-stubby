package com.staygrounded.httpstubby.handler;

import com.staygrounded.httpstubby.history.History;
import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.HttpResponse;
import com.staygrounded.httpstubby.response.HttpResponseMatcher;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.staygrounded.httpstubby.request.HttpRequest.createHttpRequestFrom;
import static java.util.Collections.singletonList;


public class HttpRequestHandler implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final HttpResponseMatcher responseSelector;
    private final History history;
    private final RequestResponseHandlerListener requestResponseHandlerListener;

    private HttpRequestHandler(HttpResponseMatcher responseSelector,
                               History history,
                               RequestResponseHandlerListener requestResponseHandlerListener) {
        this.responseSelector = responseSelector;
        this.history = history;
        this.requestResponseHandlerListener = requestResponseHandlerListener;
    }

    public static HttpRequestHandler httpRequestHandler(HttpResponseMatcher responseSelector,
                                                        History history,
                                                        RequestResponseHandlerListener requestResponseHandlerListener) {
        return new HttpRequestHandler(responseSelector, history, requestResponseHandlerListener);
    }

    @Override
    public final void handle(HttpExchange httpExchange) {
        try {
            final HttpRequest httpRequest = createHttpRequestFrom(httpExchange);
            history.newRequest(httpRequest);
            requestResponseHandlerListener.newRequest(httpRequest);

            final HttpResponse httpResponse = responseSelector.matchHttpRequestToHttpResponse(httpRequest);
            history.newResponse(httpResponse);
            requestResponseHandlerListener.newResponse(httpResponse);

            generateResponse(httpExchange, httpResponse);
        } catch (Throwable t) {
            LOG.error("An error occurred while handling: " + httpExchange.getRequestURI().toString(), t);
        } finally {
            httpExchange.close();
        }
    }

    private void generateResponse(HttpExchange httpExchange, HttpResponse httpResponse) throws IOException {
        if (httpResponse == null) {
            throw new IllegalArgumentException("Cannot send response, none set");
        }

        final Map<String, List<String>> headerMap = new HashMap<>();
        httpResponse.getHeaders().forEach((key, value) -> headerMap.put(key, singletonList(value)));

        httpExchange.getResponseHeaders().put("Content-Type", singletonList(httpResponse.getContentType()));
        httpExchange.getResponseHeaders().putAll(headerMap);

        int responseBodyLength = httpResponse.getResponseLength();
        httpExchange.sendResponseHeaders(httpResponse.getStatusCode(), responseBodyLength);

        httpExchange.getResponseBody().write(httpResponse.getBody());
    }

}
