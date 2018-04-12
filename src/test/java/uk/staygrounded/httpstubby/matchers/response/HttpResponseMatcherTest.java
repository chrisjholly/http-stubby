package uk.staygrounded.httpstubby.matchers.response;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import uk.staygrounded.httpstubby.server.response.HttpResponse;
import uk.staygrounded.httpstubby.server.response.HttpResponseBuilder;
import uk.staygrounded.httpstubby.server.response.HttpResponseMatcher;
import org.hamcrest.core.Is;
import org.junit.Test;
import uk.staygrounded.httpstubby.matchers.request.RequestUriMatcher;
import uk.staygrounded.httpstubby.server.request.HttpMethod;
import uk.staygrounded.httpstubby.server.response.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;

import static uk.staygrounded.httpstubby.server.request.HttpRequest.createHttpRequestWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpResponseMatcherTest {

    private final HttpResponseMatcher underTest = new HttpResponseMatcher();

    @Test
    public void selectsFirstMatchingResponse() throws URISyntaxException {
        final HttpResponse expectedHttpResponse = mock(HttpResponse.class);
        final HttpResponse unexpectedHttpResponse = mock(HttpResponse.class);

        underTest.addResponse(RequestUriMatcher.uriEqualTo("/another-url"), createResponseBuilder(unexpectedHttpResponse));
        underTest.addResponse(RequestUriMatcher.uriEqualTo("/some-url"), createResponseBuilder(expectedHttpResponse));
        underTest.addResponse(RequestUriMatcher.uriStartsWith("/some-url"), createResponseBuilder(unexpectedHttpResponse));

        final HttpRequest httpRequest = HttpRequest.createHttpRequestWith(HttpMethod.GET, URI.create("/some-url"));
        final HttpResponse actualHttpResponse = underTest.findHttpResponseFromHttpRequest(httpRequest);

        assertEquals(expectedHttpResponse, actualHttpResponse);
    }

    @Test
    public void returns404NotFoundWhenNoMatchingResponse() throws URISyntaxException {
        final HttpResponse unexpectedHttpResponse = mock(HttpResponse.class);

        underTest.addResponse(RequestUriMatcher.uriEqualTo("/another-url"), createResponseBuilder(unexpectedHttpResponse));

        final HttpRequest httpRequest = HttpRequest.createHttpRequestWith(HttpMethod.GET, URI.create("/some-url"));
        final HttpResponse actualHttpResponse = underTest.findHttpResponseFromHttpRequest(httpRequest);

        assertThat(actualHttpResponse.getStatusCode(), Is.is(HttpStatus.Code.NOT_FOUND.getCode()));
    }

    @Test
    public void returnDefaultResponseWhenNoMatchingResponseFound() throws URISyntaxException {
        final HttpResponse expectedHttpResponse = mock(HttpResponse.class);

        underTest.setDefaultResponse(createResponseBuilder(expectedHttpResponse));

        final HttpRequest httpRequest = HttpRequest.createHttpRequestWith(HttpMethod.GET, URI.create("/some-url"));
        final HttpResponse actualHttpResponse = underTest.findHttpResponseFromHttpRequest(httpRequest);

        assertEquals(expectedHttpResponse, actualHttpResponse);
    }

    @Test
    public void doNotReturnDefaultWhenMatchingResponseFound() throws URISyntaxException {
        final HttpResponse expectedHttpResponse = mock(HttpResponse.class);
        final HttpResponse unexpectedHttpResponse = mock(HttpResponse.class);

        underTest.addResponse(RequestUriMatcher.uriEqualTo("/some-url"), createResponseBuilder(expectedHttpResponse));
        underTest.setDefaultResponse(createResponseBuilder(unexpectedHttpResponse));

        final HttpRequest httpRequest = HttpRequest.createHttpRequestWith(HttpMethod.GET, URI.create("/some-url"));
        final HttpResponse actualHttpResponse = underTest.findHttpResponseFromHttpRequest(httpRequest);

        assertEquals(expectedHttpResponse, actualHttpResponse);
    }

    private HttpResponseBuilder createResponseBuilder(final HttpResponse httpResponse) {
        final HttpResponseBuilder mockHttpResponseBuilder = mock(HttpResponseBuilder.class);
        when(mockHttpResponseBuilder.build()).thenReturn(httpResponse);
        return mockHttpResponseBuilder;
    }

}
