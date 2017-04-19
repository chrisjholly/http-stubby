package com.solly.httpstubby.matchers.request;

import com.solly.httpstubby.request.HttpRequest;
import com.solly.httpstubby.request.HttpMethod;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertThat;

public class RequestHttpMethodMatcherTest {
    @Test
    public void aGetRequest() throws Exception {
        assertThat(aRequestWithMethod(HttpMethod.GET), RequestMethodMatcher.forAGetRequest());
    }

    @Test
    public void aPostRequest() throws Exception {
        assertThat(aRequestWithMethod(HttpMethod.POST), RequestMethodMatcher.forAPostRequest());
    }

    @Test
    public void aPutRequest() throws Exception {
        assertThat(aRequestWithMethod(HttpMethod.PUT), RequestMethodMatcher.forAPutRequest());
    }

    @Test
    public void aDeleteRequest() throws Exception {
        assertThat(aRequestWithMethod(HttpMethod.DELETE), RequestMethodMatcher.forADeleteRequest());
    }

    @Test
    public void aHeadRequest() throws Exception {
        assertThat(aRequestWithMethod(HttpMethod.HEAD), RequestMethodMatcher.forAHeadRequest());
    }

    private HttpRequest aRequestWithMethod(HttpMethod httpMethod) throws URISyntaxException {
        return new HttpRequest(httpMethod, new URI("some-url"), null);
    }

}