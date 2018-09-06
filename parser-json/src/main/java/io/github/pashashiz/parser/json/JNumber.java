package io.github.pashashiz.parser.json;

import java.util.Objects;

public class JNumber implements Json {

    private final double value;

    public JNumber(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JNumber jNumber = (JNumber) o;
        return Double.compare(jNumber.value, value) == 0;
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
