package com.staygrounded.httpstubby.response;

import java.util.Map;

import static com.staygrounded.httpstubby.response.MediaType.TEXT_PLAIN;
import static java.nio.charset.Charset.defaultCharset;

public class HttpResponse {

    private final Map<String, String> headers;
    private final int statusCode;
    private final String body;

    HttpResponse(int statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body != null ? body : "";
        this.headers = headers;
    }

    public byte[] getBody() {
        return body.getBytes(defaultCharset());
    }

    public String getBodyAsString() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getResponseLength() {
        return body.length();
    }

    public String getContentType() {
        return headers.getOrDefault("Content-Type", TEXT_PLAIN.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpResponse httpResponse = (HttpResponse) o;

        if (!headers.equals(httpResponse.headers)) return false;
        if (statusCode != httpResponse.statusCode) return false;
        return body != null ? body.equals(httpResponse.body) : httpResponse.body == null;
    }

    @Override
    public int hashCode() {
        int result = headers.hashCode();
        result = 31 * result + statusCode;
        result = 31 * result + body.hashCode();
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
