package com.staygrounded.httpstubby.server.handler;

import com.staygrounded.httpstubby.auditor.HttpRequestResponseAuditor;
import com.staygrounded.httpstubby.server.request.HttpRequest;
import com.staygrounded.httpstubby.server.response.HttpResponse;
import com.staygrounded.httpstubby.server.response.HttpResponseMatcher;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.staygrounded.httpstubby.server.request.HttpRequest.createHttpRequestFrom;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;


public class HttpRequestHandler implements HttpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final HttpResponseMatcher httpResponseMatcher;
    private final HttpRequestResponseAuditor httpRequestResponseAuditor;

    private HttpRequestHandler(HttpResponseMatcher httpResponseMatcher, HttpRequestResponseAuditor httpRequestResponseAuditor) {
        this.httpResponseMatcher = httpResponseMatcher;
        this.httpRequestResponseAuditor = httpRequestResponseAuditor;
    }

    public static HttpRequestHandler httpRequestHandler(HttpResponseMatcher responseSelector, HttpRequestResponseAuditor httpRequestResponseAuditor) {
        return new HttpRequestHandler(responseSelector, httpRequestResponseAuditor);
    }

    @Override
    public final void handle(HttpExchange httpExchange) {
        try {
            final HttpRequest httpRequest = createHttpRequestFrom(httpExchange);
            httpRequestResponseAuditor.newRequest(httpRequest);

            final HttpResponse httpResponse = httpResponseMatcher.findHttpResponseFromHttpRequest(httpRequest);
            httpRequestResponseAuditor.newResponse(httpResponse);

            httpExchange.getResponseHeaders().put("Content-Type", singletonList(httpResponse.getContentType()));
            httpExchange.getResponseHeaders().putAll(httpResponse.getHeaders().entrySet()
                    .stream().collect(toMap(Map.Entry::getKey, entry -> singletonList(entry.getValue()))));

            httpExchange.sendResponseHeaders(httpResponse.getStatusCode(), httpResponse.getResponseLength());
            httpExchange.getResponseBody().write(httpResponse.getBody());

        } catch (Throwable t) {
            LOG.error("An error occurred while handling: " + httpExchange.getRequestURI().toString(), t);
        } finally {
            httpExchange.close();
        }
    }

}
