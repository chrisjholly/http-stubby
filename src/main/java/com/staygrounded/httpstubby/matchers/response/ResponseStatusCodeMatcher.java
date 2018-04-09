package com.staygrounded.httpstubby.matchers.response;

import com.staygrounded.httpstubby.server.response.HttpResponse;
import com.staygrounded.httpstubby.server.response.HttpStatus;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

public class ResponseStatusCodeMatcher extends TypeSafeMatcher<HttpResponse> {

    private final int statusCode;

    private ResponseStatusCodeMatcher(int statusCode) {
        this.statusCode = statusCode;
    }

    public static ResponseStatusCodeMatcher withStatusCode(int statusCode) {
        return new ResponseStatusCodeMatcher(statusCode);
    }

    public static ResponseStatusCodeMatcher withStatusCode(HttpStatus.Code statusCode) {
        return new ResponseStatusCodeMatcher(statusCode.getCode());
    }

    protected boolean matchesSafely(HttpResponse httpResponse) {
        return equalTo(statusCode).matches(httpResponse.getStatusCode());
    }

    @Override
    public void describeTo(Description description) {
        equalTo(statusCode).describeTo(description);
    }

}

