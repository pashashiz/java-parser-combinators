package io.github.pashashiz.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FunctionalInterface
public interface Parser<A> {

    Result<A> apply(Location location);

    default Result<A> run(String src) {
        return apply(new Location(src, 0));
    }

    default <B> Parser<B> flatMap(Function<A, Parser<B>> f) {
        return location -> {
            Result<A> result = apply(location);
            return result.match(
                    success -> {
                        Parser<B> next = f.apply(success.getValue());
                        return next.apply(success.getLocation());
                    },
                    Failure::cast);
        };
    }

    default <B> Parser<B> map(Function<A, B> f) {
        return flatMap(result -> success(f.apply(result)));
    }

    default <B> Parser<B> cast(Class<B> clazz) {
        return cast();
    }

    @SuppressWarnings("unchecked")
    default <B> Parser<B> cast() {
        return map(value -> (B) value);
    }

    default Parser<A> label(String label) {
        return location -> {
            Result<A> result = apply(location);
            return result.match(
                    success -> success,
                    failure -> failure.push(label, location));
        };
    }

    default <B, C> Parser<C> map2(Parser<B> other, BiFunction<A, B, C> f) {
        return map2(this, other, f);
    }

    default <B, C> Parser<C> map2(Supplier<Parser<B>> otherLazy, BiFunction<A, B, C> f) {
        return map2(this, otherLazy, f);
    }

    default <B> Parser<Pair<A, B>> product(Parser<B> other) {
        return product(this, other);
    }

    default <B> Parser<B> skipLeft(Parser<B> right) {
        return skipLeft(this, right);
    }

    default <B> Parser<A> skipRight(Parser<B> right) {
        return skipRight(this, right);
    }

    default Parser<A> or(Parser<A> other) {
        return or(this, other);
    }

    default Parser<List<A>> many() {
        return many(this);
    }

    default Parser<List<A>> many1() {
        return many1(this);
    }

    default Parser<Optional<A>> optional() {
        return optional(this);
    }

    static <A> Parser<A> success(A result) {
        return location -> new Success<>(result, location);
    }

    static <A> Parser<A> failure(String error) {
        return location -> new Failure<A>(error, location);
    }

    static <A, B, C> Parser<C> map2(Parser<A> first, Parser<B> second, BiFunction<A, B, C> f) {
        return map2(first, () -> second, f);
    }

    static <A, B, C> Parser<C> map2(Parser<A> first, Supplier<Parser<B>> secondLazy, BiFunction<A, B, C> f) {
        return first.flatMap(currentValue ->
                secondLazy.get().map(otherValue -> f.apply(currentValue, otherValue)));
    }

    static <A, B> Parser<Pair<A, B>> product(Parser<A> left, Parser<B> right) {
        return map2(left, right, Pair::new);
    }

    static <A> Parser<A> skipLeft(Parser<?> left, Parser<A> right) {
        return map2(left, right, (leftValue, rightValue) -> rightValue);
    }

    static <A> Parser<A> skipRight(Parser<A> left, Parser<?> right) {
        return map2(left, right, (leftValue, rightValue) -> leftValue);
    }

    static <A> Parser<A> or(Parser<A> left, Parser<A> right) {
        return location -> {
            Result<A> result = left.apply(location);
            return result.match(
                    success -> success,
                    failure -> right.apply(location));
        };
    }

    static <A> Parser<A> surround(Parser<?> left, Parser<A> parser, Parser<?> right) {
        return skipLeft(left, skipRight(parser, right));
    }

    static <A> Parser<List<A>> many(Parser<A> parser) {
        // The idiomatic functional implementation
        // return or(
        //         map2(parser, () -> many(parser), Utils::addHeadToList),
        //         success(Collections.emptyList()));
        // The imperative one which will work for Java without stack overflow on long lists
        return location -> {
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
        };
    }

    static <A> Parser<List<A>> many1(Parser<A> parser) {
        return map2(parser, () -> many(parser), Utils::addHeadToList);
    }

    static <A> Parser<Optional<A>> optional(Parser<A> parser) {
        return location -> {
            Result<A> result = parser.apply(location);
            return result.match(
                    success -> new Success<>(Optional.of(success.getValue()), success.getLocation()),
                    failure -> new Success<Optional<A>>(Optional.empty(), location));
        };
    }

    static Parser<String> string(String string) {
        return location -> {
            if (location.getNext().startsWith(string)) {
                return new Success<>(string, location.advanceBy(string.length()));
            } else {
                return new Failure<>("string '" + string + "'", location);
            }
        };
    }

    static Parser<String> regexp(String regexp) {
        return location -> {
            Pattern pattern = Pattern.compile(regexp);
            Matcher matcher = pattern.matcher(location.getNext());
            if (matcher.find()) {
                return new Success<>(matcher.group(), location.advanceBy(matcher.end()));
            } else {
                return new Failure<>("regexp '" + regexp + "'", location);
            }
        };
    }

    static Parser<String> word() {
        return regexp("^\\w+").label("word");
    }

    static Parser<String> natural() {
        return regexp("\\d+").label("natural number");
    }

    static Parser<String> whitespace() {
        return regexp("\\s*").label("whitespace");
    }

    static Parser<String> eol() {
        return regexp("\n|\r").label("end of line");
    }

    static Parser<String> allUntilEol() {
        return regexp("[^\n\r]*").skipRight(eol()).label("all until end of line");
    }

    static <A> Parser<A> token(Parser<A> parser) {
        return skipRight(parser, whitespace());
    }
}
