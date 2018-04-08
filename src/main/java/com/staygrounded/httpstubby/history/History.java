package com.staygrounded.httpstubby.history;

import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.Response;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class History {
    private final Deque<HttpRequest> requests = new ArrayDeque<HttpRequest>();
    private final Deque<Response> responses = new ArrayDeque<Response>();

    public void newRequest(HttpRequest request) {
        synchronized (requests) {
            requests.push(request);
        }
    }

    public void newResponse(Response response) {
        if (response != null) {
            synchronized (responses) {
                responses.push(response);
            }
        }
    }

    public HttpRequest lastRequest() {
        synchronized (requests) {
            return requests.isEmpty() ? null : requests.peek();
        }
    }

    public synchronized Response lastResponse() {
        synchronized (responses) {
            return responses.isEmpty() ? null : responses.peek();
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
        synchronized (responses) {
            responses.clear();
        }
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
