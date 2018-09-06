package io.github.pashashiz.parser;

import java.util.function.Function;

public interface Functor<A> {

    <B> Functor<B> map(Function<A, B> mapper);
}
