package com.github.kokorin.jdbunit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Row {
    private final List<String> values;

    public Row(List<String> values) {
        Objects.requireNonNull(values);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("At least one value in a row is required");
        }

        values = Collections.unmodifiableList(new ArrayList<>(values));
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }
}