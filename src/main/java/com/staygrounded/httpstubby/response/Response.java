package com.staygrounded.httpstubby.response;

import java.util.HashMap;
import java.util.Map;

import static com.staygrounded.httpstubby.response.MediaType.TEXT_PLAIN;
import static java.nio.charset.Charset.defaultCharset;

public class Response {

    private final Map<String, String> headers;
    private final HttpStatus.Code statusCode;
    private final String body;

    public Response(HttpStatus.Code statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body != null ? body : "";
        this.headers = headers;
    }

    public static Response aResponseWithStatusCode(HttpStatus.Code statusCode) {
        return new Response(statusCode, null, new HashMap<>());
    }

    public byte[] getBody() {
        return body.getBytes(defaultCharset());
    }

    public String getBodyAsString() {
        return body.toString();
    }

    public HttpStatus.Code getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getResponseLength() {
        return body.length();
    }

    public String getContentType() {
        if (headers.containsKey("Content-Type")) {
            return headers.get("Content-Type");
        } else {
            return TEXT_PLAIN.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (!headers.equals(response.headers)) return false;
        if (statusCode != response.statusCode) return false;
        return body != null ? body.equals(response.body) : response.body == null;
    }

    @Override
    public int hashCode() {
        int result = headers.hashCode();
        result = 31 * result + statusCode.hashCode();
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "headers=" + headers +
                ", statusCode=" + statusCode +
                ", body='" + body + '\'' +
                '}';
    }
}
