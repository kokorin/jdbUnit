package com.github.kokorin.jdbunit.table;

import org.junit.Test;

public class ColumnTest {
    @Test(expected = NullPointerException.class)
    public void nonNullName() throws Exception {
        new Column(null, StandardType.BOOLEAN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyName() throws Exception {
        new Column("", StandardType.LONG);
    }

    @Test(expected = NullPointerException.class)
    public void nonNullType() throws Exception {
        new Column("any", null);
    }
}
