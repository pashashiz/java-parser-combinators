package io.github.pashashiz.parser.properties;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class PropertyList implements PropertyTree {

    private final List<PropertyTree> properties;

    public PropertyList() {
        this.properties = new ArrayList<>();
    }

    public PropertyList(List<PropertyTree> properties) {
        this.properties = properties;
    }

    public List<PropertyTree> getAll() {
        return properties;
    }

    public PropertyList setAll(List<PropertyTree> properties) {
        return new PropertyList(properties);
    }

    public PropertyList add(PropertyTree value) {
        List<PropertyTree> newProperties = new ArrayList<>(properties);
        newProperties.add(value);
        return new PropertyList(newProperties);
    }

    public PropertyList add(int index, PropertyTree value) {
        List<PropertyTree> newProperties = new ArrayList<>(index + 1);
        newProperties.addAll(properties);
        ensureCapacity(newProperties, index + 1);
        newProperties.set(index, value);
        return new PropertyList(newProperties);
    }

    private void ensureCapacity(List<PropertyTree> newProperties, int size) {
        if (newProperties.size() < size) {
            IntStream.range(newProperties.size(), size).forEach(i -> newProperties.add(null));
        }
    }

    @Override
    public String joinSymbol() {
        return "";
    }

    @Override
    public PropertyTree merge(PropertyTree other) {
        if (!(other instanceof PropertyList))
            throw new IllegalArgumentException("merging works for the same type only");
        int maxSize = Math.max(properties.size(), ((PropertyList) other).properties.size());
        List<PropertyTree>  mergedProperties = new ArrayList<>(properties);
        ensureCapacity(mergedProperties, maxSize);
        IntStream.range(0, maxSize).forEach(i -> {
            PropertyTree left = properties.size() > i
                    ? properties.get(i)
                    : null;
            PropertyTree right = ((PropertyList) other).properties.size() > i
                    ? ((PropertyList) other).properties.get(i)
                    : null;
            if (left != null && right == null)
                mergedProperties.set(i, left);
            if (right != null && left == null)
                mergedProperties.set(i, right);
            else if (right != null && left != null) {
                mergedProperties.set(i, left.merge(right));
            }
        });
        return new PropertyList(mergedProperties);
    }

    @Override
    public List<String> toStringLines() {
        return IntStream.range(0, properties.size())
                .boxed()
                .flatMap(i -> replicate(properties.get(i), (value, join) -> "[" + i + "]" + join + value))
                .collect(Collectors.toList());
    }

    private Stream<String> replicate(PropertyTree property, BiFunction<String, String, String> mapper) {
        if (property == null)
            return Stream.empty();
        return property.toStringLines().stream().map(line -> mapper.apply(line, property.joinSymbol()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyList that = (PropertyList) o;
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
