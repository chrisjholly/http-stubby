package uk.staygrounded.httpstubby.auditor;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import uk.staygrounded.httpstubby.server.response.HttpResponse;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.LinkedList;

public class HttpRequestResponseHistory {

    private final LinkedList<HttpRequest> httpRequests = new LinkedList<>();
    private final LinkedList<HttpResponse> httpResponses = new LinkedList<>();

    void newRequest(HttpRequest request) {
        httpRequests.push(request);
    }

    void newResponse(HttpResponse httpResponse) {
        httpResponses.push(httpResponse);
    }

    public HttpRequest lastRequest() {
        return httpRequests.isEmpty() ? null : httpRequests.getFirst();
    }

    public HttpResponse lastResponse() {
        return httpResponses.isEmpty() ? null : httpResponses.getFirst();
    }

    public void clearHttpRequestAndResponses() {
        httpRequests.clear();
        httpResponses.clear();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
