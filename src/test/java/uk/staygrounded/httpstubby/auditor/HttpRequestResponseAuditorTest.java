package uk.staygrounded.httpstubby.auditor;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import uk.staygrounded.httpstubby.server.response.HttpResponse;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created by chrisholly on 08/04/2018.
 */
public class HttpRequestResponseAuditorTest {

    private final HttpRequestResponseHistory httpRequestResponseHistory = mock(HttpRequestResponseHistory.class);
    private final HttpRequestResponseEventListener httpRequestResponseEventListener1 = mock(HttpRequestResponseEventListener.class);
    private final HttpRequestResponseEventListener httpRequestResponseEventListener2 = mock(HttpRequestResponseEventListener.class);

    private final HttpRequestResponseAuditor underTest = new HttpRequestResponseAuditor(httpRequestResponseHistory);

    @Test
    public void invokesMultipleEventListenersWhenNewRequestIsExecuted() throws Exception {
        final HttpRequest httpRequestMock = mock(HttpRequest.class);

        underTest.registerHttpRequestResponseEventListener(httpRequestResponseEventListener1);
        underTest.registerHttpRequestResponseEventListener(httpRequestResponseEventListener2);

        underTest.newRequest(httpRequestMock);

        verify(httpRequestResponseHistory, times(1)).newRequest(httpRequestMock);
        verify(httpRequestResponseEventListener1, times(1)).newRequest(httpRequestMock);
        verify(httpRequestResponseEventListener2, times(1)).newRequest(httpRequestMock);
    }

    @Test
    public void auditsRequestWithoutAnyEventListeners() throws Exception {
        final HttpRequest httpRequestMock = mock(HttpRequest.class);

        underTest.newRequest(httpRequestMock);

        verify(httpRequestResponseHistory, times(1)).newRequest(httpRequestMock);
    }

    @Test
    public void invokesMultipleEventListenersWhenNewResponseIsExecuted() throws Exception {
        final HttpResponse httpResponseMock = mock(HttpResponse.class);

        underTest.registerHttpRequestResponseEventListener(httpRequestResponseEventListener1);
        underTest.registerHttpRequestResponseEventListener(httpRequestResponseEventListener2);

        underTest.newResponse(httpResponseMock);

        verify(httpRequestResponseHistory, times(1)).newResponse(httpResponseMock);
        verify(httpRequestResponseEventListener1, times(1)).newResponse(httpResponseMock);
        verify(httpRequestResponseEventListener2, times(1)).newResponse(httpResponseMock);
    }

    @Test
    public void auditsResponseWithoutAnyEventListeners() throws Exception {
        final HttpResponse httpResponseMock = mock(HttpResponse.class);

        underTest.newResponse(httpResponseMock);

        verify(httpRequestResponseHistory, times(1)).newResponse(httpResponseMock);
    }

}