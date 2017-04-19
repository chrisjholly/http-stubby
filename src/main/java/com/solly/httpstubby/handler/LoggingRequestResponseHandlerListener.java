package com.solly.httpstubby.handler;

import com.solly.httpstubby.request.HttpRequest;
import com.solly.httpstubby.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingRequestResponseHandlerListener implements RequestResponseHandlerListener {

    private static final Logger logger = LoggerFactory.getLogger(LoggingRequestResponseHandlerListener.class);

    @Override
    public void newRequest(HttpRequest httpRequest) {
        logger.info("New HTTP request processed: {}", httpRequest);
    }

    @Override
    public void newResponse(Response response) {
        logger.info("New HTTP response processed: {}", response);
    }
}
