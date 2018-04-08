package com.staygrounded.httpstubby.matchers.response;

import com.staygrounded.httpstubby.response.Response;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.staygrounded.httpstubby.matchers.response.ResponseHeaderMatcher.responseHeaderContains;
import static com.staygrounded.httpstubby.response.HttpStatus.Code.OK;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ResponseHeaderMatcherTest {

    @Test
    public void returnsTrueWhenContainsMatchingHeader() throws URISyntaxException, IOException {
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("A", "1");
        expectedHeaders.put("C", "3");

        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("A", "1");
        responseHeaders.put("C", "3");
        Response response = createResponse(responseHeaders);

        assertThat(response, responseHeaderContains(expectedHeaders));
    }

    @Test
    public void returnsTrueWhenContainsMatchingSingleHeader() throws IOException, URISyntaxException {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("A", "1");
        responseHeaders.put("B", "2");
        Response response = createResponse(responseHeaders);

        assertThat(response, responseHeaderContains("A", "1"));
    }

    @Test
    public void returnsFalseWhenDoesNotContainMatchingSingleHeader() throws IOException, URISyntaxException {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("A", "1");
        responseHeaders.put("C", "3");
        Response response = createResponse(responseHeaders);

        assertThat(response, not(responseHeaderContains("A", "5")));
    }

    @Test
    public void returnsFalseWhenMissingExpectedHeader() throws URISyntaxException, IOException {
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("A", "1");
        expectedHeaders.put("D", "4");

        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("A", "1");
        responseHeaders.put("C", "3");
        Response response = createResponse(responseHeaders);

        assertThat(response, not(responseHeaderContains(expectedHeaders)));
    }

    @Test
    public void returnsTrueNoHeadersExpectedOrFound() throws URISyntaxException, IOException {
        Response response = createResponse(new HashMap<>());

        assertThat(response, responseHeaderContains(new HashMap<>()));
    }

    private Response createResponse(Map<String, String> headers) throws URISyntaxException, IOException {
        return new Response(OK, "", headers);
    }

}