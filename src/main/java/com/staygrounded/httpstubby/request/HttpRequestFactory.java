package com.staygrounded.httpstubby.request;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class HttpRequestFactory {

    public HttpRequest createHttpRequest(HttpExchange httpExchange) throws IOException {
        return new HttpRequest(
                HttpMethod.valueOf(httpExchange.getRequestMethod()),
                httpExchange.getRequestURI(),
                IOUtils.toString(httpExchange.getRequestBody()),
                httpExchange.getRequestHeaders());
    }

}
