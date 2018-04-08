package com.staygrounded.httpstubby.auditor;

import com.staygrounded.httpstubby.server.request.HttpRequest;
import com.staygrounded.httpstubby.server.response.HttpResponse;

public class TestStubHttpRequestResponseEventListener implements HttpRequestResponseEventListener {

    private boolean hasRequest;
    private boolean hasResponse;

    @Override
    public void newRequest(HttpRequest httpRequest) {
        this.hasRequest = true;
    }

    @Override
    public void newResponse(HttpResponse httpResponse) {
        this.hasResponse = true;
    }

    public boolean isHasRequest() {
        return hasRequest;
    }

    public boolean isHasResponse() {
        return hasResponse;
    }
}
