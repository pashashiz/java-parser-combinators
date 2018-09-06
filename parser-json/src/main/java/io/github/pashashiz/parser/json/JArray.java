package io.github.pashashiz.parser.json;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class JArray implements Json {

    private final List<Json> values;

    public JArray(List<Json> values) {
        this.values = values;
    }

    public JArray(Json... values) {
        this.values = asList(values);
    }

    public List<Json> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JArray jArray = (JArray) o;
        return Objects.equals(values, jArray.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return '[' + values.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")) +
                ']';
    }
}
