package com.staygrounded.httpstubby.handler;

import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.Response;

public class TestStubRequestResponseHandlerListener implements RequestResponseHandlerListener {

    private boolean hasRequest;
    private boolean hasResponse;

    @Override
    public void newRequest(HttpRequest httpRequest) {
        this.hasRequest = true;
    }

    @Override
    public void newResponse(Response response) {
        this.hasResponse = true;
    }

    public boolean isHasRequest() {
        return hasRequest;
    }

    public boolean isHasResponse() {
        return hasResponse;
    }
}
