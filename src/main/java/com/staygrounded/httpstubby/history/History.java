package com.staygrounded.httpstubby.history;

import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.HttpResponse;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class History {
    private final Deque<HttpRequest> requests = new ArrayDeque<HttpRequest>();
    private final Deque<HttpResponse> httpResponses = new ArrayDeque<HttpResponse>();

    public void newRequest(HttpRequest request) {
        synchronized (requests) {
            requests.push(request);
        }
    }

    public void newResponse(HttpResponse httpResponse) {
        if (httpResponse != null) {
            synchronized (httpResponses) {
                httpResponses.push(httpResponse);
            }
        }
    }

    public HttpRequest lastRequest() {
        synchronized (requests) {
            return requests.isEmpty() ? null : requests.peek();
        }
    }

    public synchronized HttpResponse lastResponse() {
        synchronized (httpResponses) {
            return httpResponses.isEmpty() ? null : httpResponses.peek();
        }
    }

    public String lastRequestUri() {
        HttpRequest httpRequest = lastRequest();
        return httpRequest != null ? httpRequest.getRequestUri().toString() : null;
    }

    public Map<String, String> lastRequestHeaders() {
        HttpRequest httpRequest = lastRequest();
        return httpRequest != null ? httpRequest.getRequestHeaders() : null;
    }

    public String lastRequestBody() {
        HttpRequest httpRequest = lastRequest();
        return httpRequest != null ? httpRequest.getRequestBody() : null;
    }

    public void clear() {
        synchronized (requests) {
            requests.clear();
        }
        synchronized (httpResponses) {
            httpResponses.clear();
        }
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
