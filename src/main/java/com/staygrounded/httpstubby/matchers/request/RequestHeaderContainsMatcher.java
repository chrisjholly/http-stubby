package com.staygrounded.httpstubby.matchers.request;

import com.staygrounded.httpstubby.request.HttpRequest;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

public class RequestHeaderContainsMatcher extends TypeSafeMatcher<HttpRequest> {

    private final String headerName;
    private final String headerValue;

    private RequestHeaderContainsMatcher(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    public static RequestHeaderContainsMatcher requestHeaderContains(final String headerName, final String headerValue) {
        return new RequestHeaderContainsMatcher(headerName, headerValue);
    }

    @Override
    protected boolean matchesSafely(HttpRequest httpRequest) {
        return Objects.equals(headerValue, httpRequest.getRequestHeaders().get(headerName));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches request with header '" + headerName + ": " + headerValue + "'");
    }

}
