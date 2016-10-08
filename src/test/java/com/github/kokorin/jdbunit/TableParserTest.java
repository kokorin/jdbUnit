package com.github.kokorin.jdbunit;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import org.unitils.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class TableParserTest {
    @Test(expected = IllegalArgumentException.class)
    public void errorIfParsingEmptyLine() throws Exception {
        TableParser.parseCells("");
    }

    @Test
    public void parseColumn() throws Exception {
       assertReflectionEquals(new Column("name", "type"), TableParser.parseColumn("name:type"));
       assertReflectionEquals(new Column("name", "String"), TableParser.parseColumn("name"));
    }

    @Test
    public void parseCells() throws Exception {
        assertEquals(singletonList(" "), TableParser.parseCells(" "));
        assertEquals(singletonList(" "), TableParser.parseCells("| "));
        assertEquals(singletonList(" "), TableParser.parseCells(" |"));
        assertEquals(singletonList(" "), TableParser.parseCells("| |"));

        assertEquals(singletonList("value"), TableParser.parseCells("value"));
        assertEquals(singletonList("value"), TableParser.parseCells("|value"));
        assertEquals(singletonList("value"), TableParser.parseCells("value|"));
        assertEquals(singletonList("value"), TableParser.parseCells("|value|"));

        assertEquals(asList("one", "two"), TableParser.parseCells("one|two"));
        assertEquals(asList("one", "two"), TableParser.parseCells("|one|two"));
        assertEquals(asList("one", "two"), TableParser.parseCells("one|two|"));
        assertEquals(asList("one", "two"), TableParser.parseCells("|one|two|"));
    }
}
