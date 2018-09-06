package io.github.pashashiz.parser.json;

import io.github.pashashiz.parser.Result;

public interface Json {

    static Result<Json> parse(String value) {
        return JsonParsers.json().parse(value);
    }
}

