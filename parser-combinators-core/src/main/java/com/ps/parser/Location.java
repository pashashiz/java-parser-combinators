package com.ps.parser;

public class Location {

    private final String input;
    private final int offset;

    public Location(String input) {
        this(input, 0);
    }

    public Location(String input, int offset) {
        this.input = input;
        this.offset = offset;
    }

    public String getInput() {
        return input;
    }

    public String getNext() {
        return input.substring(offset);
    }

    public int getOffset() {
        return offset;
    }

    public Location advanceBy(int n) {
        return new Location(input, offset + n);
    }

    @Override
    public String toString() {
        return "at position " + offset + " in \"" + input + "\"";
    }
}
