package uk.staygrounded.httpstubby.matchers.response;

import uk.staygrounded.httpstubby.server.response.HttpResponse;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

public class ResponseBodyMatcher extends TypeSafeMatcher<HttpResponse> {

    private final String responseBody;

    private ResponseBodyMatcher(String responseBody) {
        this.responseBody = responseBody;
    }

    public static ResponseBodyMatcher withBody(String responseBody) {
        return new ResponseBodyMatcher(responseBody);
    }

    protected boolean matchesSafely(HttpResponse httpResponse) {
        return equalTo(responseBody).matches(httpResponse.getBodyAsString());
    }

    @Override
    public void describeTo(Description description) {
        equalTo(responseBody).describeTo(description);
    }

}

