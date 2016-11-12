package com.github.kokorin.jdbunit;

import com.github.kokorin.jdbunit.annotation.DataSet;
import com.github.kokorin.jdbunit.annotation.ExpectedDataSet;
import org.junit.*;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;

@DataSet("Before.md")
public class MySqlIT {
    private static Connection connection;
    @ClassRule
    public static final MySQLContainer mysql = new MySQLContainer();

    @Rule
    public final JdbUnitRule rule = new JdbUnitRule(connection);

    @BeforeClass
    public static void setUp() throws Exception {
        connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
        connection.setAutoCommit(false);
        DatabaseTest.createTable(MySqlIT.class, connection);
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

    @Test
    @ExpectedDataSet("AfterDelete.md")
    public void verifiesEmptyTables() throws Exception {
        DatabaseTest.verifiesEmptyTables(connection);
    }

    @Test
    @DataSet("BeforeWithVars.md")
    @ExpectedDataSet("AfterWithVars.md")
    public void capturesVariables() throws Exception {
        DatabaseTest.capturesVariables(connection);
    }

}
