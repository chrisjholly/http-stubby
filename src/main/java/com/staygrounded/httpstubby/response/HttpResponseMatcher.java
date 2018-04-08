package com.staygrounded.httpstubby.response;

import com.staygrounded.httpstubby.request.HttpRequest;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.staygrounded.httpstubby.response.HttpStatus.Code.NOT_FOUND;
import static com.staygrounded.httpstubby.response.HttpResponseBuilder.responseOf;

public class HttpResponseMatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseMatcher.class);

    private HttpResponseBuilder defaultResponse = responseOf(NOT_FOUND);
    private final Map<Matcher<HttpRequest>, HttpResponseBuilder> requestResponseMappings = new LinkedHashMap<>();

    public void addResponse(Matcher<HttpRequest> matcher, HttpResponseBuilder httpResponseBuilder) {
        requestResponseMappings.put(matcher, httpResponseBuilder);
    }

    public void setDefaultResponse(HttpResponseBuilder defaultHttpResponseBuilder) {
        this.defaultResponse = defaultHttpResponseBuilder;
    }

    public void clearResponses() {
        requestResponseMappings.clear();
    }

    public HttpResponse matchHttpRequestToHttpResponse(HttpRequest httpRequest) {
        LOGGER.info("Attempting to find matcher for request: {}", httpRequest);

        for (Matcher<HttpRequest> matcher : requestResponseMappings.keySet()) {
            if (matcher.matches(httpRequest)) {
                return requestResponseMappings.get(matcher).build();
            }
        }

        LOGGER.info("No response found. Using default response: {}", defaultResponse.toString());
        return defaultResponse.build();
    }

}
