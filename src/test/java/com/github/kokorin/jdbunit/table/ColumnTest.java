package com.github.kokorin.jdbunit.table;

import com.github.kokorin.jdbunit.table.Column;
import org.junit.Test;

public class ColumnTest {
    @Test(expected = NullPointerException.class)
    public void nonNullName() throws Exception {
        new Column(null, Column.Type.BOOLEAN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyName() throws Exception {
        new Column("", Column.Type.LONG);
    }

    @Test(expected = NullPointerException.class)
    public void nonNullType() throws Exception {
        new Column("any", null);
    }
}
