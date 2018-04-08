package com.staygrounded.httpstubby.server.request;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final URI requestUri;
    private final String requestBody;
    private final Map<String, String> requestHeaders;

    private HttpRequest(HttpMethod httpMethod, URI requestUri, String requestBody, Headers requestHeaders) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.requestBody = requestBody;
        this.requestHeaders = headersToMap(requestHeaders);
    }

    public static HttpRequest createHttpRequestWith(HttpMethod httpMethod, URI uri) {
        return new HttpRequest(httpMethod, uri, null, new Headers());
    }

    public static HttpRequest createHttpRequestWith(HttpMethod httpMethod, URI uri, Headers headers) {
        return new HttpRequest(httpMethod, uri, null, headers);
    }

    public static HttpRequest createHttpRequestFrom(HttpExchange httpExchange) throws IOException {
        return new HttpRequest(
                HttpMethod.valueOf(httpExchange.getRequestMethod()),
                httpExchange.getRequestURI(),
                IOUtils.toString(httpExchange.getRequestBody()),
                httpExchange.getRequestHeaders());
    }

    public URI getRequestUri() {
        return requestUri;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public HttpMethod getRequestMethod() {
        return httpMethod;
    }

    public String getRequestBody() {
        return requestBody;
    }

    private Map<String, String> headersToMap(Headers headers) {
        final Map<String, String> requestHeaders = new LinkedHashMap<>();
        for (String headerKey : headers.keySet()) {
            requestHeaders.put(headerKey, headers.getFirst(headerKey));
        }
        return requestHeaders;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "httpMethod=" + httpMethod +
                ", requestUri=" + requestUri +
                ", requestBody='" + requestBody + '\'' +
                ", requestHeaders=" + requestHeaders +
                '}';
    }
}
