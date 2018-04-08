package com.staygrounded.httpstubby.auditor;

import com.staygrounded.httpstubby.server.request.HttpRequest;
import com.staygrounded.httpstubby.server.response.HttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chrisholly on 08/04/2018.
 */
public class HttpRequestResponseAuditor {

    private final HttpRequestResponseHistory httpRequestResponseHistory;
    private final List<HttpRequestResponseEventListener> httpRequestResponseEventListeners = new ArrayList<>();

    public HttpRequestResponseAuditor(HttpRequestResponseHistory httpRequestResponseHistory) {
        this.httpRequestResponseHistory = httpRequestResponseHistory;
    }

    public void newRequest(HttpRequest httpRequest) {
        httpRequestResponseHistory.newRequest(httpRequest);
        httpRequestResponseEventListeners.forEach(eventListener -> eventListener.newRequest(httpRequest));
    }

    public void newResponse(HttpResponse httpResponse) {
        httpRequestResponseHistory.newResponse(httpResponse);
        httpRequestResponseEventListeners.forEach(eventListener -> eventListener.newResponse(httpResponse));
    }

    public void registerHttpRequestResponseEventListener(HttpRequestResponseEventListener httpRequestResponseEventListener) {
        httpRequestResponseEventListeners.add(httpRequestResponseEventListener);
    }
}
