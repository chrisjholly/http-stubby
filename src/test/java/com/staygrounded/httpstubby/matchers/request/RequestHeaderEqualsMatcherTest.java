package com.staygrounded.httpstubby.matchers.request;

import com.staygrounded.httpstubby.server.request.HttpRequest;
import com.sun.net.httpserver.Headers;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.staygrounded.httpstubby.matchers.request.RequestHeaderEqualsMatcher.requestHeaderContains;
import static com.staygrounded.httpstubby.server.request.HttpMethod.GET;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class RequestHeaderEqualsMatcherTest {

    @Test
    public void returnsTrueWhenHttpRequestContainsMatchingHeader() throws URISyntaxException, IOException {
        final Headers actualHeaders = new Headers();
        actualHeaders.add("Some-header-name", "some-header-value");
        actualHeaders.add("aAother-header-name", "another-header-value");

        final HttpRequest actualHttpRequest = createHttpRequest(actualHeaders);

        assertTrue(requestHeaderContains("Some-header-name", "some-header-value").matchesSafely(actualHttpRequest));
    }

    @Test
    public void returnsFalseWhenHttpRequestDoesNotContainMatchingHeaderName() throws IOException, URISyntaxException {
        final Headers actualHeaders = new Headers();
        actualHeaders.add("Some-header-name", "some-header-value");

        final HttpRequest actualHttpRequest = createHttpRequest(actualHeaders);

        assertFalse(requestHeaderContains("Another-header-name", "some-header-value").matchesSafely(actualHttpRequest));
    }

    @Test
    public void returnsFalseWhenHttpRequestDoesNotContainMatchingHeaderValue() throws IOException, URISyntaxException {
        final Headers actualHeaders = new Headers();
        actualHeaders.add("Some-header-name", "some-header-value");

        final HttpRequest actualHttpRequest = createHttpRequest(actualHeaders);

        assertFalse(requestHeaderContains("Some-header-name", "another-header-value").matchesSafely(actualHttpRequest));
    }

    @Test
    public void returnsFalseWhenHttpRequestDoesNotContainMatchingHeader() throws IOException, URISyntaxException {

        final HttpRequest actualHttpRequest = createHttpRequest(new Headers());

        assertFalse(requestHeaderContains("Some-header-name", "some-header-value").matchesSafely(actualHttpRequest));
    }

    private HttpRequest createHttpRequest(Headers headers) throws URISyntaxException, IOException {
        return HttpRequest.createHttpRequestWith(GET, new URI("/some-uri"), headers);
    }

}
