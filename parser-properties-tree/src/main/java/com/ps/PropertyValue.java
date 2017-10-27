package com.ps;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;

public class PropertyValue implements PropertyTree {

    private final String value;

    public PropertyValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public PropertyValue setValue(String value) {
        return new PropertyValue(value);
    }

    @Override
    public String joinSymbol() {
        return "=";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyValue that = (PropertyValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public List<String> toStringLines() {
        return singletonList(toString());
    }

}
