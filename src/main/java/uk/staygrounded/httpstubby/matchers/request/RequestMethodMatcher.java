package uk.staygrounded.httpstubby.matchers.request;

import uk.staygrounded.httpstubby.server.request.HttpMethod;
import uk.staygrounded.httpstubby.server.request.HttpRequest;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static uk.staygrounded.httpstubby.server.request.HttpMethod.*;
import static org.hamcrest.Matchers.equalTo;

public class RequestMethodMatcher extends TypeSafeMatcher<HttpRequest> {

    private final HttpMethod expectedMethod;

    private RequestMethodMatcher(HttpMethod expectedMethod) {
        this.expectedMethod = expectedMethod;
    }

    public static RequestMethodMatcher forAGetRequest() {
        return new RequestMethodMatcher(GET);
    }

    public static RequestMethodMatcher forAPostRequest() {
        return new RequestMethodMatcher(POST);
    }

    public static RequestMethodMatcher forAPutRequest() {
        return new RequestMethodMatcher(PUT);
    }

    public static RequestMethodMatcher forADeleteRequest() {
        return new RequestMethodMatcher(DELETE);
    }

    public static RequestMethodMatcher forAHeadRequest() {
        return new RequestMethodMatcher(HEAD);
    }

    protected boolean matchesSafely(HttpRequest httpRequest) {
        return equalTo(expectedMethod).matches(httpRequest.getRequestMethod());
    }

    @Override
    public void describeTo(Description description) {
        equalTo(expectedMethod).describeTo(description);
    }

}

