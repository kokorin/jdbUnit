package com.github.kokorin.jdbunit;

import com.github.kokorin.jdbunit.annotation.DataSet;
import com.github.kokorin.jdbunit.annotation.ExpectedDataSet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

@DataSet("Before.md")
public class SQLiteIT {
    private static Connection connection;
    @Rule
    public final JdbUnitRule rule = new JdbUnitRule(connection);

    @BeforeClass
    public static void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        connection.setAutoCommit(false);
        DatabaseTest.createTable(SQLiteIT.class, connection);
        connection.commit();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void deletesAndInserts() throws Exception {
        DatabaseTest.deletesAndInserts(connection);
    }

    @Test
    @ExpectedDataSet("After.md")
    public void respectsExpected() throws Exception {
        DatabaseTest.respectsExpected(connection);
    }
}
