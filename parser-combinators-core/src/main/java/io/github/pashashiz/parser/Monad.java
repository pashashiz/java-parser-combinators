package io.github.pashashiz.parser;

import java.util.function.Function;

public interface Monad<A> {

    <B, MB extends Monad<B>> MB flatMap(Function<A, MB> mapper);

}
