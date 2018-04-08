package com.staygrounded.httpstubby.handler;

import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.Response;

public interface RequestResponseHandlerListener {

    void newRequest(HttpRequest httpRequest);

    void newResponse(Response response);
}
