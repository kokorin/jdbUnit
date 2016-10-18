package com.github.kokorin.jdbunit.h2;

import com.github.kokorin.jdbunit.JdbUnitRule;
import com.github.kokorin.jdbunit.annotation.DataSet;
import com.github.kokorin.jdbunit.annotation.ExpectedDataSet;
import org.apache.commons.io.IOUtils;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;

@DataSet
public class H2IT {
    public static JdbcDataSource h2DataSource = new JdbcDataSource();

    @Rule
    public final JdbUnitRule rule = new JdbUnitRule(h2DataSource);

    @BeforeClass
    public static void setupDataSource() throws Throwable {
        h2DataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        InputStream sqlStream = H2IT.class.getResourceAsStream("H2IT.sql");
        String sql = IOUtils.toString(sqlStream);

        try (Connection connection = h2DataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

    @Test
    @ExpectedDataSet("H2IT.md")
    public void respectExpected() throws Exception {
        try (Connection connection = h2DataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                boolean result = statement.execute("SELECT COUNT(*) FROM Test");
                assertTrue(result);
            }
        }
    }

    @Test
    public void inserts() throws Exception {

    }
}
