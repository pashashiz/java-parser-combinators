package io.github.pashashiz.parser;

import java.util.Objects;

public class Success<A> implements Result<A> {

    private final A value;
    private final Location location;

    public Success(A result, Location location) {
        this.value = result;
        this.location = location;
    }

    public A getValue() {
        return value;
    }

    public Location getLocation() {
        return location;
    }

    public Success<A> advanceBy(int n) {
        return new Success<>(value, location.advanceBy(n));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Success<?> success = (Success<?>) o;
        return Objects.equals(value, success.value) &&
                Objects.equals(location, success.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, location);
    }

    @Override
    public String toString() {
        return "Success{" + value + " " + location + '}';
    }
}
