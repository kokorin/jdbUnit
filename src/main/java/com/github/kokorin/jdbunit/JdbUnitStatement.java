package com.github.kokorin.jdbunit;

import com.github.kokorin.jdbunit.operation.Delete;
import com.github.kokorin.jdbunit.operation.Insert;
import com.github.kokorin.jdbunit.operation.Operation;
import com.github.kokorin.jdbunit.operation.Verify;
import com.github.kokorin.jdbunit.table.Table;
import org.junit.runners.model.Statement;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class JdbUnitStatement extends Statement {
    //jUnit Statement
    private final Statement base;
    private final Connection connection;
    private final InputStream dataSet;
    private final InputStream expectedDataSet;

    public JdbUnitStatement(Statement base, Connection connection, InputStream dataSet, InputStream expectedDataSet) {
        this.base = base;
        this.connection = connection;
        this.dataSet = dataSet;
        this.expectedDataSet = expectedDataSet;
    }

    @Override
    public void evaluate() throws Throwable {
        deleteAndInsert();
        base.evaluate();
        verifyExpected();
    }

    private void deleteAndInsert() throws Exception {
        if (dataSet == null) {
            return;
        }

        connection.setAutoCommit(false);

        List<Table> tables = TableParser.parseTables(dataSet);
        List<Operation> operations = Arrays.asList(new Delete(), new Insert());

        for (Operation operation : operations) {
            operation.execute(tables, connection);
            connection.commit();
        }
    }

    private void verifyExpected() throws Exception {
        if (expectedDataSet == null) {
            return;
        }

        connection.setAutoCommit(false);

        List<Table> tables = TableParser.parseTables(expectedDataSet);
        new Verify().execute(tables, connection);

        connection.rollback();
    }
}
