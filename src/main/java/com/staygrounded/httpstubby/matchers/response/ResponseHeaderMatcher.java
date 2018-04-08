package com.staygrounded.httpstubby.matchers.response;

import com.staygrounded.httpstubby.response.Response;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;

public class ResponseHeaderMatcher extends TypeSafeMatcher<Response> {

    private final Map<String, String> expectedHeaders;

    private ResponseHeaderMatcher(Map<String, String> expectedHeaders) {
        this.expectedHeaders = expectedHeaders;
    }

    public static ResponseHeaderMatcher responseHeaderContains(final String headerName, final String headerValue) {
        return new ResponseHeaderMatcher(new HashMap<String, String>() {{
            put(headerName, headerValue);
        }});
    }

    public static ResponseHeaderMatcher responseHeaderContains(Map<String, String> expectedHeaders) {
        return new ResponseHeaderMatcher(expectedHeaders);
    }

    @Override
    protected boolean matchesSafely(Response response) {
        for (Map.Entry<String, String> expectedHeader : expectedHeaders.entrySet()) {
            final String requestHeaderValue = response.getHeaders().get(expectedHeader.getKey());
            if (!Objects.equals(expectedHeader.getValue(), requestHeaderValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        equalTo(expectedHeaders).describeTo(description);
    }

}

