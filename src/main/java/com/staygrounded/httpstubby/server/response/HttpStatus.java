package com.staygrounded.httpstubby.server.response;

import static java.lang.String.format;

public class HttpStatus {

    public enum Code {

        OK(200, "OK"),
        CREATED(201, "Created"),
        ACCEPTED(202, "Accepted"),
        NO_CONTENT(204, "No Content"),

        MULTIPLE_CHOICES(300, "Multiple Choices"),
        MOVED_PERMANENTLY(301, "Moved Permanently"),
        MOVED_TEMPORARILY(302, "Moved Temporarily"),
        FOUND(302, "Found"),
        SEE_OTHER(303, "See Other"),
        NOT_MODIFIED(304, "Not Modified"),

        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_ALLOWED(405, "HttpMethod Not Allowed"),
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

        INTERNAL_SERVER_ERROR(500, "Server Error"),
        BAD_GATEWAY(502, "Bad Gateway"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        GATEWAY_TIMEOUT(504, "Gateway Timeout");

        private final int statusCode;
        private final String message;

        Code(int code, String message) {
            this.statusCode = code;
            this.message = message;
        }

        public int getCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return format("[%d-%s]", this.statusCode, this.message);
        }

    }

}
