package uk.staygrounded.httpstubby.matchers.request;

import uk.staygrounded.httpstubby.server.request.HttpRequest;
import uk.staygrounded.httpstubby.server.request.HttpMethod;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static uk.staygrounded.httpstubby.server.request.HttpRequest.createHttpRequestWith;
import static org.junit.Assert.assertThat;

public class RequestHttpMethodMatcherTest {
    @Test
    public void aGetRequest() throws Exception {
        assertThat(aRequestWithHttpMethod(HttpMethod.GET), RequestMethodMatcher.forAGetRequest());
    }

    @Test
    public void aPostRequest() throws Exception {
        assertThat(aRequestWithHttpMethod(HttpMethod.POST), RequestMethodMatcher.forAPostRequest());
    }

    @Test
    public void aPutRequest() throws Exception {
        assertThat(aRequestWithHttpMethod(HttpMethod.PUT), RequestMethodMatcher.forAPutRequest());
    }

    @Test
    public void aDeleteRequest() throws Exception {
        assertThat(aRequestWithHttpMethod(HttpMethod.DELETE), RequestMethodMatcher.forADeleteRequest());
    }

    @Test
    public void aHeadRequest() throws Exception {
        assertThat(aRequestWithHttpMethod(HttpMethod.HEAD), RequestMethodMatcher.forAHeadRequest());
    }

    private HttpRequest aRequestWithHttpMethod(HttpMethod httpMethod) throws URISyntaxException {
        return createHttpRequestWith(httpMethod, new URI("some-url"));
    }

}