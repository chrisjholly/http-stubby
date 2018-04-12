package uk.staygrounded.httpstubby.server.response;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.lang.Thread.sleep;
import static java.time.Duration.ZERO;

public class HttpResponseBuilder {

    private final int statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private Callable<String> bodyCallback = () -> "";
    private Duration latency = ZERO;

    private HttpResponseBuilder(int statusCode) {
        this.statusCode = statusCode;
    }

    public static HttpResponseBuilder responseOf(HttpStatus.Code code) {
        return new HttpResponseBuilder(code.getCode());
    }

    public static HttpResponseBuilder responseOf(int code) {
        return new HttpResponseBuilder(code);
    }

    public HttpResponseBuilder withHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpResponseBuilder withBody(String body) {
        bodyCallback = () -> body;
        return this;
    }

    public HttpResponseBuilder withBody(Callable<String> body) {
        bodyCallback = body;
        return this;
    }

    public HttpResponseBuilder withContentType(String mediaType) {
        headers.put("Content-Type", mediaType);
        return this;
    }

    public HttpResponseBuilder withLatency(Duration latency) {
        this.latency = latency;
        return this;
    }

    public HttpResponse build() {
        try {
            sleep(latency.toMillis());
            return new HttpResponse(statusCode, bodyCallback.call(), headers);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create the response body", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
