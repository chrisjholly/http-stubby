package com.solly.httpstubby.server;

import com.solly.httpstubby.handler.TestStubRequestResponseHandlerListener;
import com.solly.httpstubby.matchers.request.RequestHeaderExistsMatcher;
import com.solly.httpstubby.matchers.response.ResponseHeaderMatcher;
import com.solly.httpstubby.server.ssl.SelfSignedSSLContextFactory;
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

import static com.solly.httpstubby.matchers.request.RequestHeaderMatcher.requestHeaderContains;
import static com.solly.httpstubby.matchers.request.RequestMethodMatcher.forAGetRequest;
import static com.solly.httpstubby.matchers.request.RequestMethodMatcher.forAPostRequest;
import static com.solly.httpstubby.matchers.request.RequestUriMatcher.uriEqualTo;
import static com.solly.httpstubby.matchers.request.RequestUriMatcher.uriStartsWith;
import static com.solly.httpstubby.matchers.response.ResponseBodyMatcher.withBody;
import static com.solly.httpstubby.matchers.response.ResponseStatusCodeMatcher.withStatusCode;
import static com.solly.httpstubby.response.HttpStatus.Code.NOT_FOUND;
import static com.solly.httpstubby.response.HttpStatus.Code.OK;
import static com.solly.httpstubby.response.ResponseBuilder.responseOf;
import static com.solly.httpstubby.server.HttpPortNumberGenerator.nextAvailablePortNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertTrue;


public class StubbableHttpServerTest {

    private StubbableHttpServer underTest;

    @Test
    public void storesLastRequestUri() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        whenAHttpGetRequestIsMadeFor("/context/some-url");

