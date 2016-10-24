package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.table.Column;
import com.github.kokorin.jdbunit.table.Row;
import com.github.kokorin.jdbunit.table.Table;
import com.github.kokorin.jdbunit.table.Type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Insert implements Operation {
    @Override
    public void execute(List<Table> tables, Connection connection) {
        Objects.requireNonNull(tables);
        Objects.requireNonNull(connection);

        try {
            for (Table table : tables) {
                insertTable(table, connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void insertTable(Table table, Connection connection) throws SQLException {
        String query = buildInsertQuery(table);

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            for (Row row : table.getRows()) {
                setParameters(statement, table.getColumns(), row);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    void setParameters(PreparedStatement statement, List<Column> columns, Row row) throws SQLException {
        List<Object> values = row.getValues();
        for (int i = 0; i < values.size(); ++i) {
            Object value = values.get(i);
            Type type = columns.get(i).getType();
            type.setParameter(statement, i + 1, value);
        }
    }

    static String buildInsertQuery(Table table) {
        //It seems that not all DBs support SQL-92 standard
        // regarding multiline insert, so insert line by line
        StringBuilder names = new StringBuilder();
        StringBuilder params = new StringBuilder();

        boolean first = true;
        for (Column column : table.getColumns()) {
            if (!first) {
                names.append(", ");
                params.append(", ");
            }
            names.append(column.getName());
            params.append("?");
            first = false;
        }

        String result = "INSERT INTO " + table.getName()
                + " (" + names.toString() + ")"
                + " VALUES (" + params.toString() + ")";

        return result;
    }
}
