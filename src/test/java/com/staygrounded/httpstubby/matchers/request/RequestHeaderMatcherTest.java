package com.staygrounded.httpstubby.matchers.request;

import com.staygrounded.httpstubby.request.HttpRequest;
import com.sun.net.httpserver.Headers;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.staygrounded.httpstubby.request.HttpMethod.GET;
import static com.staygrounded.httpstubby.matchers.request.RequestHeaderMatcher.requestHeaderContains;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class RequestHeaderMatcherTest {

    @Test
    public void returnsTrueWhenContainsMatchingHeader() throws URISyntaxException, IOException {
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("A", "1");
        expectedHeaders.put("C", "3");

        HttpRequest httpRequest = createHttpRequest(someHeaders());

        assertThat(httpRequest, requestHeaderContains(expectedHeaders));
    }

    @Test
    public void returnsTrueWhenContainsMatchingSingleHeader() throws IOException, URISyntaxException {
        assertThat(createHttpRequest(someHeaders()), requestHeaderContains("A", "1"));
    }

    @Test
    public void returnsFalseWhenDoesNotContainsMatchingSingleHeader() throws IOException, URISyntaxException {
        assertThat(createHttpRequest(someHeaders()), not(requestHeaderContains("A", "5")));
    }

    @Test
    public void returnsFalseWhenMissingExpectedHeader() throws URISyntaxException, IOException {
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("A", "1");
        expectedHeaders.put("D", "4");

        HttpRequest httpRequest = createHttpRequest(someHeaders());

        assertThat(httpRequest, not(requestHeaderContains(expectedHeaders)));
    }

    @Test
    public void returnsTrueNoHeadersExpectedOrFound() throws URISyntaxException, IOException {
        HttpRequest httpRequest = createHttpRequest(new Headers());

        assertThat(httpRequest, requestHeaderContains(new HashMap<>()));
    }

    private HttpRequest createHttpRequest(Headers headers) throws URISyntaxException, IOException {
        return new HttpRequest(GET, new URI("/some-uri"), "", headers);
    }

    private Headers someHeaders() {
        Headers headers = new Headers();
        headers.add("A", "1");
        headers.add("B", "2");
        headers.add("C", "3");
        return headers;
    }
}
