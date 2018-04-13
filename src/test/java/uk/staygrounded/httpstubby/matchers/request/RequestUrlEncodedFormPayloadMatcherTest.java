package uk.staygrounded.httpstubby.matchers.request;

import org.junit.Test;
import org.mockito.Mockito;
import uk.staygrounded.httpstubby.matchers.request.builder.UrlEncodedFormPayloadMatcherBuilder;
import uk.staygrounded.httpstubby.server.request.HttpRequest;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;
import static uk.staygrounded.httpstubby.matchers.request.RequestUrlEncodedFormPayloadMatcher.urlFormPayload;
import static uk.staygrounded.httpstubby.matchers.request.builder.UrlEncodedFormPayloadMatcherBuilder.aUrlFormMatcher;

/**
 * Created by chrisholly on 12/04/2018.
 */
public class RequestUrlEncodedFormPayloadMatcherTest {

    @Test
    public void returnsTrueWhenUrlFormPayloadContainsKey() throws Exception {
        final UrlEncodedFormPayloadMatcherBuilder mapMatcherBuilder = aUrlFormMatcher().hasKey("key1");
        final HttpRequest httpRequest = aRequestWithUrlFormPayload("key1=value1&key2=value2");

        assertTrue(urlFormPayload(mapMatcherBuilder).matchesSafely(httpRequest));
    }

    @Test
    public void returnsFalseWhenUrlFormPayloadDoesNotContainsKey() throws Exception {
        final UrlEncodedFormPayloadMatcherBuilder mapMatcherBuilder = aUrlFormMatcher().hasKey("key3");
        final HttpRequest httpRequest = aRequestWithUrlFormPayload("key1=value1&key2=value2");

        assertFalse(urlFormPayload(mapMatcherBuilder).matchesSafely(httpRequest));
    }

    @Test
    public void returnsTrueWhenUrlFormPayloadContainsEntry() throws Exception {
        final UrlEncodedFormPayloadMatcherBuilder mapMatcherBuilder = aUrlFormMatcher().withKeyAndValue("key1", "value1");
        final HttpRequest httpRequest = aRequestWithUrlFormPayload("key1=value1&key2=value2");

        assertTrue(urlFormPayload(mapMatcherBuilder).matchesSafely(httpRequest));
    }

    @Test
    public void returnsFalseWhenUrlFormPayloadDoesNotContainsEntry() throws Exception {
        final UrlEncodedFormPayloadMatcherBuilder mapMatcherBuilder = aUrlFormMatcher().withKeyAndValue("key3", "value3");
        final HttpRequest httpRequest = aRequestWithUrlFormPayload("key1=value1&key2=value2");

        assertFalse(urlFormPayload(mapMatcherBuilder).matchesSafely(httpRequest));
    }

    @Test
    public void returnsTrueWhenUrlFormPayloadContainsSpecialCharacters() throws Exception {
        final UrlEncodedFormPayloadMatcherBuilder mapMatcherBuilder = aUrlFormMatcher().withKeyAndValue("ABC#$%&123", "123#$%&ABC");
        final HttpRequest httpRequest = aRequestWithUrlFormPayload("ABC%23%24%25%26123=123%23%24%25%26ABC");

        assertTrue(urlFormPayload(mapMatcherBuilder).matchesSafely(httpRequest));
    }

    private HttpRequest aRequestWithUrlFormPayload(String urlFormPayload) {
        final HttpRequest httpRequestMock = Mockito.mock(HttpRequest.class);
        when(httpRequestMock.getRequestBody()).thenReturn(urlFormPayload);
        return httpRequestMock;
    }

}