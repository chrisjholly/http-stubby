package com.staygrounded.httpstubby.auditor;

import com.staygrounded.httpstubby.server.request.HttpRequest;
import com.staygrounded.httpstubby.server.response.HttpResponse;

public interface HttpRequestResponseEventListener {

    void newRequest(HttpRequest httpRequest);

    void newResponse(HttpResponse httpResponse);
}
