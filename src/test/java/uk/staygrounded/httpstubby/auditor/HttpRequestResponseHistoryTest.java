package uk.staygrounded.httpstubby.auditor;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import uk.staygrounded.httpstubby.server.response.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class HttpRequestResponseHistoryTest {

    private HttpRequestResponseHistory underTest = new HttpRequestResponseHistory();

    @Test
    public void lastRequestReturnsNullWhenNoRequestsMade() throws Exception {
        assertNull(underTest.lastRequest());
    }

    @Test
    public void lastResponseReturnsNullWhenNoResponsesSent() throws Exception {
        assertNull(underTest.lastResponse());
    }

    @Test
    public void lastRequestIsReturnedMultipleTimes() throws Exception {
        final HttpRequest request = mock(HttpRequest.class);

        underTest.newRequest(request);

        assertEquals(request, underTest.lastRequest());
        assertEquals(request, underTest.lastRequest());
    }

    @Test
    public void lastResponseIsReturnedMultipleTimes() throws Exception {
        final HttpResponse httpResponse = mock(HttpResponse.class);

        underTest.newResponse(httpResponse);

        Assert.assertEquals(httpResponse, underTest.lastResponse());
        Assert.assertEquals(httpResponse, underTest.lastResponse());
    }

    @Test
    public void retrievesLatestRequest() throws Exception {
        final HttpRequest olderRequest = mock(HttpRequest.class);
        final HttpRequest newerRequest = mock(HttpRequest.class);

        underTest.newRequest(olderRequest);
        underTest.newRequest(newerRequest);

        assertEquals(newerRequest, underTest.lastRequest());
    }

    @Test
    public void retrievesLatestResponse() throws Exception {
        final HttpResponse olderHttpResponse = mock(HttpResponse.class);
        final HttpResponse newerHttpResponse = mock(HttpResponse.class);

        underTest.newResponse(olderHttpResponse);
        underTest.newResponse(newerHttpResponse);

        Assert.assertEquals(newerHttpResponse, underTest.lastResponse());
    }

    @Test
    public void lastRequestAndResponseAreNullAfterClearingRequestAndResponses() throws Exception {
        final HttpRequest request = mock(HttpRequest.class);
        final HttpResponse httpResponse = mock(HttpResponse.class);

        underTest.newRequest(request);
        underTest.newResponse(httpResponse);
        underTest.clearHttpRequestAndResponses();

        assertNull(underTest.lastRequest());
        assertNull(underTest.lastResponse());
    }

}
