package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.Table;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class Delete implements Operation {
    @Override
    public void execute(List<Table> tables, DataSource dataSource) {
        Objects.requireNonNull(tables);
        Objects.requireNonNull(dataSource);

        //Deletion of existing conect should be done in reverse order
        // to deal with reference integrity
        ListIterator<Table> reverse = tables.listIterator(tables.size());

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement()) {
                while (reverse.hasPrevious()) {
                    Table table = reverse.previous();
                    statement.execute("DELETE FROM " + table.getName());
                }
            }

            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
