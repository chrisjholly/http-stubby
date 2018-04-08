package com.staygrounded.httpstubby.matchers.request;

import com.staygrounded.httpstubby.server.request.HttpRequest;
import com.staygrounded.httpstubby.server.request.HttpMethod;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static com.staygrounded.httpstubby.server.request.HttpMethod.*;
import static org.hamcrest.Matchers.equalTo;

public class RequestMethodMatcher extends TypeSafeMatcher<HttpRequest> {

    private final HttpMethod expectedMethod;

    private RequestMethodMatcher(HttpMethod expectedMethod) {
        this.expectedMethod = expectedMethod;
    }

    public static RequestMethodMatcher forARequestWithMethod(HttpMethod requestHttpMethod) {
        return new RequestMethodMatcher(requestHttpMethod);
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
    protected void describeMismatchSafely(HttpRequest item, Description mismatchDescription) {
        equalTo(expectedMethod).describeMismatch(item.getRequestMethod(), mismatchDescription);
    }

    @Override
    public void describeTo(Description description) {
        equalTo(expectedMethod).describeTo(description);
    }

}

