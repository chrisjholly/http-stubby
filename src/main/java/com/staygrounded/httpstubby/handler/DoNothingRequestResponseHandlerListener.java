package com.staygrounded.httpstubby.handler;

import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.HttpResponse;

public class DoNothingRequestResponseHandlerListener implements RequestResponseHandlerListener {

    @Override
    public void newRequest(HttpRequest httpRequest) {

    }

    @Override
    public void newResponse(HttpResponse httpResponse) {

    }
}
