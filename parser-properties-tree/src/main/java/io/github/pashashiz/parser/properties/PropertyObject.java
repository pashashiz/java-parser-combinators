package io.github.pashashiz.parser.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class PropertyObject implements PropertyTree {

    private final Map<String, PropertyTree> properties;

    public PropertyObject() {
        this.properties = new HashMap<>();
    }

    public PropertyObject(Map<String, PropertyTree> properties) {
        this.properties = properties;
    }

    public Map<String, PropertyTree> getAll() {
        return properties;
    }

    public PropertyObject setAll(Map<String, PropertyTree> properties) {
        return new PropertyObject(properties);
    }

    public PropertyObject add(String key, PropertyTree value) {
        HashMap<String, PropertyTree> newProperties = new HashMap<>(properties);
        newProperties.put(key, value);
        return new PropertyObject(newProperties);
    }

    @Override
    public String joinSymbol() {
        return ".";
    }

    @Override
    public PropertyTree merge(PropertyTree other) {
        if (!(other instanceof PropertyObject))
            throw new IllegalArgumentException("merging works for the same type only");
        Map<String, PropertyTree> mergedProperties = new HashMap<>(properties);
            ((PropertyObject) other).properties.forEach((key, value)
                    -> mergedProperties.merge(key, value, PropertyTree::merge));
        return new PropertyObject(mergedProperties);
    }

    @Override
    public List<String> toStringLines() {
        return properties.entrySet().stream()
                .flatMap(entry -> replicate(entry.getValue(), (value, join) -> entry.getKey() + join + value))
                .collect(Collectors.toList());
    }

    private Stream<String> replicate(PropertyTree property, BiFunction<String, String, String> mapper) {
        return property.toStringLines().stream().map(line -> mapper.apply(line, property.joinSymbol()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyObject that = (PropertyObject) o;
        return Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public String toString() {
        return toStringLines().stream().collect(joining("\n"));
    }
}
