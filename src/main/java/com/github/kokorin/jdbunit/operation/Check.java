package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.Table;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Check implements Operation {
    @Override
    public void execute(List<Table> tables, DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
