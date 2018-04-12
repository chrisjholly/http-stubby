package uk.staygrounded.httpstubby.matchers.request;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.contains;

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
        contains(headerName).describeTo(description);
    }

}
