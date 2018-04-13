package uk.staygrounded.httpstubby.matchers.request;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import uk.staygrounded.httpstubby.matchers.request.builder.UrlEncodedFormPayloadMatcherBuilder;
import uk.staygrounded.httpstubby.server.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;

import static java.net.URLDecoder.decode;

public class RequestUrlEncodedFormPayloadMatcher extends TypeSafeMatcher<HttpRequest> {

    private final Matcher<Map<String, String>> mapMatcher;

    private RequestUrlEncodedFormPayloadMatcher(Matcher<Map<String, String>> mapMatcher) {
        this.mapMatcher = mapMatcher;
    }

    public static RequestUrlEncodedFormPayloadMatcher urlFormPayload(UrlEncodedFormPayloadMatcherBuilder mapMatcherBuilder) {
        return new RequestUrlEncodedFormPayloadMatcher(mapMatcherBuilder.build());
    }

    @Override
    protected boolean matchesSafely(HttpRequest httpRequest) {
        final Map<String, String> formMap = new HashMap<>();

        final String[] formElements = httpRequest.getRequestBody().split("&");
        for (String formElement : formElements) {
            final String[] keyAndValue = formElement.split("=");
            formMap.put(decode(keyAndValue[0]), decode(keyAndValue[1]));
        }

        return mapMatcher.matches(formMap);
    }

    @Override
    public void describeTo(Description description) {
        mapMatcher.describeTo(description);
    }

}

