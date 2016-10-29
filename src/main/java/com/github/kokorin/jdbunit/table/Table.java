package com.github.kokorin.jdbunit.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Table {
    private final String name;
    private final List<Column> columns;
    private final List<Row> rows;

    public Table(String name, List<Column> columns, List<Row> rows) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(columns);
        Objects.requireNonNull(rows);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Table name must be non empty");
        }
        if (columns.isEmpty() && !rows.isEmpty()) {
            throw new IllegalArgumentException("At least one column is required for non-empty table");
        }

        for (Row row : rows) {
            if (row.getValues().size() != columns.size()) {
                throw new IllegalArgumentException("Every row must have value for every column.");
            }
        }

        this.name = name;
        this.columns = Collections.unmodifiableList(new ArrayList<>(columns));
        this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Row> getRows() {
        return rows;
    }
}
