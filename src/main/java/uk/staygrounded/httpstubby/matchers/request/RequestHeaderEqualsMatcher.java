package uk.staygrounded.httpstubby.matchers.request;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

public class RequestHeaderEqualsMatcher extends TypeSafeMatcher<HttpRequest> {

    private final String headerName;
    private final String headerValue;

    private RequestHeaderEqualsMatcher(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    public static RequestHeaderEqualsMatcher requestHeaderContains(final String headerName, final String headerValue) {
        return new RequestHeaderEqualsMatcher(headerName, headerValue);
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
