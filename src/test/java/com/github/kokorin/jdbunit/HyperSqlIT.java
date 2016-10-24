package com.github.kokorin.jdbunit;

import com.github.kokorin.jdbunit.annotation.DataSet;
import com.github.kokorin.jdbunit.annotation.ExpectedDataSet;
import org.junit.*;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DataSet("Before.md")
public class HyperSqlIT {
    private static Connection connection;
    @Rule
    public final JdbUnitRule rule = new JdbUnitRule(connection);

    @BeforeClass
    public static void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:test", "SA", "");
        connection.setAutoCommit(false);
        DatabaseTest.createTable(HyperSqlIT.class, connection);
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
