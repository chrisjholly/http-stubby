package com.staygrounded.httpstubby.auditor;

import com.staygrounded.httpstubby.server.request.HttpRequest;
import com.staygrounded.httpstubby.server.response.HttpResponse;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.LinkedList;

public class HttpRequestResponseHistory {

    private final LinkedList<HttpRequest> requests = new LinkedList<>();
    private final LinkedList<HttpResponse> httpResponses = new LinkedList<>();

    public void newRequest(HttpRequest request) {
        requests.push(request);
    }

    public void newResponse(HttpResponse httpResponse) {
        httpResponses.push(httpResponse);
    }

    public HttpRequest lastRequest() {
        return requests.isEmpty() ? null : requests.getFirst();
    }

    public HttpResponse lastResponse() {
        return httpResponses.isEmpty() ? null : httpResponses.getFirst();
    }

    public void clear() {
        requests.clear();
        httpResponses.clear();
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
