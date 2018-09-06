package io.github.pashashiz.parser;

import java.util.function.Function;

public interface Result<A> {

    <B> Result<B> map(Function<A, B> mapper);

    default <B> B match(Function<Success<A>, B> onSuccess, Function<Failure<A>, B> onFailure) {
        if (this instanceof Success) {
            return onSuccess.apply((Success<A>) this);
        } else if (this instanceof Failure) {
            return onFailure.apply((Failure<A>) this);
        } else {
            throw new IllegalArgumentException("type " + this.getClass().getCanonicalName() + " is not supported");
        }
    }

}
