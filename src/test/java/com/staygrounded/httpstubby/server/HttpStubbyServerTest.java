package com.staygrounded.httpstubby.server;

import com.staygrounded.httpstubby.auditor.HttpRequestResponseEventListener;
import com.staygrounded.httpstubby.server.request.HttpRequest;
import com.staygrounded.httpstubby.server.ssl.SelfSignedSSLContextFactory;
import org.apache.commons.lang.time.StopWatch;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static com.staygrounded.httpstubby.matchers.request.RequestMethodMatcher.forAGetRequest;
import static com.staygrounded.httpstubby.matchers.request.RequestMethodMatcher.forAPostRequest;
import static com.staygrounded.httpstubby.matchers.request.RequestUriMatcher.*;
import static com.staygrounded.httpstubby.matchers.response.ResponseBodyMatcher.withBody;
import static com.staygrounded.httpstubby.matchers.response.ResponseStatusCodeMatcher.withStatusCode;
import static com.staygrounded.httpstubby.server.HttpPortNumberGenerator.nextAvailablePortNumber;
import static com.staygrounded.httpstubby.server.HttpServerFactory.httpConfiguration;
import static com.staygrounded.httpstubby.server.HttpStubbyServer.stubbyServerWith;
import static com.staygrounded.httpstubby.server.response.HttpResponseBuilder.responseOf;
import static com.staygrounded.httpstubby.server.response.HttpStatus.Code.NOT_FOUND;
import static com.staygrounded.httpstubby.server.response.HttpStatus.Code.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class HttpStubbyServerTest {

    private HttpStubbyServer underTest;

    @Test
    public void storesLastRequest() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        whenAHttpGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastRequest(), allOf(
                uriEqualTo("/some-url"),
                is(forAGetRequest())));
    }

    @Test
    public void storesLastResponse() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();
        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"));

        whenAHttpGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void returnsNotFoundDefaultServerResponseWhenNoResponsesArePrimed() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        whenAHttpGetRequestIsMadeFor("/url-for-a-get-request");

        then(underTest.httpRequestResponseHistory().lastResponse(), is(withStatusCode(NOT_FOUND)));
    }

    @Test
    public void serverReturnsConfiguredDefaultResponseWhenNoOtherResponsesFound() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturnWhenNoResponseFound(responseOf(NOT_FOUND).withBody("no responses match"));

        whenAHttpGetRequestIsMadeFor("/some-url-with-no-response-found");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(NOT_FOUND),
                withBody("no responses match")));
    }

    @Test
    public void returnsCorrectResponseWhenMultipleResponsesArePrimed() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-not-be-returned"), uriEqualTo("/url-not-to-be-called"));
        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"), uriEqualTo("/url-to-be-called"));

        whenAHttpGetRequestIsMadeFor("/url-to-be-called");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void returnFirstResponseWhenMultipleResponsesMatchRequest() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-should-be-returned"), uriEqualTo("/url"));
        underTest.willReturn(responseOf(OK).withBody("response-should-not-be-returned"), uriStartsWith("/url"));

        whenAHttpGetRequestIsMadeFor("/url");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-should-be-returned")));
    }

    @Test
    public void returnsCorrectResponseWhenMultipleMatchersArePrimed() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-for-post-request"), uriEndsWith("/url-to-test"), forAPostRequest());

        whenAHttpPostRequestIsMadeFor("/url-to-test");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-for-post-request")));
    }

    @Test
    public void returnsCorrectResponseForRequestWhichHasQueryParameters() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"), uriEqualTo("/response-uri-with-query-params?x=this-is-the-correct-request"));

        whenAHttpGetRequestIsMadeFor("/response-uri-with-query-params?x=this-is-the-correct-request");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void returnsResponseAfterConfiguredLatency() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        final StopWatch stopWatch = new StopWatch() {{
            start();
        }};

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned")
                .withLatency(Duration.ofSeconds(2)));

        whenAHttpGetRequestIsMadeFor("/some-uri");

        stopWatch.stop();

        assertTrue(Duration.ofMillis(stopWatch.getTime()).getSeconds() >= 2);
    }

    @Test
    public void returnsDifferentResponseOnSubsequentRequests() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody(new Callable<String>() {
            private final AtomicInteger uniqueNumber = new AtomicInteger(1);

            public String call() {
                return String.valueOf(uniqueNumber.getAndIncrement());
            }
        }));

        whenAHttpGetRequestIsMadeFor("/unique-number-generator");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("1")));

        whenAHttpGetRequestIsMadeFor("/unique-number-generator");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("2")));
    }

    @Test
    public void returnsResponseFromServerWithDefaultHttpsConfigurationServingHttpsRequests() throws Exception {
        underTest = stubbyServerWith(HttpServerFactory.httpsConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"));

        whenAHttpsGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void returnsResponseFromServerWithCustomHttpsConfigurationServingHttpsRequests() throws Exception {
        underTest = stubbyServerWith(HttpServerFactory.httpsConfiguration(nextAvailablePortNumber(), new SelfSignedSSLContextFactory()
                .createContext("/https-keystore.jks", "password")));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"));

        whenAHttpsGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void httpRequestResponseHistoryReturnsNullAfterRequestAndResponsesAreCleared() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        whenAHttpGetRequestIsMadeFor("/some-url");

        underTest.clearHistoryAndResponses();

        then(underTest.httpRequestResponseHistory().lastRequest(), is(nullValue()));
        then(underTest.httpRequestResponseHistory().lastResponse(), is(nullValue()));
    }

    @Test
    public void returnsDefaultServerResponseWhenPrimedResponsesAreCleared() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"));

        whenAHttpGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastResponse(), notNullValue());

        underTest.clearHistoryAndResponses();

        then(underTest.httpRequestResponseHistory().lastResponse(), nullValue());
    }

    @Test
    public void requestResponseHandlerListenerIsCalledWhenRequestIsReceived() throws Exception {
        final HttpRequestResponseEventListener requestResponseHandlerListener = mock(HttpRequestResponseEventListener.class);

        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.registerHttpRequestResponseEventListener(requestResponseHandlerListener);
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"));

        whenAHttpGetRequestIsMadeFor("/some-url");

        verify(requestResponseHandlerListener, times(1)).newRequest(Mockito.any(HttpRequest.class));
        verify(requestResponseHandlerListener, times(1)).newResponse(Mockito.any(com.staygrounded.httpstubby.server.response.HttpResponse.class));
    }


    private HttpResponse whenAHttpsGetRequestIsMadeFor(String uri, Header... headers) throws Exception {
        HttpGet getMethod = new HttpGet("https://localhost:" + underTest.httpPort() + uri);
        return executeMethod(getMethod, headers);
    }

    private HttpResponse whenAHttpGetRequestIsMadeFor(String uri, Header... headers) throws Exception {
        HttpGet getMethod = new HttpGet("http://localhost:" + underTest.httpPort() + uri);
        return executeMethod(getMethod, headers);
    }

    private HttpResponse whenAHttpPostRequestIsMadeFor(String uri, Header... headers) throws Exception {
        HttpPost postMethod = new HttpPost("http://localhost:" + underTest.httpPort() + uri);
        return executeMethod(postMethod, headers);
    }

    public static <T extends Object> void then(T theThing, Matcher<? super T> is) {
        assertThat(theThing, is);
    }


    private <T extends HttpRequestBase> HttpResponse executeMethod(T httpMethod, Header[] headers) throws Exception {
        final SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();

        final CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                .build();

        for (Header header : headers) {
            httpMethod.addHeader(header);
        }

        return httpClient.execute(httpMethod);
    }

    @After
    public void stopServer() {
        underTest.stop();
    }

}
