package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.Column;
import com.github.kokorin.jdbunit.Row;
import com.github.kokorin.jdbunit.Table;
import org.junit.Test;
import org.mockito.InOrder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class InsertTest {
    @Test(expected = NullPointerException.class)
    public void nonNullTables() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        new Insert().execute(null, dataSource);
    }

    @Test(expected = NullPointerException.class)
    public void nonNullDataSource() throws Exception {
        new Insert().execute(Collections.<Table>emptyList(), null);
    }

    @Test
    public void execute() throws Exception {
        List<Table> tables = asList(
                new Table(
                        "First",
                        asList(
                                new Column("id", Column.Type.LONG),
                                new Column("username", Column.Type.STRING),
                                new Column("birth", Column.Type.DATE)
                        ),
                        asList(
                                new Row(asList("1", "first user", "2016-10-09")),
                                new Row(asList("3000000000", "secondUser", "1986-09-24")),
                                new Row(asList("42", "last", "2013-11-13"))
                        )
                ),
                new Table(
                        "Second",
                        asList(
                                new Column("id", Column.Type.LONG),
                                new Column("passed", Column.Type.BOOLEAN)
                        ),
                        asList(
                                new Row(asList("1", "true")),
                                new Row(asList("100", "false")),
                                new Row(asList("123123", "other"))
                        )
                )
        );

        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement first = mock(PreparedStatement.class);
        PreparedStatement second = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("INSERT INTO First (id, username, birth) VALUES (?, ?, ?)")).thenReturn(first);
        when(connection.prepareStatement("INSERT INTO Second (id, passed) VALUES (?, ?)")).thenReturn(second);

        //Actual test call
        new Insert().execute(tables, dataSource);

        InOrder inOrder = inOrder(connection, first, second);

        inOrder.verify(connection).setAutoCommit(false);

        inOrder.verify(first).setObject(1, 1L, Types.BIGINT);
        inOrder.verify(first).setObject(2, "first user", Types.VARCHAR);
        inOrder.verify(first).setObject(3, new Date(116, 9, 9), Types.DATE);
        inOrder.verify(first).addBatch();

        inOrder.verify(first).setObject(1, 3_000_000_000L, Types.BIGINT);
        inOrder.verify(first).setObject(2, "secondUser", Types.VARCHAR);
        inOrder.verify(first).setObject(3, new Date(86, 8, 24), Types.DATE);
        inOrder.verify(first).addBatch();

        inOrder.verify(first).setObject(1, 42L, Types.BIGINT);
        inOrder.verify(first).setObject(2, "last", Types.VARCHAR);
        inOrder.verify(first).setObject(3, new Date(113, 10, 13), Types.DATE);
        inOrder.verify(first).addBatch();
        inOrder.verify(first).executeBatch();

        inOrder.verify(second).setObject(1, 1L, Types.BIGINT);
        inOrder.verify(second).setObject(2, true, Types.BIT);
        inOrder.verify(second).addBatch();

        inOrder.verify(second).setObject(1, 100L, Types.BIGINT);
        inOrder.verify(second).setObject(2, false, Types.BIT);
        inOrder.verify(second).addBatch();

        inOrder.verify(second).setObject(1, 123123L, Types.BIGINT);
        inOrder.verify(second).setObject(2, false, Types.BIT);
        inOrder.verify(second).addBatch();
        inOrder.verify(second).executeBatch();

        inOrder.verify(connection).commit();
        inOrder.verify(connection).close();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void buildInsertQuery() throws Exception {
        Table table = new Table(
                "First",
                asList(
                        new Column("id", Column.Type.LONG),
                        new Column("username", Column.Type.STRING),
                        new Column("birth", Column.Type.DATE)
                ),
                Collections.<Row>emptyList()
        );

        assertEquals(
                "INSERT INTO First (id, username, birth) VALUES (?, ?, ?)",
                Insert.buildInsertQuery(table)
        );
    }
}
