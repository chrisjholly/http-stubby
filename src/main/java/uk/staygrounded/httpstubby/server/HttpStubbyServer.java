package uk.staygrounded.httpstubby.server;

import uk.staygrounded.httpstubby.auditor.HttpRequestResponseAuditor;
import uk.staygrounded.httpstubby.auditor.HttpRequestResponseEventListener;
import uk.staygrounded.httpstubby.auditor.HttpRequestResponseHistory;
import uk.staygrounded.httpstubby.server.request.HttpRequest;
import uk.staygrounded.httpstubby.server.response.HttpResponseBuilder;
import uk.staygrounded.httpstubby.server.response.HttpResponseMatcher;
import org.hamcrest.Matcher;

import static uk.staygrounded.httpstubby.server.handler.HttpRequestHandler.httpRequestHandler;
import static org.hamcrest.core.AllOf.allOf;

public class HttpStubbyServer {

    private final HttpServer server;
    private final HttpResponseMatcher httpResponseMatcher;
    private final HttpRequestResponseHistory httpRequestResponseHistory;
    private final HttpRequestResponseAuditor httpRequestResponseAuditor;

    private HttpStubbyServer(HttpServer server) {
        this.server = server;
        this.httpResponseMatcher = new HttpResponseMatcher();
        this.httpRequestResponseHistory = new HttpRequestResponseHistory();
        this.httpRequestResponseAuditor = new HttpRequestResponseAuditor(httpRequestResponseHistory);
    }

    public static HttpStubbyServer stubbyServerWith(HttpServer httpServer) {
        return new HttpStubbyServer(httpServer);
    }

    public void registerHttpRequestResponseEventListener(HttpRequestResponseEventListener httpRequestResponseEventListener) {
        httpRequestResponseAuditor.registerHttpRequestResponseEventListener(httpRequestResponseEventListener);
    }

    public void start() {
        this.server.addContext(httpRequestHandler(httpResponseMatcher, httpRequestResponseAuditor));
        this.httpRequestResponseHistory.clearHttpRequestAndResponses();
        this.server.start();
    }

    public void stop() {
        server.stop();
    }

    public int httpPort() {
        return server.port();
    }

    public HttpRequestResponseHistory httpRequestResponseHistory() {
        return httpRequestResponseHistory;
    }

    public void clearHistoryAndResponses() {
        httpRequestResponseHistory.clearHttpRequestAndResponses();
        httpResponseMatcher.clearResponses();
    }

    public HttpResponseBuilder willReturn(HttpResponseBuilder responseBuilder, Matcher<HttpRequest>... matchers) {
        httpResponseMatcher.addResponse(allOf(matchers), responseBuilder);
        return responseBuilder;
    }

    public HttpResponseBuilder willReturnWhenNoResponseFound(HttpResponseBuilder responseBuilder) {
        httpResponseMatcher.setDefaultResponse(responseBuilder);
        return responseBuilder;
    }

}