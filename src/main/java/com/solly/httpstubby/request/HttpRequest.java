package com.solly.httpstubby.request;

import com.sun.net.httpserver.Headers;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final URI requestUri;
    private final String requestBody;
    private final Map<String, String> requestHeaders;

    public HttpRequest(HttpMethod httpMethod, URI requestUri, String requestBody) {
        this(httpMethod, requestUri, requestBody, new Headers());
    }

    public HttpRequest(HttpMethod httpMethod, URI requestUri, String requestBody, Headers requestHeaders) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.requestBody = requestBody;
        this.requestHeaders = headersToMap(requestHeaders);
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
        Map<String, String> requestHeaders = new LinkedHashMap<>();
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
