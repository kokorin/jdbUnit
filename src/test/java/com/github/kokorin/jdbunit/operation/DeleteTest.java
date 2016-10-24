package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.table.Column;
import com.github.kokorin.jdbunit.table.Row;
import com.github.kokorin.jdbunit.table.StandardType;
import com.github.kokorin.jdbunit.table.Table;
import org.junit.Test;
import org.mockito.InOrder;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

public class DeleteTest {
    @Test(expected = NullPointerException.class)
    public void nonNullTables() throws Exception {
        Connection connection = mock(Connection.class);
        new Delete().execute(null, connection);
    }

    @Test(expected = NullPointerException.class)
    public void nonNullDataSource() throws Exception {
        new Delete().execute(Collections.<Table>emptyList(), null);
    }

    @Test
    public void execute() throws Exception {
        List<Table> tables = asList(
                new Table("First", singletonList(new Column("id", StandardType.LONG)), Collections.<Row>emptyList()),
                new Table("Second", singletonList(new Column("any", StandardType.STRING)), Collections.<Row>emptyList()),
                new Table("Third", singletonList(new Column("some", StandardType.DATE)), Collections.<Row>emptyList())
        );

        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);

        when(connection.createStatement()).thenReturn(statement);

        //Actual test call
        new Delete().execute(tables, connection);

        InOrder inOrder = inOrder(connection, statement);
        inOrder.verify(statement).execute("DELETE FROM Third");
        inOrder.verify(statement).execute("DELETE FROM Second");
        inOrder.verify(statement).execute("DELETE FROM First");
        inOrder.verify(statement).close();
        inOrder.verifyNoMoreInteractions();
    }
}
