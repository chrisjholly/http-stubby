package com.staygrounded.httpstubby.matchers.response;

import com.staygrounded.httpstubby.server.response.HttpResponse;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

public class ResponseHeaderEqualsMatcher extends TypeSafeMatcher<HttpResponse> {

    private final String headerName;
    private final String headerValue;

    private ResponseHeaderEqualsMatcher(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    public static ResponseHeaderEqualsMatcher responseHeaderContains(final String headerName, final String headerValue) {
        return new ResponseHeaderEqualsMatcher(headerName, headerValue);
    }

    @Override
    protected boolean matchesSafely(HttpResponse httpResponse) {
        return Objects.equals(httpResponse.getHeaders().get(headerName), headerValue);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("should contain header:");
        description.appendText(headerName);
        description.appendText("-");
        description.appendText(headerValue);
    }

}

