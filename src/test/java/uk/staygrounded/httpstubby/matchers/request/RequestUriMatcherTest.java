package uk.staygrounded.httpstubby.matchers.request;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static uk.staygrounded.httpstubby.matchers.request.RequestUriMatcher.*;
import static junit.framework.TestCase.assertTrue;

public class RequestUriMatcherTest {

    @Test
    public void startsWith() throws Exception {
        assertTrue(uriStartsWith("some").matchesSafely(aRequestWithUri(URI.create("some-uri"))));
    }

    @Test
    public void contains() throws Exception {
        assertTrue(uriContains("me-ur").matchesSafely(aRequestWithUri(URI.create("some-uri"))));
    }

    @Test
    public void endsWith() throws Exception {
        assertTrue(uriEndsWith("uri").matchesSafely(aRequestWithUri(URI.create("some-uri"))));
    }

    @Test
    public void equalTo() throws Exception {
        assertTrue(uriEqualTo("some-uri").matchesSafely(aRequestWithUri(URI.create("some-uri"))));
    }

    @Test
    public void equalToIgnoringCase() throws Exception {
        assertTrue(uriEqualToIgnoringCase("soME-Uri").matchesSafely(aRequestWithUri(URI.create("some-uri"))));
    }

    private HttpRequest aRequestWithUri(URI uri) throws URISyntaxException {
        return HttpRequest.createHttpRequestWith(null, uri);
    }

}
