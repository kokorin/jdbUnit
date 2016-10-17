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
import java.util.Arrays;
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
        Table table1 = mock(Table.class);
        Table table2 = mock(Table.class);
        List<Table> tables = asList(table1, table2);

        Insert insert = spy(new Insert());
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);

        doNothing().when(insert).insertTable(any(Table.class), any(Connection.class));
        when(dataSource.getConnection()).thenReturn(connection);

        //Actual test call
        insert.execute(tables, dataSource);

        InOrder inOrder = inOrder(insert, dataSource, connection);
        inOrder.verify(dataSource).getConnection();
        inOrder.verify(connection).setAutoCommit(false);
        inOrder.verify(insert).insertTable(table1, connection);
        inOrder.verify(insert).insertTable(table2, connection);
        inOrder.verify(connection).commit();
        inOrder.verify(connection).close();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void insertTable() throws Exception {
        List<Column> columns = asList(
                new Column("id", Column.Type.LONG),
                new Column("name", Column.Type.STRING)
        );
        Row row1 = new Row(Arrays.<Object>asList(123L, "Test"));
        Row row2 = new Row(Arrays.<Object>asList(321L, "Mock"));
        List<Row> rows = asList(row1, row2);
        Table table = new Table("TableName", columns, rows);

        Insert insert = spy(new Insert());
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        doNothing().when(insert).setParameters(any(PreparedStatement.class), anyList(), any(Row.class));
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        insert.insertTable(table, connection);

        InOrder inOrder = inOrder(insert, connection, statement);
        inOrder.verify(insert).insertTable(table, connection);
        inOrder.verify(connection).prepareStatement(anyString());
        inOrder.verify(insert).setParameters(statement, columns, row1);
        inOrder.verify(statement).addBatch();
        inOrder.verify(insert).setParameters(statement, columns, row2);
        inOrder.verify(statement).addBatch();
        inOrder.verify(statement).executeBatch();
        inOrder.verify(statement).close();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void setParameters() throws Exception {
        Insert insert = new Insert();
        PreparedStatement statement = mock(PreparedStatement.class);

        List<Column> columns = asList(
                new Column("id", Column.Type.LONG),
                new Column("name", Column.Type.STRING),
                new Column("password", Column.Type.STRING)
        );
        Row row = new Row(Arrays.<Object>asList(123L, "Name", "Password"));

        insert.setParameters(statement, columns, row);

        InOrder inOrder = inOrder(statement);
        inOrder.verify(statement).setObject(1, 123L, Types.BIGINT);
        inOrder.verify(statement).setObject(2, "Name", Types.VARCHAR);
        inOrder.verify(statement).setObject(3, "Password", Types.VARCHAR);
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
