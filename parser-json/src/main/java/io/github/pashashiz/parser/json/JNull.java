package io.github.pashashiz.parser.json;

public class JNull implements Json {

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JNull;
    }

    @Override
    public String toString() {
        return "null";
    }
}
