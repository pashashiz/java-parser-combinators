package com.ps.parser;

import org.junit.Test;

import java.util.Arrays;

import static com.ps.parser.Parser.*;
import static com.ps.parser.ParserMatchers.failure;
import static com.ps.parser.ParserMatchers.success;
import static org.junit.Assert.assertThat;

public class ParserTest {

    @Test
    public void string_WhenSuccess() throws Exception {
        assertThat(string("hello").run("hello pavlo"), success("hello"));
    }

    @Test
    public void string_WhenFailure() throws Exception {
        assertThat(string("yo").run("hello pavlo"), failure());
    }

    @Test
    public void regexp_WhenSuccess() throws Exception {
        assertThat(regexp("hel[a-z]o").run("hello pavlo"), success("hello"));
    }

    @Test
    public void regexp_WhenFailure() throws Exception {
        assertThat(regexp("yo").run("hello pavlo"), failure());
    }

    @Test
    public void eol_WhenSuccess() throws Exception {
        assertThat(eol().run("\n"), success("\n"));
    }

    @Test
    public void allUntilEol_WhenSuccess() throws Exception {
        assertThat(allUntilEol().run("hello pavlo\n"), success("hello pavlo"));
    }

    @Test
    public void allUntilEol_WhenFailure() throws Exception {
        assertThat(allUntilEol().run("hello pavlo"), failure());
    }

    @Test
    public void word_WhenSuccess() throws Exception {
        assertThat(word().run("hey dude"), success("hey"));
    }

    @Test
    public void word_WhenFailed() throws Exception {
        assertThat(word().run("?dude"), failure());
    }

    @Test
    public void flatMap_WhenSucceed() throws Exception {
        assertThat(string("1")
                .flatMap(a -> string("2").map(b -> a + b))
                .flatMap(a -> string("3").map(b -> a + b))
                .run("12345"),
                success("123"));
    }

    @Test
    public void map_WhenSucceed() throws Exception {
        assertThat(string("1").map(a -> a + "!").run("12345"), success("1!"));
    }

    @Test
    public void map2_WhenSucceed() throws Exception {
        assertThat(
                string("1").map2(string("2"), (a, b) -> a + b)
                           .map2(string("3"), (a, b) -> a + b).run("12345"),
                success("123"));
    }

    @Test
    public void skipLeft_WhenSucceed() throws Exception {
        assertThat(
                regexp("\\s").skipLeft(string("hello")).run(" hello pavlo"),
                success("hello"));
    }

    @Test
    public void skipRight_WhenSucceed() throws Exception {
        assertThat(
                regexp("hello").skipRight(string(" "))
                        .map2(string("pavlo"), (s, s2) -> s + " " + s2).run("hello pavlo"),
                success("hello pavlo"));
    }

    @Test
    public void or_WhenFirstSuccess() throws Exception {
        assertThat(
                string("hello").or(string("yo")).run("hello pavlo"),
                success("hello"));
    }

    @Test
    public void or_WhenFirstFailsButFallbackSucceed() throws Exception {
        assertThat(
                string("hello").or(string("yo")).run("yo pavlo"),
                success("yo"));
    }

    @Test
    public void or_WhenNestedSucceed() throws Exception {
        assertThat(
                string("hello").or(string("yo")).or(string("hi")).run("hi pavlo"),
                success("hi"));
    }

    @Test
    public void or_WhenFallbackFails() throws Exception {
        assertThat(
                string("hello").or(string("yo")).run("hi pavlo"),
                failure());
    }

    @Test
    public void many_WhenSucceed() throws Exception {
        assertThat(
                many(skipRight(natural(), string(";"))).run("1;2;3;"),
                success(Arrays.asList("1", "2", "3")));
    }

    @Test
    public void manyOrOne_WhenSucceed() throws Exception {
        assertThat(
                many1(skipRight(natural(), string(";"))).run("1;2;3;"),
                success(Arrays.asList("1", "2", "3")));
    }

    @Test
    public void manyOrOne_WhenFailure() throws Exception {
        assertThat(
                many1(skipRight(natural(), string(";"))).run(""),
                failure());
    }
}