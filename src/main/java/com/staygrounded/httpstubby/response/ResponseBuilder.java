package com.staygrounded.httpstubby.response;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.lang.Thread.sleep;
import static java.time.Duration.ZERO;

public class ResponseBuilder {

    private final Map<String, String> headers = new HashMap<>();
    private final HttpStatus.Code statusCode;
    private final Callable<String> responseCallback;
    private Duration latency = ZERO;

    public static ResponseBuilder responseOf(HttpStatus.Code code, String responseBody) {
        return new ResponseBuilder(code, responseBody);
    }

    public static ResponseBuilder responseOf(HttpStatus.Code code, Callable<String> responseCallback) {
        return new ResponseBuilder(code, responseCallback);
    }

    public ResponseBuilder(HttpStatus.Code statusCode, final String body) {
        this.statusCode = statusCode;
        this.responseCallback = () -> body;
    }

    public ResponseBuilder(HttpStatus.Code statusCode, final Callable<String> responseCallback) {
        this.statusCode = statusCode;
        this.responseCallback = responseCallback::call;
    }

    public ResponseBuilder withHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public ResponseBuilder withContentType(String mediaType) {
        headers.put("Content-Type", mediaType);
        return this;
    }

    public ResponseBuilder withLatency(Duration latency) {
        this.latency = latency;
        return this;
    }

    public Response generateResponse() {
        try {
            sleep(latency.toMillis());
            return new Response(statusCode, responseCallback.call(), headers);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create the response body", e);
        }
    }

    @Override
    public String toString() {
        return "ResponseBuilder{" +
                "headers=" + headers +
                ", statusCode=" + statusCode +
                ", responseCallback=" + responseCallback +
                ", latency=" + latency +
                '}';
    }

}
