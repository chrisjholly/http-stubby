package com.solly.httpstubby.handler;

import com.solly.httpstubby.request.HttpRequest;
import com.solly.httpstubby.response.Response;

public interface RequestResponseHandlerListener {
    void newRequest(HttpRequest httpRequest);

    void newResponse(Response response);
}
