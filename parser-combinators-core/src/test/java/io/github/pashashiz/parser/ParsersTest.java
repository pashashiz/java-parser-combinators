package io.github.pashashiz.parser;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static io.github.pashashiz.parser.PMatchers.failure;
import static io.github.pashashiz.parser.PMatchers.success;
import static io.github.pashashiz.parser.Parsers.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertThat;

public class ParsersTest {

    @Test
    public void string_WhenSuccess() {
        assertThat(string("hello").parse("hello pavlo"), success("hello"));
    }

    @Test
    public void string_WhenFailure() {
        assertThat(string("yo").parse("hello pavlo"), failure());
    }

    @Test
    public void regexp_WhenSuccess() {
        assertThat(regexp("hel[a-z]o").parse("hello pavlo"), success("hello"));
    }

    @Test
    public void regexp_WhenFailure() {
        assertThat(regexp("yo").parse("hello pavlo"), failure());
    }

    @Test
    public void eol_WhenSuccess() {
        assertThat(eol().parse("\n"), success());
    }

    @Test
    public void allUntilEol_WhenSuccess() {
        assertThat(allUntilEol().parse("hello pavlo\n"), success("hello pavlo"));
    }

    @Test
    public void allUntilEol_WhenFailure() {
        assertThat(allUntilEol().parse("hello pavlo"), failure());
    }

    @Test
    public void word_WhenSuccess() {
        assertThat(word().parse("hey dude"), success("hey"));
    }

    @Test
    public void word_WhenFailed() {
        assertThat(word().parse("?dude"), failure());
    }

    @Test
    public void token_WhenBothSpaces() {
        assertThat(
                token(string("hi")).parse("  hi  "),
                success("hi"));
    }

    @Test
    public void token_WhenLeftSpace() {
        assertThat(
                token(string("hi")).parse("  hi"),
                success("hi"));
    }

    @Test
    public void token_WhenRightSpace() {
        assertThat(
                token(string("hi")).parse("hi  "),
                success("hi"));
    }

    @Test
    public void token_WhenNoSpaces() {
        assertThat(
                token(string("hi")).parse("hi"),
                success("hi"));
    }

    @Test
    public void token_WhenMultiple() {
        assertThat(
                product(token(string("hello")), token(string("dude"))).parse("hello dude"),
                success(new Pair<>("hello", "dude")));
    }

    @Test
    public void natural_WhenSuccess() {
        assertThat(natural().parse("1"), success(1));
    }

    @Test
    public void natural_WhenFailure() {
        assertThat(natural().parse("hey 1"), failure());
    }

    @Test
    public void doubleWithPoint_WhenSuccess() {
        assertThat(double_().parse("1.1 hey"), success(1.1));
    }

    @Test
    public void doubleNoPoint_WhenSuccess() {
        assertThat(double_().parse("1 hey"), success(1.0));
    }

    @Test
    public void double_WhenFailure() {
        assertThat(double_().parse("hey 1"), failure());
    }

    @Test
    public void null_WhenSuccess() {
        assertThat(null_().parse("null"), success());
    }

    @Test
    public void null_WhenFailure() {
        assertThat(null_().parse("0"), failure());
    }

    @Test
    public void boolean_TrueWhenSuccess() {
        assertThat(boolean_().parse("true"), success(true));
    }

    @Test
    public void boolean_FalseWhenSuccess() {
        assertThat(boolean_().parse("false"), success(false));
    }

    @Test
    public void boolean_WhenFailure() {
        assertThat(boolean_().parse("0"), failure());
    }

    @Test
    public void escaped_WhenSimpleSuccess() {
        assertThat(quoted().parse("\"hey\" other"), success("hey"));
    }

    @Test
    public void escaped_WhenSimpleFailure() {
        assertThat(quoted().parse("hey other"), failure());
    }

    @Test
    public void escaped_WhenQuotedSuccess() {
         assertThat(quoted().parse("\"hey Bob\" other"), success("hey Bob"));
    }

