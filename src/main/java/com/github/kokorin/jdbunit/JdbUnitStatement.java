package com.github.kokorin.jdbunit;

import com.github.kokorin.jdbunit.operation.Delete;
import com.github.kokorin.jdbunit.operation.Insert;
import com.github.kokorin.jdbunit.operation.Operation;
import com.github.kokorin.jdbunit.operation.Verify;
import org.junit.runners.model.Statement;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class JdbUnitStatement extends Statement {
    //jUnit Statement
    private final Statement base;
    private final InputStream dataSet;
    private final InputStream expectedDataSet;
    private final DataSource dataSource;

    public JdbUnitStatement(Statement base, InputStream dataSet, InputStream expectedDataSet, DataSource dataSource) {
        this.base = base;
        this.dataSet = dataSet;
        this.expectedDataSet = expectedDataSet;
        this.dataSource = dataSource;
    }

    @Override
    public void evaluate() throws Throwable {
        deleteAndInsert();
        base.evaluate();
        checkExpected();
    }


    private void deleteAndInsert() throws Exception {
        if (dataSet == null) {
            return;
        }

        List<Table> tables = TableParser.parseTables(dataSet);
        List<Operation> operations = Arrays.asList(new Delete(), new Insert());
        for (Operation operation : operations) {
            operation.execute(tables, dataSource);
        }
    }

    private void checkExpected() throws Exception {
        if (expectedDataSet == null) {
            return;
        }

        List<Table> tables = TableParser.parseTables(expectedDataSet);
        new Verify().execute(tables, dataSource);
    }
}
