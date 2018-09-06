package io.github.pashashiz.parser.json;

import java.util.Objects;

public class JString implements Json {

    private final String value;

    public JString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JString jString = (JString) o;
        return Objects.equals(value, jString.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return '"' + value + '"';
    }
}
