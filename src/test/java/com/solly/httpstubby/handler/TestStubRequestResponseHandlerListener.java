package com.solly.httpstubby.handler;

import com.solly.httpstubby.request.HttpRequest;
import com.solly.httpstubby.response.Response;

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
