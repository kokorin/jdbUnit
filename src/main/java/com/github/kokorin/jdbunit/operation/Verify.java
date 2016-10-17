package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.Column;
import com.github.kokorin.jdbunit.Row;
import com.github.kokorin.jdbunit.Table;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static com.github.kokorin.jdbunit.JdbUnitAssert.assertTableEquals;

public class Verify implements Operation {
    @Override
    public void execute(List<Table> tables, DataSource dataSource) {
        tables = combineAll(tables);

        try (Connection connection = dataSource.getConnection()) {
            for (Table expected : tables) {
                Table actual = readTable(connection, expected);
                assertTableEquals(expected, actual);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    Table readTable(Connection connection, Table expected) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String query = createSelectQuery(expected);

            try (ResultSet resultSet = statement.executeQuery(query)) {
                ResultSetMetaData meta = resultSet.getMetaData();
                List<Column> columns = readColumns(meta);
                List<Row> rows = readRows(resultSet, columns);

                return new Table(expected.getName(), columns, rows);
            }
        }
    }

    List<Column> readColumns(ResultSetMetaData meta) throws SQLException {
        int count = meta.getColumnCount();
        List<Column> result = new ArrayList<>(count);

        for (int i = 0; i < count; ++i) {
            int sqlType = meta.getColumnType(i);
            Column.Type type = Column.Type.fromSqlType(sqlType);
            String name = meta.getColumnName(i);
            result.add(new Column(name, type));
        }

        return result;
    }

    List<Row> readRows(ResultSet set, List<Column> columns) throws SQLException {
        List<Row> result = new ArrayList<>();

        int count = columns.size();
        while (set.next()) {
            List<Object> values = new ArrayList<>(count);
            for (int i = 0; i < count; ++i) {
                Object value = set.getObject(i);
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

        for (int i = 0; i < expected.getColumns().size(); ++i) {
            Column column = expected.getColumns().get(i);
            if (i != 0) {
                result.append(", ");
            }
            result.append(column.getName());
        }

        result.append(" FROM ").append(expected.getName());

        return result.toString();
    }
}
