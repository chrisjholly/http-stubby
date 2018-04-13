package uk.staygrounded.httpstubby.matchers.request.builder;

import org.hamcrest.Matcher;
import org.hamcrest.collection.IsMapContaining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;

/**
 * Created by chrisholly on 12/04/2018.
 */
public class UrlEncodedFormPayloadMatcherBuilder {

    private final List<Matcher<? super Map<String, String>>> matchers = new ArrayList<>();

    public static UrlEncodedFormPayloadMatcherBuilder aUrlFormMatcher() {
        return new UrlEncodedFormPayloadMatcherBuilder();
    }

    public UrlEncodedFormPayloadMatcherBuilder hasKey(final String key) {
        matchers.add(IsMapContaining.hasKey(key));
        return this;
    }

    public UrlEncodedFormPayloadMatcherBuilder withKeyAndValue(final String key, final String value) {
        matchers.add(IsMapContaining.hasEntry(key, value));
        return this;
    }

    public Matcher<Map<String, String>> build() {
        return allOf(matchers);
    }

}

