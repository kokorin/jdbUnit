package com.github.kokorin.jdbunit;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNotNull;

public class TableTest {
    private static List<Column> twoColumns = asList(
            new Column("name1", Column.Type.INTEGER),
            new Column("name2", Column.Type.TIMESTAMP)
    );;
    private static List<Row> noRows = emptyList();

    @Test(expected = NullPointerException.class)
    public void nonNullName() throws Exception {
        new Table(null, twoColumns, noRows);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyName() throws Exception {
        new Table("", twoColumns, noRows);
    }

    @Test(expected = NullPointerException.class)
    public void nonNullColumns() throws Exception {
        new Table("any", null, noRows);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyColumns() throws Exception {
        new Table("any", Collections.<Column>emptyList(), noRows);
    }

    @Test(expected = NullPointerException.class)
    public void nonNullRows() throws Exception {
        new Table("any", twoColumns, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rowsHaveValueForEachColumn() throws Exception {
        Row oneValueRow = new Row(singletonList("zxc"));
        new Table("any", twoColumns, singletonList(oneValueRow));
    }

    @Test
    public void canCreateTableWithNameColumnsRows() throws Exception {
        Table table = new Table("any", twoColumns, noRows);
        assertNotNull(table);
    }
}
