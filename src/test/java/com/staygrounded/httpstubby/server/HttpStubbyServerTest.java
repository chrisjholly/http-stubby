package com.staygrounded.httpstubby.server;

import com.staygrounded.httpstubby.auditor.TestStubHttpRequestResponseEventListener;
import com.staygrounded.httpstubby.matchers.request.RequestHeaderExistsMatcher;
import com.staygrounded.httpstubby.matchers.response.ResponseHeaderMatcher;
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
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static com.staygrounded.httpstubby.matchers.request.RequestHeaderContainsMatcher.requestHeaderContains;
import static com.staygrounded.httpstubby.matchers.request.RequestMethodMatcher.forAGetRequest;
import static com.staygrounded.httpstubby.matchers.request.RequestMethodMatcher.forAPostRequest;
import static com.staygrounded.httpstubby.matchers.request.RequestUriMatcher.uriEqualTo;
import static com.staygrounded.httpstubby.matchers.request.RequestUriMatcher.uriStartsWith;
import static com.staygrounded.httpstubby.matchers.response.ResponseBodyMatcher.withBody;
import static com.staygrounded.httpstubby.matchers.response.ResponseStatusCodeMatcher.withStatusCode;
import static com.staygrounded.httpstubby.server.response.HttpResponseBuilder.responseOf;
import static com.staygrounded.httpstubby.server.response.HttpStatus.Code.NOT_FOUND;
import static com.staygrounded.httpstubby.server.response.HttpStatus.Code.OK;
import static com.staygrounded.httpstubby.server.HttpPortNumberGenerator.nextAvailablePortNumber;
import static com.staygrounded.httpstubby.server.HttpServerFactory.httpConfiguration;
import static com.staygrounded.httpstubby.server.HttpStubbyServer.stubbyServerWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertTrue;

public class HttpStubbyServerTest {

    private HttpStubbyServer underTest;

    @Test
    public void storesLastRequestUri() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        whenAHttpGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastRequest().getRequestUri().toString(), equalTo("/some-url"));
    }

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
    public void storesLastRequestHeaders() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        whenAHttpGetRequestIsMadeFor("/some-url", withHeader("Some-header-key", "Some-header-value"));

        then(underTest.httpRequestResponseHistory().lastRequest(), allOf(
                RequestHeaderExistsMatcher.requestHeaderExists("Some-header-key"),
                requestHeaderContains("Some-header-key", "Some-header-value")));
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
    public void serverRespondsWithCorrectResponseIfMultipleResponsesArePrimed() throws Exception {
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
    public void returnFirstResponseConfiguredWhenMoreThanOneMatcherMatchesRequest() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-should-be-returned"), uriEqualTo("/url"));
        underTest.willReturn(responseOf(OK).withBody("response-should-not-be-)returned"), uriStartsWith("/url"));

        whenAHttpGetRequestIsMadeFor("/url");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-should-be-returned")));
    }

    @Test
    public void serverRespondsForSpecificUriWithAdditionalMatcher() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-for-post-request"), forAPostRequest());

        whenAHttpPostRequestIsMadeFor("/url-to-test");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-for-post-request")));
    }

    @Test
    public void serverRespondsNullForAdditionalRequestMatcherNotMatching() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-body-for-a-)post-request"), forAPostRequest());

        whenAHttpGetRequestIsMadeFor("/url-for-a-get-request");

        then(underTest.httpRequestResponseHistory().lastResponse(), is(withStatusCode(NOT_FOUND)));
    }

    @Test
    public void serverRespondsForSpecificUriWithQueryParameters() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-not-be-returned"), uriEqualTo("/response-uri-with-query-params"));
        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"), uriEqualTo("/response-uri-with-query-params?x=this-is-the-correct-request"));

        whenAHttpGetRequestIsMadeFor("/response-uri-with-query-params?x=this-is-the-correct-request");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void serverRespondsForAUriThatStartsWith() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"), uriStartsWith("/url"));

        whenAHttpGetRequestIsMadeFor("/url-that-ends-with-anything");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void serverRespondsWithDelay() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned")
                .withLatency(Duration.ofSeconds(2)));

        whenAHttpGetRequestIsMadeFor("/url-that-ends-with-anything");

        stopWatch.stop();

        assertTrue(Duration.ofMillis(stopWatch.getTime()).getSeconds() >= 2);
    }

    @Test
    public void storesLastResponseForAGetRequest() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"), forAGetRequest());

        whenAHttpGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void storesLastResponseForAPostRequest() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"), forAPostRequest());

        whenAHttpPostRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void storesLastResponseForAResponseWithHeader() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned")
                .withHeader("some-header-key", "some-header-value"));

        whenAHttpGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned"),
                ResponseHeaderMatcher.responseHeaderContains("some-header-key", "some-header-value")));
    }

    @Test
    public void serverRespondsWithADifferentValueOnSubsequentRequests() throws Exception {
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
    public void serverSupportsHttpsRequests() throws Exception {
        underTest = stubbyServerWith(HttpServerFactory.httpsConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"));

        whenAHttpsGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void serveSupportsHttpsRequests() throws Exception {
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
    public void clearHistoryAndResponseRemovesLatestRequestAndResponses() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"));

        whenAHttpGetRequestIsMadeFor("/some-url");

        underTest.clearHistoryAndResponses();

        then(underTest.httpRequestResponseHistory().lastRequest(), is(nullValue()));
        then(underTest.httpRequestResponseHistory().lastResponse(), is(nullValue()));
    }

    @Test
    public void anyExistingPrimedResponsesAreRemovedFromStub() throws Exception {
        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"));

        whenAHttpGetRequestIsMadeFor("/some-url");

        then(underTest.httpRequestResponseHistory().lastResponse(), notNullValue());

        underTest.clearHistoryAndResponses();

        then(underTest.httpRequestResponseHistory().lastResponse(), nullValue());
    }

    @Test
    public void requestResponseHandlerListenerIsCalled() throws Exception {
        final TestStubHttpRequestResponseEventListener requestResponseHandlerListener = new TestStubHttpRequestResponseEventListener();

        underTest = stubbyServerWith(httpConfiguration(nextAvailablePortNumber()));
        underTest.registerHttpRequestResponseEventListener(requestResponseHandlerListener);
        underTest.start();

        underTest.willReturn(responseOf(OK).withBody("response-to-be-returned"));

        whenAHttpGetRequestIsMadeFor("/some-url");

        assertTrue(requestResponseHandlerListener.isHasRequest());
        assertTrue(requestResponseHandlerListener.isHasResponse());
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

    private BasicHeader withHeader(String name, String value) {
        return new BasicHeader(name, value);
    }

    public static <T extends Object> void then(T theThing, Matcher<? super T> is) {
        assertThat(theThing, is);
    }

    public static <T extends Object> void and(T theThing) {
        return;
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