        then(underTest.history().lastRequest().getRequestUri().toString(), equalTo("/context/some-url"));
    }

    @Test
    public void storesLastRequest() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        whenAHttpGetRequestIsMadeFor("/context/some-url");

        then(underTest.history().lastRequest(), allOf(
                uriEqualTo("/context/some-url"),
                is(forAGetRequest())));
    }

    @Test
    public void storesLastRequestHeaders() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        whenAHttpGetRequestIsMadeFor("/context/some-url", withHeader("Some-header-key", "Some-header-value"));

        then(underTest.history().lastRequest(), allOf(
                RequestHeaderExistsMatcher.requestHeaderExists("Some-header-key"),
                requestHeaderContains("Some-header-key", "Some-header-value"),
                RequestHeaderExistsMatcher.requestHeaderDoesNotExist("header-which-does-not-exist")));
    }

    @Test
    public void storesLastResponse() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");
        and(underTest.willReturn(responseOf(OK, "response-to-be-returned")));

        whenAHttpGetRequestIsMadeFor("/context/some-url");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void serverReturnsConfiguredDefaultResponseWhenNoOtherResponsesFound() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturnWhenNoResponseFound(responseOf(NOT_FOUND, "no responses match")));

        whenAHttpGetRequestIsMadeFor("/context/some-url-with-no-response-found");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(NOT_FOUND),
                withBody("no responses match")));
    }

    @Test
    public void serverRespondsWithCorrectResponseIfMultipleResponsesArePrimed() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-not-be-returned"), uriEqualTo("/context/url-not-to-be-called")));
        and(underTest.willReturn(responseOf(OK, "response-to-be-returned"), uriEqualTo("/context/url-to-be-called")));

        whenAHttpGetRequestIsMadeFor("/context/url-to-be-called");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void returnFirstResponseConfiguredWhenMoreThanOneMatcherMatchesRequest() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-should-be-returned"), uriEqualTo("/context/url")));
        and(underTest.willReturn(responseOf(OK, "response-should-not-be-returned"), uriStartsWith("/context/url")));

        whenAHttpGetRequestIsMadeFor("/context/url");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-should-be-returned")));
    }

    @Test
    public void serverRespondsForSpecificUriWithAdditionalMatcher() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-for-post-request"), forAPostRequest()));

        whenAHttpPostRequestIsMadeFor("/context/url-to-test");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-for-post-request")));
    }

    @Test
    public void serverRespondsNullForAdditionalRequestMatcherNotMatching() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-body-for-a-post-request"), forAPostRequest()));

        whenAHttpGetRequestIsMadeFor("/context/url-for-a-get-request");

        then(underTest.history().lastResponse(), is(withStatusCode(NOT_FOUND)));
    }

    @Test
    public void serverRespondsForSpecificUriWithQueryParameters() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-not-be-returned"), uriEqualTo("/context/response-uri-with-query-params")));
        and(underTest.willReturn(responseOf(OK, "response-to-be-returned"), uriEqualTo("/context/response-uri-with-query-params?x=this-is-the-correct-request")));

        whenAHttpGetRequestIsMadeFor("/context/response-uri-with-query-params?x=this-is-the-correct-request");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void serverRespondsForAUriThatStartsWith() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-to-be-returned"), uriStartsWith("/context/url")));

        whenAHttpGetRequestIsMadeFor("/context/url-that-ends-with-anything");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void serverRespondsWithDelay() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        and(underTest.willReturn(responseOf(OK, "response-to-be-returned")
                .withLatency(Duration.ofSeconds(2))));

        whenAHttpGetRequestIsMadeFor("/context/url-that-ends-with-anything");

        stopWatch.stop();

        assertTrue(Duration.ofMillis(stopWatch.getTime()).getSeconds() >= 2);
    }

    @Test
    public void storesLastResponseForAGetRequest() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-to-be-returned"), forAGetRequest()));

        whenAHttpGetRequestIsMadeFor("/context/some-url");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void storesLastResponseForAPostRequest() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-to-be-returned"), forAPostRequest()));

        whenAHttpPostRequestIsMadeFor("/context/some-url");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void storesLastResponseForAResponseWithHeader() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-to-be-returned")
                .withHeader("some-header-key", "some-header-value")));

        whenAHttpGetRequestIsMadeFor("/context/some-url");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned"),
                ResponseHeaderMatcher.responseHeaderContains("some-header-key", "some-header-value")));
    }

    @Test
    public void serverRespondsWithADifferentValueOnSubsequentRequests() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, new Callable<String>() {
            private final AtomicInteger uniqueNumber = new AtomicInteger(1);

            public String call() {
                return String.valueOf(uniqueNumber.getAndIncrement());
            }
        })));

        whenAHttpGetRequestIsMadeFor("/context/unique-number-generator");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("1")));

        whenAHttpGetRequestIsMadeFor("/context/unique-number-generator");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("2")));
    }

    @Test
    public void serverSupportsHttpsRequests() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpsServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-to-be-returned")));

        whenAHttpsGetRequestIsMadeFor("/context/some-url");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void serveSupportsHttpsRequests() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpsServer(nextAvailablePortNumber(), new SelfSignedSSLContextFactory()
                .createContext("/https-keystore.jks", "password")));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-to-be-returned")));

        whenAHttpsGetRequestIsMadeFor("/context/some-url");

        then(underTest.history().lastResponse(), allOf(
                withStatusCode(OK),
                withBody("response-to-be-returned")));
    }

    @Test
    public void clearHistoryAndResponseRemovesLatestRequestAndResponses() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        and(underTest.willReturn(responseOf(OK, "response-to-be-returned")));

        whenAHttpGetRequestIsMadeFor("/context/some-url");

        underTest.clearHistoryAndResponses();

        then(underTest.history().lastRequest(), is(nullValue()));
        then(underTest.history().lastResponse(), is(nullValue()));
    }

    @Test
    public void anyExistingPrimedResponsesAreRemovedFromStub() throws Exception {
        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context");

        underTest.willReturn(responseOf(OK, "response-to-be-returned"));

        whenAHttpGetRequestIsMadeFor("/context/some-url");

        then(underTest.history().lastResponse(), notNullValue());

        underTest.clearHistoryAndResponses();

        then(underTest.history().lastResponse(), nullValue());
    }

    @Test
    public void requestResponseHandlerListenerIsCalled() throws Exception {
        final TestStubRequestResponseHandlerListener requestResponseHandlerListener = new TestStubRequestResponseHandlerListener();

        underTest = new StubbableHttpServer(HttpServerFactory.createHttpServer(nextAvailablePortNumber()));
        underTest.startWithContext("/context", requestResponseHandlerListener);

        underTest.willReturn(responseOf(OK, "response-to-be-returned"));

        whenAHttpGetRequestIsMadeFor("/context/some-url");

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
        System.out.println("11");
        return httpClient.execute(httpMethod);
    }

    @After
    public void stopServer() {
        underTest.stop();
    }

}
