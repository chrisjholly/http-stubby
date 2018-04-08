package com.staygrounded.httpstubby.matchers.request;

import com.staygrounded.httpstubby.request.HttpRequest;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class RequestHeaderExistsMatcher extends TypeSafeMatcher<HttpRequest> {

    private final String headerName;

    private RequestHeaderExistsMatcher(String headerName) {
        this.headerName = headerName;
    }

    public static RequestHeaderExistsMatcher requestHeaderExists(String header) {
        return new RequestHeaderExistsMatcher(header);
    }

    @Override
    protected boolean matchesSafely(HttpRequest httpRequest) {
        return httpRequest.getRequestHeaders().containsKey(headerName);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches request without header '" + headerName + "'");
    }

}
