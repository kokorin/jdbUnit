package com.github.kokorin.jdbunit;

import org.junit.runners.model.Statement;

import javax.sql.DataSource;
import java.io.InputStream;

public class JdbUnitStatement extends Statement {
    private final Statement base;
    private final InputStream initialTalesLocation;
    private final InputStream expectedTalesLocation;
    private final DataSource dataSource;

    public JdbUnitStatement(Statement base, InputStream initialTalesLocation, InputStream expectedTalesLocation, DataSource dataSource) {
        this.base = base;
        this.initialTalesLocation = initialTalesLocation;
        this.expectedTalesLocation = expectedTalesLocation;
        this.dataSource = dataSource;
    }

    @Override
    public void evaluate() throws Throwable {
        boolean successfullyEvaluated = false;

        try {
            deleteAndInsert();
            base.evaluate();
            successfullyEvaluated = true;
        } finally {
            //Only if there was no exception yet
            if (successfullyEvaluated) {
                checkExpected();
            }
        }
    }


    private void deleteAndInsert() throws Exception {

    }

    private void checkExpected() throws Exception {

    }
}
