package io.github.pashashiz.parser.json;

import java.util.Objects;

public class JBoolean implements Json {

    private final boolean value;

    public JBoolean(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JBoolean jBoolean = (JBoolean) o;
        return value == jBoolean.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
