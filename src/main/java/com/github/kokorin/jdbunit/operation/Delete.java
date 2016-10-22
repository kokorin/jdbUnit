package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.table.Table;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class Delete implements Operation {
    @Override
    public void execute(List<Table> tables, Connection connection) {
        Objects.requireNonNull(tables);
        Objects.requireNonNull(connection);

        //Deletion of existing conect should be done in reverse order
        // to deal with reference integrity
        ListIterator<Table> reverse = tables.listIterator(tables.size());

        try (Statement statement = connection.createStatement()) {
            while (reverse.hasPrevious()) {
                Table table = reverse.previous();
                statement.execute("DELETE FROM " + table.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
