package uk.staygrounded.httpstubby.server.response;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static uk.staygrounded.httpstubby.server.response.HttpStatus.Code.NOT_FOUND;
import static uk.staygrounded.httpstubby.server.response.HttpResponseBuilder.responseOf;

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

    public HttpResponse findHttpResponseFromHttpRequest(HttpRequest httpRequest) {
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
