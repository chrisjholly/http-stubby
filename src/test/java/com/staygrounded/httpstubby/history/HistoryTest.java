package com.staygrounded.httpstubby.history;

import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.response.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HistoryTest {

    private static final String REQUEST_URI = "/some-uri";
    private static final String REQUEST_BODY = "some-request-body";

    private History underTest = new History();

    @Test
    public void lastRequestNullWhenNoRequestsMade() throws Exception {
        assertNull(underTest.lastRequest());
    }

    @Test
    public void lastRequestUriNullWhenNoRequestsMade() throws Exception {
        assertNull(underTest.lastRequestUri());
    }

    @Test
    public void lastRequestBodyNullWhenNoRequestsMade() throws Exception {
        assertNull(underTest.lastRequestBody());
    }

    @Test
    public void lastRequestHeadersNullWhenNoRequestsMade() throws Exception {
        assertNull(underTest.lastRequestHeaders());
    }

    @Test
    public void lastResponseNullWhenNoResponsesSent() throws Exception {
        assertNull(underTest.lastResponse());
    }

    @Test
    public void returnsLastRequestUri() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getRequestUri()).thenReturn(new URI(REQUEST_URI));

        underTest.newRequest(request);
        assertEquals(REQUEST_URI, underTest.lastRequestUri());
    }

    @Test
    public void returnsLastRequestBody() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getRequestBody()).thenReturn(REQUEST_BODY);

        underTest.newRequest(request);
        assertEquals(REQUEST_BODY, underTest.lastRequestBody());
    }

    @Test
    public void lastRequestCanBeCalledMultipleTimes() throws Exception {
        HttpRequest request = mock(HttpRequest.class);

        underTest.newRequest(request);

        assertEquals(request, underTest.lastRequest());
        assertEquals(request, underTest.lastRequest());
    }

    @Test
    public void lastResponseCanBeCalledMultipleTimes() throws Exception {
        HttpResponse httpResponse = mock(HttpResponse.class);

        underTest.newResponse(httpResponse);

        Assert.assertEquals(httpResponse, underTest.lastResponse());
        Assert.assertEquals(httpResponse, underTest.lastResponse());
    }

    @Test
    public void retrievesLatestRequest() throws Exception {
        HttpRequest olderRequest = mock(HttpRequest.class);
        HttpRequest newerRequest = mock(HttpRequest.class);

        underTest.newRequest(olderRequest);
        underTest.newRequest(newerRequest);

        assertEquals(newerRequest, underTest.lastRequest());
    }

    @Test
    public void retrievesLatestResponse() throws Exception {
        HttpResponse olderHttpResponse = mock(HttpResponse.class);
        HttpResponse newerHttpResponse = mock(HttpResponse.class);

        underTest.newResponse(olderHttpResponse);
        underTest.newResponse(newerHttpResponse);

        Assert.assertEquals(newerHttpResponse, underTest.lastResponse());
    }

    @Test
    public void lastRequestAndResponseAreNullAfterClearingRequestHistory() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse httpResponse = mock(HttpResponse.class);

        underTest.newRequest(request);
        underTest.newResponse(httpResponse);
        underTest.clear();

        assertNull(underTest.lastRequest());
        assertNull(underTest.lastResponse());
    }

    @Test
    public void nullResponsesDoNotGetAddedToHistory() {
        underTest.newResponse(null);

        assertNull(underTest.lastResponse());
    }

}
