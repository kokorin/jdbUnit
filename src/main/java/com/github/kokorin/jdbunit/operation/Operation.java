package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.table.Table;

import javax.sql.DataSource;
import java.util.List;

public interface Operation {
    void execute(List<Table> tables, DataSource dataSource);
}
