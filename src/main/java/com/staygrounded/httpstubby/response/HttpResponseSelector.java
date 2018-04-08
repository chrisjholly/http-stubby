package com.staygrounded.httpstubby.response;

import com.staygrounded.httpstubby.request.HttpRequest;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.staygrounded.httpstubby.response.HttpStatus.Code.NOT_FOUND;
import static com.staygrounded.httpstubby.response.Response.aResponseWithStatusCode;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class HttpResponseSelector {
    private static final Logger LOG = LoggerFactory.getLogger(HttpResponseSelector.class);

    private final Map<Matcher<HttpRequest>, ResponseBuilder> requestResponseMappings = new LinkedHashMap<>();
    private Optional<ResponseBuilder> defaultResponse = empty();

    public static HttpResponseSelector stubbableResponseSelector() {
        return new HttpResponseSelector();
    }

    public void addResponse(Matcher<HttpRequest> matcher, ResponseBuilder responseBuilder) {
        requestResponseMappings.put(matcher, responseBuilder);
    }

    public void setDefaultResponse(ResponseBuilder defaultResponseBuilder) {
        this.defaultResponse = ofNullable(defaultResponseBuilder);
    }

    public void clearResponses() {
        requestResponseMappings.clear();
    }

    public Response selectResponse(HttpRequest httpRequest) {
        LOG.info("Attempting to find matcher for request: {}", httpRequest);

        for (Matcher<HttpRequest> matcher : requestResponseMappings.keySet()) {
            if (matcher.matches(httpRequest)) {
                LOG.info("Request matcher found");
                return requestResponseMappings.get(matcher).generateResponse();
            }
        }

        return defaultResponse.map(ResponseBuilder::generateResponse)
                .orElse(aResponseWithStatusCode(NOT_FOUND));
    }

}
