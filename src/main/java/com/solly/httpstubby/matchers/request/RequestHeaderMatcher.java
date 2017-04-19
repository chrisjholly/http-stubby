package com.solly.httpstubby.matchers.request;

import com.solly.httpstubby.request.HttpRequest;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestHeaderMatcher extends TypeSafeMatcher<HttpRequest> {

    private final Map<String, String> expectedHeaders;

    private RequestHeaderMatcher(Map<String, String> expectedHeaders) {
        this.expectedHeaders = expectedHeaders;
    }

    public static RequestHeaderMatcher requestHeaderContains(final String headerName, final String headerValue) {
        return new RequestHeaderMatcher(new HashMap<String, String>() {{
            put(headerName, headerValue);
        }});
    }

    public static RequestHeaderMatcher requestHeaderContains(Map<String, String> expectedHeaders) {
        return new RequestHeaderMatcher(expectedHeaders);
    }

    @Override
    protected boolean matchesSafely(HttpRequest httpRequest) {
        for (Map.Entry<String, String> expectedHeader : expectedHeaders.entrySet()) {
            final String requestHeaderValue = httpRequest.getRequestHeaders().get(expectedHeader.getKey());
            if (!Objects.equals(expectedHeader.getValue(), requestHeaderValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("matches request with headers '" + expectedHeaders + "'");
    }

}
