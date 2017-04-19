package com.solly.httpstubby.matchers.response;

import com.solly.httpstubby.response.HttpStatus;
import com.solly.httpstubby.response.Response;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

public class ResponseStatusCodeMatcher extends TypeSafeMatcher<Response> {

    private final HttpStatus.Code statusCode;

    private ResponseStatusCodeMatcher(HttpStatus.Code statusCode) {
        this.statusCode = statusCode;
    }

    public static ResponseStatusCodeMatcher withStatusCode(HttpStatus.Code statusCode) {
        return new ResponseStatusCodeMatcher(statusCode);
    }

    protected boolean matchesSafely(Response response) {
        return equalTo(statusCode).matches(response.getStatusCode());
    }

    @Override
    protected void describeMismatchSafely(Response response, Description mismatchDescription) {
        equalTo(statusCode).describeMismatch(response.getStatusCode(), mismatchDescription);
    }

    @Override
    public void describeTo(Description description) {
        equalTo(statusCode).describeTo(description);
    }

}

