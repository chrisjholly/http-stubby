package com.staygrounded.httpstubby.matchers.response;

import com.staygrounded.httpstubby.response.HttpResponse;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

public class ResponseHeaderMatcher extends TypeSafeMatcher<HttpResponse> {

    private final String headerName;
    private final String headerValue;

    private ResponseHeaderMatcher(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    public static ResponseHeaderMatcher responseHeaderContains(final String headerName, final String headerValue) {
        return new ResponseHeaderMatcher(headerName, headerValue);
    }

    @Override
    protected boolean matchesSafely(HttpResponse httpResponse) {

        if (httpResponse.getHeaders().containsKey(headerName)) {
            if (Objects.equals(httpResponse.getHeaders().get(headerName), headerValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("should contain header:");
        description.appendText(headerName);
        description.appendText("-");
        description.appendText(headerValue);
    }

}

