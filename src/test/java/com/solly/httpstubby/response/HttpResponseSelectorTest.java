package com.solly.httpstubby.response;

import com.solly.httpstubby.request.HttpRequest;
import com.solly.httpstubby.request.HttpMethod;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.solly.httpstubby.request.HttpMethod.GET;
import static com.solly.httpstubby.matchers.request.RequestUriMatcher.uriEqualTo;
import static com.solly.httpstubby.matchers.request.RequestUriMatcher.uriStartsWith;
import static com.solly.httpstubby.response.HttpStatus.Code.NOT_FOUND;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpResponseSelectorTest {

    private final HttpResponseSelector underTest = HttpResponseSelector.stubbableResponseSelector();

    @Test
    public void selectsFirstMatchingResponse() throws URISyntaxException {
        Response expectedResponse = mock(Response.class);
        Response unexpectedResponse = mock(Response.class);

        underTest.addResponse(uriEqualTo("/another-url"), createResponseBuilder(unexpectedResponse));
        underTest.addResponse(uriEqualTo("/some-url"), createResponseBuilder(expectedResponse));
        underTest.addResponse(uriStartsWith("/some-url"), createResponseBuilder(unexpectedResponse));

        Response actualResponse = underTest.selectResponse(createHttpRequest(GET, "/some-url"));

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void returns404NotFoundWhenNoMatchingResponse() throws URISyntaxException {
        Response unexpectedResponse = mock(Response.class);

        underTest.addResponse(uriEqualTo("/another-url"), createResponseBuilder(unexpectedResponse));

        Response actualResponse = underTest.selectResponse(createHttpRequest(GET, "/some-url"));

        assertThat(actualResponse.getStatusCode(), is(NOT_FOUND));
    }

    @Test
    public void returnDefaultResopnseWhenNoMatchingResponseFound() throws URISyntaxException {
        Response expectedResponse = mock(Response.class);

        underTest.setDefaultResponse(createResponseBuilder(expectedResponse));

        Response actualResponse = underTest.selectResponse(createHttpRequest(GET, "/some-url"));

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void doNotReturnDefaultWhenMatchingResponseFound() throws URISyntaxException {
        Response expectedResponse = mock(Response.class);
        Response unexpectedResponse = mock(Response.class);

        underTest.addResponse(uriEqualTo("/some-url"), createResponseBuilder(expectedResponse));
        underTest.setDefaultResponse(createResponseBuilder(unexpectedResponse));

        Response actualResponse = underTest.selectResponse(createHttpRequest(GET, "/some-url"));

        assertEquals(expectedResponse, actualResponse);
    }

    private HttpRequest createHttpRequest(HttpMethod httpMethod, String uri) throws URISyntaxException {
        return new HttpRequest(httpMethod, new URI(uri), null);
    }

    private ResponseBuilder createResponseBuilder(final Response response) {
        ResponseBuilder mockResponseBuilder = mock(ResponseBuilder.class);
        when(mockResponseBuilder.generateResponse()).thenReturn(response);
        return mockResponseBuilder;
    }


}
