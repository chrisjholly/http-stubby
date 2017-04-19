package com.solly.httpstubby.matchers.request;

import com.solly.httpstubby.request.HttpRequest;
import com.sun.net.httpserver.Headers;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.solly.httpstubby.matchers.request.RequestHeaderExistsMatcher.requestHeaderDoesNotExist;
import static com.solly.httpstubby.matchers.request.RequestHeaderExistsMatcher.requestHeaderExists;
import static org.junit.Assert.assertTrue;

public class RequestHeaderExistsMatcherTest {

    @Test
    public void requestHeaderDoesNotExistReturnsTrueWhenMissing() throws Exception {
        assertTrue(requestHeaderDoesNotExist("Foo").matchesSafely(aRequestWithHeaders(new Headers())));
    }

    @Test
    public void requestHeaderExistsReturnsTrueWhenPresentWithNullValue() throws Exception {
        Headers headers = new Headers();
        headers.add("Foo", null);
        assertTrue(requestHeaderExists("Foo").matchesSafely(aRequestWithHeaders(headers)));
    }

    @Test
    public void requestHeadExistsReturnsTrueWhenPresentWithValue() throws Exception {
        Headers headers = new Headers();
        headers.add("Foo", "bar");
        assertTrue(requestHeaderExists("Foo").matchesSafely(aRequestWithHeaders(headers)));
    }

    private HttpRequest aRequestWithHeaders(Headers headers) throws URISyntaxException {
        return new HttpRequest(null, new URI("some-url"), null, headers);
    }

}
