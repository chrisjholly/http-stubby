package com.staygrounded.httpstubby.matchers.request;

import com.staygrounded.httpstubby.request.HttpRequest;
import com.sun.net.httpserver.Headers;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.staygrounded.httpstubby.matchers.request.RequestHeaderExistsMatcher.requestHeaderExists;
import static org.junit.Assert.assertTrue;

public class RequestHeaderExistsMatcherTest {

    @Test
    public void requestTrueWhenHttpRequestContainsHeader() throws Exception {
        final Headers headers = new Headers();
        headers.add("Some-header-name", "some-header-value");

        assertTrue(requestHeaderExists("Some-header-name").matchesSafely(aRequestWithHeaders(headers)));
    }

    private HttpRequest aRequestWithHeaders(Headers headers) throws URISyntaxException {
        return HttpRequest.createHttpRequestWith(null, new URI("some-url"), headers);
    }

}
