package com.github.kokorin.jdbunit;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.sql.*;

import static org.junit.Assert.*;

public class DatabaseTest {
    private DatabaseTest() {
    }

    public static void createTable(Class<?> test, Connection connection) throws Exception {
        try (Statement statement = connection.createStatement()) {
            String filename = test.getSimpleName() + ".sql";
            InputStream sqlsStream = test.getResourceAsStream(filename);
            String sqls = IOUtils.toString(sqlsStream);
            for (String sql : sqls.split(";")) {
                statement.execute(sql);
            }
        }
    }

    public static void deletesAndInserts(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery("SELECT * FROM Test");
            assertTrue(set.next());

            assertEquals(true, set.getBoolean("tBoolean"));
            assertEquals(42, set.getInt("tInt"));
            assertEquals(123, set.getLong("tLong"));
            assertEquals(3.14159f, set.getFloat("tFloat"), 0.0000001f);
            assertEquals(2.71, set.getDouble("tDouble"), 0.00001);
            assertEquals(new Date(148, 11, 31), set.getDate("tDate"));
            assertEquals(new Time(12, 6, 58), set.getTime("tTime"));
            assertEquals(new Timestamp(148, 11, 31, 12, 6, 58, 0), set.getTimestamp("tTimestamp"));
            assertEquals("Hello, world!", set.getString("tString"));

            //Only one row has been returned
            assertFalse(set.next());
        }
    }

    public static void respectsExpected(Connection connection) throws Exception {
        String sql = "INSERT INTO Test (" +
                "tBoolean, tInt, tLong, tFloat, tDouble, tDate, tTime, tTimestamp, tString" +
                ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, false);
            statement.setInt(2, 69);
            statement.setLong(3, 321);
            statement.setFloat(4, 5.0123f);
            statement.setDouble(5, 4.04);
            statement.setDate(6, new Date(86, 9, 25));
            statement.setTime(7, new Time(23, 15, 4));
            statement.setTimestamp(8, new Timestamp(86, 9, 25, 23, 15, 4, 0));
            statement.setString(9, "JdbUnit works!");
            statement.execute();
        }
    }
}
