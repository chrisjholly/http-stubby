package com.staygrounded.httpstubby.handler;

import com.staygrounded.httpstubby.history.History;
import com.staygrounded.httpstubby.request.HttpRequest;
import com.staygrounded.httpstubby.request.HttpRequestFactory;
import com.staygrounded.httpstubby.response.HttpResponseSelector;
import com.staygrounded.httpstubby.response.Response;
import com.staygrounded.httpstubby.response.ResponseExecutor;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import static com.staygrounded.httpstubby.response.HttpStatus.Code.OK;
import static com.staygrounded.httpstubby.response.Response.aResponseWithStatusCode;
import static org.mockito.Mockito.*;

public class HttpRequestHandlerTest {

    private static final History history = mock(History.class);
    private HttpRequestFactory httpRequestFactory = mock(HttpRequestFactory.class);
    private HttpRequest httpRequest = mock(HttpRequest.class);
    private HttpResponseSelector responseSelector = mock(HttpResponseSelector.class);
    private HttpExchange httpExchange = mock(HttpExchange.class);
    private ResponseExecutor responseExecutor = mock(ResponseExecutor.class);
    private RequestResponseHandlerListener requestResponseHandlerListener = mock(RequestResponseHandlerListener.class);

    private HttpRequestHandler underTest;

    @Before
    public void setUp() throws Exception {
        when(httpRequestFactory.createHttpRequest(httpExchange)).thenReturn(httpRequest);
        when(httpExchange.getRequestURI()).thenReturn(new URI("http://localhost/some-uri"));
        when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        when(httpExchange.getResponseBody()).thenReturn(mock(OutputStream.class));

        underTest = new HttpRequestHandler(httpRequestFactory, responseExecutor, responseSelector, history, requestResponseHandlerListener);
    }

    @Test
    public void sendsResponse() throws IOException {
        Response response = aResponseWithStatusCode(OK);

        when(responseSelector.selectResponse(httpRequest)).thenReturn(response);

        underTest.handle(httpExchange);

        verify(history, times(1)).newRequest(httpRequest);
        verify(requestResponseHandlerListener, times(1)).newRequest(httpRequest);

        verify(history, times(1)).newResponse(response);
        verify(responseExecutor, times(1)).sendResponse(httpExchange, response);
        verify(requestResponseHandlerListener, times(1)).newResponse(response);

        verify(httpExchange, times(1)).close();
    }

}
