package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.table.Table;

import java.sql.Connection;
import java.util.List;

public interface Operation {
    void execute(List<Table> tables, Connection connection);
}
