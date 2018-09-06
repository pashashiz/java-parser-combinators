package io.github.pashashiz.parser;

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

    public boolean reachedEnd() {
        return offset >= input.length();
    }

    private String inputProbe(int location, int width) {
        int start = Math.max(0, location - width / 2);
        int stop = Math.min(input.length(), location + width / 2);
        return (start > 0 ? "..." + start + "..." : "") +
                input.substring(start, stop) +
                (stop < input.length() ? "..." : "");
    }

    @Override
    public String toString() {
        return "at position " + offset + " in \"" + inputProbe(offset, 50) + "\"";
    }
}