    @Test
    public void escaped_WhenQuotedFailure() {
        assertThat(quoted().parse("\"hey Bob"), failure());
    }

    @Test
    public void escaped_WhenEscapedSuccess() {
        // assertThat(escaped().parse("\"hey \\\"mr.\\\" Bob\" other"), success("hey \"mr.\" Bob"));
    }

    @Test
    public void escaped_WhenEscapedFailure() {
        // assertThat(escaped().parse("\"hey \\\"mr.\\\" Bob other"), failure());
    }

    @Test
    public void flatMap_WhenSucceed() {
        assertThat(string("1")
                .flatMap(a -> string("2").map(b -> a + b))
                .flatMap(a -> string("3").map(b -> a + b))
                .parse("12345"),
                success("123"));
    }

    @Test
    public void map_WhenSucceed() {
        assertThat(string("1").map(a -> a + "!").parse("12345"), success("1!"));
    }

    @Test
    public void map2_WhenSucceed() {
        assertThat(
                string("1").and(string("2"), (a, b) -> a + b)
                           .and(string("3"), (a, b) -> a + b).parse("12345"),
                success("123"));
    }

    @Test
    public void skipLeft_WhenSucceed() {
        assertThat(
                skipLeft(regexp("\\s"), string("hello")).parse(" hello pavlo"),
                success("hello"));
    }

    @Test
    public void skipRight_WhenSucceed() {
        assertThat(
                skipRight(regexp("hello"), (string(" ")))
                        .and(string("pavlo"), (s, s2) -> s + " " + s2).parse("hello pavlo"),
                success("hello pavlo"));
    }

    @Test
    public void or_WhenFirstSuccess() {
        assertThat(
                string("hello").or(string("yo")).parse("hello pavlo"),
                success("hello"));
    }

    @Test
    public void or_WhenFirstFailsButFallbackSucceed() {
        assertThat(
                string("hello").or(string("yo")).parse("yo pavlo"),
                success("yo"));
    }

    @Test
    public void or_WhenNestedSucceed() {
        assertThat(
                string("hello").or(string("yo")).or(string("hi")).parse("hi pavlo"),
                success("hi"));
    }

    @Test
    public void or_WhenFallbackFails() {
        assertThat(
                string("hello").or(string("yo")).parse("hi pavlo"),
                failure());
    }

    @Test
    public void many_WhenSucceed() {
        assertThat(
                many(skipRight(natural(), string(";"))).parse("1;2;3;"),
                success(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void atLeastOne_WhenSucceed() {
        assertThat(
                atLeastOne(skipRight(natural(), string(";"))).parse("1;2;3;"),
                success(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void atLeastOneSeparated_WhenSucceed() {
        assertThat(
                atLeastOneSeparated(natural(), string(",")).parse("1,2,3"),
                success(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void atLeastOneSeparated_WhenFails() {
        assertThat(
                atLeastOneSeparated(natural(), string(",")).parse(""),
                failure());
    }

    @Test
    public void separated_MultipleWhenSucceed() {
        assertThat(
                separated(natural(), string(",")).parse("1,2,3"),
                success(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void separated_OneWhenSucceed() {
        assertThat(
                separated(natural(), string(",")).parse("1"),
                success(singletonList(1)));
    }

    @Test
    public void separated_EmptyWhenSucceed() {
        assertThat(
                separated(natural(), string(",")).parse(""),
                success(emptyList()));
    }

    @Test
    public void atLeastOne_WhenFailure() {
        assertThat(
                atLeastOne(skipRight(natural(), string(";"))).parse(""),
                failure());
    }

    @Test
    public void optional_WhenPresent() {
        assertThat(
                optional(string("hello")).parse("hello"),
                success(Optional.of("hello")));
    }

    @Test
    public void optional_WhenAbsent() {
        assertThat(
                optional(string("hello")).parse("hi"),
                success(Optional.empty()));
    }
}