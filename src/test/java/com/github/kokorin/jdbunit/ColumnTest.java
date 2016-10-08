package com.github.kokorin.jdbunit;

import org.junit.Test;

public class ColumnTest {
    @Test(expected = NullPointerException.class)
    public void nonNullName() throws Exception {
        new Column(null, "any");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyName() throws Exception {
        new Column("", "any");
    }

    @Test(expected = NullPointerException.class)
    public void nonNullType() throws Exception {
        new Column("any", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyType() throws Exception {
        new Column("any", "");
    }
}
