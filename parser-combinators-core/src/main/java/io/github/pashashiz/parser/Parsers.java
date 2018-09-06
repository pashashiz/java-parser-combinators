package io.github.pashashiz.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parsers {

    public static <A> Parser<A> success(A result) {
        return new Parser<>(location -> new Success<>(result, location));
    }

    public static <A> Parser<A> failure(String error) {
        return new Parser<>(location -> new Failure<A>(error, location));
    }

    public static <A> Parser<A> deferred(Supplier<Parser<A>> deferredParser) {
        return new Parser<>(location -> deferredParser.get().apply(location));
    }

    public static <A, B, C> Parser<C> and(Parser<A> first, Parser<B> second, BiFunction<A, B, C> f) {
        return first.and(second, f);
    }

    public static <A, B> Parser<Pair<A, B>> product(Parser<A> left, Parser<B> right) {
        return left.product(right);
    }

    public static <A> Parser<A> skipLeft(Parser<?> left, Parser<A> right) {
        return and(left, right, (leftValue, rightValue) -> rightValue);
    }

    public static <A> Parser<A> skipRight(Parser<A> left, Parser<?> right) {
        return and(left, right, (leftValue, rightValue) -> leftValue);
    }

    public static <A1 extends B, A2 extends B, B> Parser<B> or(Parser<A1> first, Parser<A2> second) {
        return first.map(v -> (B) v).or(second);
    }

    public static <A1 extends B, A2 extends B, A3 extends B, B> Parser<B> or(
            Parser<A1> first, Parser<A2> second, Parser<A3> third) {
        return first.map(v -> (B) v).or(second).or(third);
    }

    public static <A1 extends B, A2 extends B, A3 extends B, A4 extends B, B> Parser<B> or(
            Parser<A1> first, Parser<A2> second, Parser<A3> third, Parser<A4> forth) {
        return first.map(v -> (B) v).or(second).or(third).or(forth);
    }

    public static <A> Parser<A> surround(Parser<?> left, Parser<A> parser, Parser<?> right) {
        return skipLeft(left, skipRight(parser, right));
    }

    public static <A> Parser<List<A>> many(Parser<A> parser) {
        // The idiomatic functional implementation
        // return or(
        //         and(parser, () -> many(parser), Utils::addHeadToList),
        //         success(Collections.emptyList()));
        // The imperative one which will work for Java without stack overflow on long lists
        return new Parser<>(location -> {
            Location nextLocation = location;
            List<A> list = new ArrayList<>();
            while (true) {
                Result<A> result = parser.apply(nextLocation);
                if (result instanceof Success) {
                    Success<A> success = (Success<A>) result;
                    nextLocation = success.getLocation();
                    list.add(success.getValue());
                } else {
                    break;
                }
            }
            return new Success<>(list, nextLocation);
        });
    }

    public static <A> Parser<List<A>> atLeastOne(Parser<A> parser) {
        return and(parser, deferred(() -> many(parser)), Utils::addHeadToList);
    }

    public static <A, B> Parser<List<A>> separated(Parser<A> parser, Parser<B> separator) {
        return atLeastOneSeparated(parser, separator).or(success(new ArrayList<>()));
    }

    public static <A, B> Parser<List<A>> atLeastOneSeparated(Parser<A> parser, Parser<B> separator) {
        return and(parser, many(skipLeft(separator, parser)), Utils::addHeadToList);
    }

    public static <A> Parser<Optional<A>> optional(Parser<A> parser) {
        return new Parser<>(location -> {
            Result<A> result = parser.apply(location);
            return result.match(
                    success -> new Success<>(Optional.of(success.getValue()), success.getLocation()),
                    failure -> new Success<>(Optional.empty(), location));
        });
    }

    public static Parser<String> string(String string) {
        return new Parser<>(location -> {
            if (location.getNext().startsWith(string)) {
                return new Success<>(string, location.advanceBy(string.length()));
            } else {
                return new Failure<>("string '" + string + "'", location);
            }
        });
    }

    public static Parser<String> regexp(String regexp) {
        return new Parser<>(location -> {
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(location.getNext());
            if (matcher.find()) {
                return new Success<>(matcher.group(), location.advanceBy(matcher.end()));
            } else {
                return new Failure<>("regexp '" + regexp + "'", location);
            }
        });
    }

    public static Parser<String> word() {
        return regexp("^\\w+").label("word");
    }

    public static Parser<Void> space() {
        return regexp("\\s*")
                .map(w -> (Void) null)
                .label("space");
    }

    public static Parser<Integer> natural() {
        return regexp("^\\d+")
                .map(Integer::parseInt)
                .label("natural number");
    }

    public static Parser<Double> double_() {
        return regexp("^[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?")
                .map(Double::parseDouble)
                .label("floating point number");
    }

    public static Parser<Boolean> boolean_() {
        return regexp("^true|false").map(Boolean::valueOf);
    }

    public static Parser<Void> null_() {
        return string("null")
                .map(v -> (Void) null)
                .label("null");
    }

    public static Parser<String> quoted() {
        return regexp("^\"([^\"])*?\"")
                .map(v -> v.substring(1, v.length() - 1));
    }

    // to be fixed
    public static Parser<String> escaped() {
        return regexp("^\"(\\\"|[^\"])*?\"")
                .map(v -> v.substring(1, v.length() - 1))
                .map(v -> v.replaceAll("\\\\\"", "\""));
    }

    public static Parser<Void> eol() {
        return regexp("\n|\r").map(v -> (Void) null).label("end of line");
    }

    public static Parser<Void> end() {
        return new Parser<>(location -> {
            if (location.reachedEnd())
                return new Success<>(null, location);
            else
                return new Failure<>("end of the string", location);
        });
    }

    public static Parser<String> allUntilEol() {
        return skipRight(regexp("[^\n\r]*"), eol()).label("all until end of line");
    }

    public static <A> Parser<A> token(Parser<A> parser) {
        return surround(space(), parser, space());
    }

    public static Parser<String> token(String value) {
        return token(string(value));
    }
}
