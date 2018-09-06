package io.github.pashashiz.parser;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.pashashiz.parser.Parsers.success;

public class Parser<A> {

    private final Function<Location, Result<A>> parse;

    public Parser(Function<Location, Result<A>> parse) {
        this.parse = parse;
    }
    
    public Result<A> apply(Location location) {
        return parse.apply(location);
    }
    
    public Result<A> parse(String src) {
        return apply(new Location(src, 0));
    }

    public <B> Parser<B> cast(Class<B> clazz) {
        return cast();
    }

    @SuppressWarnings("unchecked")
    public <B> Parser<B> cast() {
        return map(value -> (B) value);
    }

    public Parser<A> label(String label) {
        return new Parser<>(location -> {
            Result<A> result = apply(location);
            return result.match(
                    success -> success,
                    failure -> failure.push(label, location));
        });
    }

    public <B> Parser<B> flatMap(Function<A, Parser<B>> f) {
        return new Parser<>(location -> {
            Result<A> result = apply(location);
            return result.match(
                    success -> {
                        Parser<B> next = f.apply(success.getValue());
                        return next.apply(success.getLocation());
                    },
                    Failure::cast);
        });
    }

    public <B> Parser<B> map(Function<A, B> f) {
        return flatMap(result -> success(f.apply(result)));
    }

    public <B, C> Parser<C> and(Parser<B> other, BiFunction<A, B, C> f) {
        return flatMap(currentValue ->
                other.map(otherValue -> f.apply(currentValue, otherValue)));
    }

    public <B, C> Parser<C> and(Supplier<Parser<B>> otherLazy, BiFunction<A, B, C> f) {
        return flatMap(currentValue ->
                otherLazy.get().map(otherValue -> f.apply(currentValue, otherValue)));
    }

    public <B> Parser<Pair<A, B>> product(Parser<B> other) {
        return and(other, Pair::new);
    }

    public <B extends A> Parser<A> recover(Parser<B> other) {
        return new Parser<>(location -> {
            Result<A> result = apply(location);
            return result.match(
                    success1 -> success1,
                    failure1 -> {
                        Result<A> result2 = other.apply(location).map(v -> v);
                        return result2.match(
                                success2 -> success2,
                                failure1::combineWithHead);
                    });
        });
    }

    public <B extends A> Parser<A> or(Parser<B> other) {
        return recover(other);
    }

}
