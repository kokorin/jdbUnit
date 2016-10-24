package com.github.kokorin.jdbunit.table;

import org.junit.Test;

import java.util.Collections;

public class RowTest {
    @Test(expected = NullPointerException.class)
    public void nonNullValues() {
        new Row(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonEmptyValues() throws Exception {
        new Row(Collections.<Object>emptyList());
    }
}
