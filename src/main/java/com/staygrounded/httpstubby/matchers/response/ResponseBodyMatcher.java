package com.staygrounded.httpstubby.matchers.response;

import com.staygrounded.httpstubby.response.Response;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

public class ResponseBodyMatcher extends TypeSafeMatcher<Response> {

    private final String responseBody;

    private ResponseBodyMatcher(String responseBody) {
        this.responseBody = responseBody;
    }

    public static ResponseBodyMatcher withBody(String responseBody) {
        return new ResponseBodyMatcher(responseBody);
    }

    protected boolean matchesSafely(Response response) {
        return equalTo(responseBody).matches(response.getBodyAsString());
    }

    @Override
    protected void describeMismatchSafely(Response response, Description mismatchDescription) {
        equalTo(responseBody).describeMismatch(response.getBodyAsString(), mismatchDescription);
    }

    @Override
    public void describeTo(Description description) {
        equalTo(responseBody).describeTo(description);
    }

}

