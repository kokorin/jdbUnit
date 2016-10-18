package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.table.Column;
import com.github.kokorin.jdbunit.table.Row;
import com.github.kokorin.jdbunit.table.Table;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Insert implements Operation {
    @Override
    public void execute(List<Table> tables, DataSource dataSource) {
        Objects.requireNonNull(tables);
        Objects.requireNonNull(dataSource);

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            for (Table table : tables) {
                insertTable(table, connection);
            }

            connection.commit();
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
            Column.Type type = columns.get(i).getType();
            statement.setObject(i + 1, value, type.getSqlType());
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
