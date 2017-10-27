package com.ps.parser;

import java.util.*;
import java.util.stream.Collectors;

public class Failure<A> implements Result<A> {

    private final Deque<Pair<String, Location>> stack;

    public Failure(String error, Location location) {
        this.stack = new ArrayDeque<>();
        this.stack.push(new Pair<>(error, location));
    }

    public Failure(Deque<Pair<String, Location>> stack) {
        this.stack = stack;
    }

    public Deque<Pair<String, Location>> getStack() {
        return stack;
    }

    public String getErrorCause() {
        return stack.getLast().getLeft();
    }

    public <B> Failure<B> cast() {
        return new Failure<>(stack);
    }

    public Failure<A> push(String error, Location location) {
        Deque<Pair<String, Location>> newStack = new ArrayDeque<>(stack);
        newStack.push(new Pair<>(error, location));
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
        printStackTrace();
        return "Failure{" + getStackTrace().get(0) + "}";
    }

    public List<String> getStackTrace() {
        ArrayList<Pair<String, Location>> reversed = new ArrayList<>(stack);
        Collections.reverse(reversed);
        return reversed.stream().map(pair ->
                "expected " + pair.getLeft() + " " + replaceNewLines(pair.getRight()))
                .collect(Collectors.toList());
    }

    public void printStackTrace() {
        System.err.println("Failure: \n" + getStackTrace().stream().collect(Collectors.joining("\n")));
    }

    private Location replaceNewLines(Location location) {
        String replaced = location.getInput().replace("\n", "\\n");
        return new Location(replaced, location.getOffset());
    }
}
