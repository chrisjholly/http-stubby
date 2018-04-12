package uk.staygrounded.httpstubby.auditor;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import uk.staygrounded.httpstubby.server.response.HttpResponse;

public interface HttpRequestResponseEventListener {

    void newRequest(HttpRequest httpRequest);

    void newResponse(HttpResponse httpResponse);
}
