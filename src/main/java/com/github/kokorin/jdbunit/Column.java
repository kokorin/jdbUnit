package com.github.kokorin.jdbunit;

import java.util.Objects;

public class Column {
    private final String name;
    private final String type;

    public Column(String name, String type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Column name must be non empty");
        }
        if (type.isEmpty()) {
            throw new IllegalArgumentException("Column type must be non empty");
        }

        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
