package com.solly.httpstubby.response;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class ResponseExecutor {

    public void sendResponse(HttpExchange httpExchange, Response response) throws IOException {
        if (response == null) {
            throw new IllegalArgumentException("Cannot send response, none set");
        }

        final Map<String, List<String>> headerMap = new HashMap<>();
        response.getHeaders().entrySet()
                .forEach(header -> headerMap.put(header.getKey(), singletonList(header.getValue())));

        httpExchange.getResponseHeaders().put("Content-Type", singletonList(response.getContentType()));
        httpExchange.getResponseHeaders().putAll(headerMap);

        int responseBodyLength = response.getResponseLength();
        httpExchange.sendResponseHeaders(response.getStatusCode().getCode(), responseBodyLength);

        httpExchange.getResponseBody().write(response.getBody());
    }

}
