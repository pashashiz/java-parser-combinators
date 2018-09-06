package io.github.pashashiz.parser.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JObject implements Json {

    private final Map<String, Json> values;

    public JObject(Map<String, Json> values) {
        this.values = values;
    }

    public JObject(String key, Json value) {
        values = new HashMap<>();
        values.put(key, value);
    }

    public Map<String, Json> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JObject jObject = (JObject) o;
        return Objects.equals(values, jObject.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return '{' + values.entrySet().stream()
                .map(e -> '"' + e.getKey() + "\": " + e.getValue())
                .collect(Collectors.joining(", ")) +
                '}';
    }
}
