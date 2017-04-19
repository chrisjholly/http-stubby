package com.solly.httpstubby.matchers.request;

import com.solly.httpstubby.request.HttpRequest;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class RequestHeaderExistsMatcher extends TypeSafeMatcher<HttpRequest> {

    private final String expectedMissingHeader;

    private RequestHeaderExistsMatcher(String expectedMissingHeader) {
        this.expectedMissingHeader = expectedMissingHeader;
    }

    public static RequestHeaderExistsMatcher requestHeaderExists(String header) {
        return new RequestHeaderExistsMatcher(header);
    }

    public static RequestHeaderExistsMatcher requestHeaderDoesNotExist(String header) {
        return new RequestHeaderExistsMatcher(header) {
            @Override
            protected boolean matchesSafely(HttpRequest httpRequest) {
                return !super.matchesSafely(httpRequest);
            }
        };
    }

    @Override
    protected boolean matchesSafely(HttpRequest httpRequest) {
        return httpRequest.getRequestHeaders().containsKey(expectedMissingHeader);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches request without header '" + expectedMissingHeader + "'");
    }

}
