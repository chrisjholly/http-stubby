package com.staygrounded.httpstubby.handler;

import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.HttpResponse;

public interface RequestResponseHandlerListener {

    void newRequest(HttpRequest httpRequest);

    void newResponse(HttpResponse httpResponse);
}
