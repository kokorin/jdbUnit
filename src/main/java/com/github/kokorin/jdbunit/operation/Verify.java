package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.table.Column;
import com.github.kokorin.jdbunit.table.Row;
import com.github.kokorin.jdbunit.table.Table;
import com.github.kokorin.jdbunit.table.Type;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.github.kokorin.jdbunit.JdbUnitAssert.assertTableEquals;

public class Verify implements Operation {
    @Override
    public void execute(List<Table> expectedTables, Connection connection) {
        expectedTables = combineAll(expectedTables);

        try {
            List<Table> actualTables = new ArrayList<>(expectedTables.size());
            for (Table expected : expectedTables) {
                Table actual = readTable(connection, expected);
                actualTables.add(actual);
            }

            assertTableEquals(expectedTables, actualTables);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    Table readTable(Connection connection, Table expected) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String query = createSelectQuery(expected);

            try (ResultSet resultSet = statement.executeQuery(query)) {
                List<Column> columns = expected.getColumns();
                List<Row> rows = readRows(resultSet, columns);

                return new Table(expected.getName(), columns, rows);
            }
        }
    }

    List<Row> readRows(ResultSet set, List<Column> columns) throws SQLException {
        List<Row> result = new ArrayList<>();

        int count = columns.size();
        while (set.next()) {
            List<Object> values = new ArrayList<>(count);
            for (int i = 0; i < count; ++i) {
                Type type = columns.get(i).getType();
                Object value = type.getValue(set, i + 1);
                values.add(value);
            }
            result.add(new Row(values));
        }

        return result;
    }

    static List<Table> combineAll(List<Table> tables) {
        Map<String, Integer> index = new HashMap<>();
        List<Table> result = new ArrayList<>();

        for (Table table : tables) {
            Integer i = index.get(table.getName());
            if (i == null) {
                index.put(table.getName(), result.size());
                result.add(table);
                continue;
            }

            Table previous = result.get(i);
            table = combine(previous, table);
            result.set(i, table);
        }

        return result;
    }

    static Table combine(Table first, Table second) {
        Map<Integer, Integer> secondIndexes = new HashMap<>();
        List<Column> columns = new ArrayList<>(first.getColumns());
        List<Row> rows = new ArrayList<>();

        for (int i = 0; i < second.getColumns().size(); ++i) {
            Column secondColumn = second.getColumns().get(i);

            int index = -1;
            for (int j = 0; j < first.getColumns().size(); ++j) {
                Column firstColumn = first.getColumns().get(j);
                if (Objects.equals(firstColumn.getName(), secondColumn.getName())) {
                    index = j;
                    break;
                }
            }

            if (index == -1) {
                index = columns.size();
                columns.add(secondColumn);
            }
            secondIndexes.put(i, index);
        }

        for (Row firstRow : first.getRows()) {
            List<Object> values = new ArrayList<>(firstRow.getValues());
            while (values.size() < columns.size()) {
                values.add(Row.ANY_VALUE);
            }
            rows.add(new Row(values));
        }

        for (Row secondRow : second.getRows()) {
            List<Object> values = Collections.nCopies(columns.size(), Row.ANY_VALUE);
            values = new ArrayList<>(values);
            for (int i = 0; i < secondRow.getValues().size(); ++i) {
                int index = secondIndexes.get(i);
                Object value = secondRow.getValues().get(i);
                values.set(index, value);
            }
            rows.add(new Row(values));
        }

        return new Table(first.getName(), columns, rows);
    }

    static String createSelectQuery(Table expected) {
        StringBuilder result = new StringBuilder("SELECT ");

        if (!expected.getColumns().isEmpty()) {
            for (int i = 0; i < expected.getColumns().size(); ++i) {
                Column column = expected.getColumns().get(i);
                if (i != 0) {
                    result.append(", ");
                }
                result.append(column.getName());
            }
        } else {
            result.append("*");
        }

        result.append(" FROM ").append(expected.getName());

        return result.toString();
    }
}
