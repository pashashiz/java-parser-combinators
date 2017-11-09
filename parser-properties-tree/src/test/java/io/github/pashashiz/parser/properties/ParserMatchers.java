package io.github.pashashiz.parser.properties;

import io.github.pashashiz.parser.Result;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ParserMatchers {

    public static <T> Matcher<Result<T>> success(T value) {
        return new BaseMatcher<Result<T>>() {

            @Override @SuppressWarnings("unchecked")
            public boolean matches(Object item) {
                Result<T> casted = (Result<T>) item;
                return casted.match(
                        success -> value.equals(success.getValue()),
                        failure -> false);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("expected").appendValue("Success{value=" + value +", location=*}");
            }
        };
    }

    public static <T> Matcher<Result<T>> failure(T error) {
        return new BaseMatcher<Result<T>>() {

            @Override @SuppressWarnings("unchecked")
            public boolean matches(Object item) {
                Result<T> casted = (Result<T>) item;
                return casted.match(
                        success -> false,
                        failure -> error.equals(failure.getErrorCause()));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("expected").appendValue("Failure{error=" + error + "}");
            }
        };
    }

    public static <T> Matcher<Result<T>> failure() {
        return new BaseMatcher<Result<T>>() {

            @Override @SuppressWarnings("unchecked")
            public boolean matches(Object item) {
                Result<T> casted = (Result<T>) item;
                return casted.match(
                        success -> false,
                        failure -> true);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("expected").appendValue("Failure{error==*}");
            }
        };
    }

}
