package com.github.kokorin.jdbunit.table;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class TableTest {
    private static List<Column> twoColumns = Arrays.asList(
            new Column("name1", StandardType.BOOLEAN),
            new Column("name2", StandardType.STRING)
    );;
    private static List<Row> twoRows = Arrays.asList(
            new Row(Arrays.<Object>asList(Boolean.TRUE, "123")),
            new Row(Arrays.<Object>asList(Boolean.FALSE, "strinmgh"))
    );

    @Test(expected = NullPointerException.class)
    public void nonNullName() throws Exception {
        new Table(null, twoColumns, twoRows);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyName() throws Exception {
        new Table("", twoColumns, twoRows);
    }

    @Test(expected = NullPointerException.class)
    public void nonNullColumns() throws Exception {
        new Table("any", null, twoRows);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyColumns() throws Exception {
        new Table("any", Collections.<Column>emptyList(), twoRows);
    }

    @Test(expected = NullPointerException.class)
    public void nonNullRows() throws Exception {
        new Table("any", twoColumns, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rowsHaveValueForEachColumn() throws Exception {
        Row oneValueRow = new Row(Collections.<Object>singletonList("zxc"));
        new Table("any", twoColumns, Collections.singletonList(oneValueRow));
    }

    @Test
    public void canCreateFilledTable() throws Exception {
        Table table = new Table("any", twoColumns, twoRows);
        assertNotNull(table);
    }

    @Test
    public void canCreateEmptyTable() throws Exception {
        Table table = new Table("any", Collections.<Column>emptyList(), Collections.<Row>emptyList());
        assertNotNull(table);
    }
}
