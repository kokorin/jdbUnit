package com.github.kokorin.jdbunit.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Row {
    private final List<Object> values;

    public static final Object ANY_VALUE = new Object(){
        @Override
        public String toString() {
            return "ANY_VALUE";
        }
    };

    public Row(List<Object> values) {
        Objects.requireNonNull(values);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("At least one value in a row is required");
        }

        values = Collections.unmodifiableList(new ArrayList<>(values));
        this.values = values;
    }

    public List<Object> getValues() {
        return values;
    }

    public static class ValueCaptor {
        private final String name;

        public ValueCaptor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Cap:" + name + ":";
        }
    }

    public static class ValueReference {
        private final String name;

        public ValueReference(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Ref=" + name + "=";
        }
    }
}
