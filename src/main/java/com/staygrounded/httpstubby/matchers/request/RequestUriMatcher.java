package com.staygrounded.httpstubby.matchers.request;

import com.staygrounded.httpstubby.request.HttpRequest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.*;

public class RequestUriMatcher extends TypeSafeMatcher<HttpRequest> {
    private final Matcher<String> uriMatcher;

    private RequestUriMatcher(Matcher<String> uriMatcher) {
        this.uriMatcher = uriMatcher;
    }

    public static RequestUriMatcher uriContains(String url) {
        return new RequestUriMatcher(containsString(url));
    }

    public static RequestUriMatcher uriStartsWith(String url) {
        return new RequestUriMatcher(startsWith(url));
    }

    public static RequestUriMatcher uriEqualTo(String url) {
        return new RequestUriMatcher(equalTo(url));
    }

    public static RequestUriMatcher uriEqualToIgnoringCase(String url) {
        return new RequestUriMatcher(equalToIgnoringCase(url));
    }

    public static RequestUriMatcher uriEndsWith(String url) {
        return new RequestUriMatcher(endsWith(url));
    }

    @Override
    protected boolean matchesSafely(HttpRequest httpRequest) {
        return uriMatcher.matches(httpRequest.getRequestUri().toASCIIString());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("request URI is ");
        uriMatcher.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(HttpRequest item, Description mismatchDescription) {
        uriMatcher.describeMismatch(item, mismatchDescription);
    }

}
