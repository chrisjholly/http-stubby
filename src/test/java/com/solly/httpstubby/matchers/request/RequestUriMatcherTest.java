package com.solly.httpstubby.matchers.request;

import com.solly.httpstubby.request.HttpRequest;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.solly.httpstubby.matchers.request.RequestUriMatcher.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class RequestUriMatcherTest {

    @Test
    public void startsWith() throws Exception {
        assertThat(aRequestWithUri("some-url"), uriStartsWith("some"));
    }

    @Test
    public void contains() throws Exception {
        assertThat(aRequestWithUri("some-url"), uriContains("me-ur"));
    }

    @Test
    public void endsWith() throws Exception {
        assertThat(aRequestWithUri("some-url"), uriEndsWith("url"));
    }

    @Test
    public void equalTo() throws Exception {
        assertThat(aRequestWithUri("some-url"), uriEqualTo("some-url"));
    }

    @Test
    public void equalToIgnoringCase() throws Exception {
        assertThat(aRequestWithUri("some-url"), uriEqualToIgnoringCase("soME-URl"));
    }


    private HttpRequest aRequestWithUri(String uri) throws URISyntaxException {
        return new HttpRequest(null, new URI(uri), null);
    }

}
