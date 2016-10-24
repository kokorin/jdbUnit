package com.github.kokorin.jdbunit.table;

import java.util.Objects;

public class Column {
    private final String name;
    private final Type type;

    public Column(String name, Type type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Column name must be non empty");
        }

        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
