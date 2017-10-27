package com.ps.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class Failure<A> implements Result<A> {

    private final Deque<String> stack;

    public Failure(String error) {
        this.stack = new ArrayDeque<>();
        this.stack.push(error);
    }

    public Failure(Deque<String> stack) {
        this.stack = stack;
    }

    public String getError() {
        return stack.getLast();
    }

    public <B> Failure<B> cast() {
        return new Failure<>(stack);
    }

    public Failure<A> label(String label) {
        Deque<String> newStack = new ArrayDeque<>(stack);
        newStack.push(label);
        return new Failure<>(newStack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Failure<?> failure = (Failure<?>) o;
        return Objects.equals(stack, failure.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack);
    }

    @Override
    public String toString() {
        return "Failure{" +
                "stack=" + stack +
                '}';
    }
}
