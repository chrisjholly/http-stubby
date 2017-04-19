package com.solly.httpstubby.handler;

import com.solly.httpstubby.request.HttpRequest;
import com.solly.httpstubby.response.Response;

public class DoNothingRequestResponseHandlerListener implements RequestResponseHandlerListener {

    @Override
    public void newRequest(HttpRequest httpRequest) {

    }

    @Override
    public void newResponse(Response response) {

    }
}
