package uk.staygrounded.httpstubby.server.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Map;

import static uk.staygrounded.httpstubby.server.MediaType.TEXT_PLAIN;
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
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
