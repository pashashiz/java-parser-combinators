package com.ps.parser;

import java.util.function.Function;

public interface Result<A> {

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
