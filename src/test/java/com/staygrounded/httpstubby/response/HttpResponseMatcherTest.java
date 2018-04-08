package com.staygrounded.httpstubby.response;

import com.staygrounded.httpstubby.request.HttpRequest;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.staygrounded.httpstubby.matchers.request.RequestUriMatcher.uriEqualTo;
import static com.staygrounded.httpstubby.matchers.request.RequestUriMatcher.uriStartsWith;
import static com.staygrounded.httpstubby.request.HttpMethod.GET;
import static com.staygrounded.httpstubby.request.HttpRequest.createHttpRequestWith;
import static com.staygrounded.httpstubby.response.HttpStatus.Code.NOT_FOUND;
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

        underTest.addResponse(uriEqualTo("/another-url"), createResponseBuilder(unexpectedHttpResponse));
        underTest.addResponse(uriEqualTo("/some-url"), createResponseBuilder(expectedHttpResponse));
        underTest.addResponse(uriStartsWith("/some-url"), createResponseBuilder(unexpectedHttpResponse));

        final HttpRequest httpRequest = createHttpRequestWith(GET, URI.create("/some-url"));
        final HttpResponse actualHttpResponse = underTest.matchHttpRequestToHttpResponse(httpRequest);

        assertEquals(expectedHttpResponse, actualHttpResponse);
    }

    @Test
    public void returns404NotFoundWhenNoMatchingResponse() throws URISyntaxException {
        final HttpResponse unexpectedHttpResponse = mock(HttpResponse.class);

        underTest.addResponse(uriEqualTo("/another-url"), createResponseBuilder(unexpectedHttpResponse));

        final HttpRequest httpRequest = createHttpRequestWith(GET, URI.create("/some-url"));
        final HttpResponse actualHttpResponse = underTest.matchHttpRequestToHttpResponse(httpRequest);

        assertThat(actualHttpResponse.getStatusCode(), is(NOT_FOUND.getCode()));
    }

    @Test
    public void returnDefaultResponseWhenNoMatchingResponseFound() throws URISyntaxException {
        final HttpResponse expectedHttpResponse = mock(HttpResponse.class);

        underTest.setDefaultResponse(createResponseBuilder(expectedHttpResponse));

        final HttpRequest httpRequest = createHttpRequestWith(GET, URI.create("/some-url"));
        final HttpResponse actualHttpResponse = underTest.matchHttpRequestToHttpResponse(httpRequest);

        assertEquals(expectedHttpResponse, actualHttpResponse);
    }

    @Test
    public void doNotReturnDefaultWhenMatchingResponseFound() throws URISyntaxException {
        final HttpResponse expectedHttpResponse = mock(HttpResponse.class);
        final HttpResponse unexpectedHttpResponse = mock(HttpResponse.class);

        underTest.addResponse(uriEqualTo("/some-url"), createResponseBuilder(expectedHttpResponse));
        underTest.setDefaultResponse(createResponseBuilder(unexpectedHttpResponse));

        final HttpRequest httpRequest = createHttpRequestWith(GET, URI.create("/some-url"));
        final HttpResponse actualHttpResponse = underTest.matchHttpRequestToHttpResponse(httpRequest);

        assertEquals(expectedHttpResponse, actualHttpResponse);
    }

    private HttpResponseBuilder createResponseBuilder(final HttpResponse httpResponse) {
        final HttpResponseBuilder mockHttpResponseBuilder = mock(HttpResponseBuilder.class);
        when(mockHttpResponseBuilder.build()).thenReturn(httpResponse);
        return mockHttpResponseBuilder;
    }

}
