package com.github.kokorin.jdbunit;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class TableParserTest {
    @Test(expected = IllegalArgumentException.class)
    public void errorIfParsingEmptyLine() throws Exception {
        TableParser.parseCells("");
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

        assertEquals(asList(" with  spaces 1 ", " with  spaces 2 "), TableParser.parseCells(" with  spaces 1 | with  spaces 2 "));
    }

    @Test
    public void parseHeader() throws Exception {
        assertReflectionEquals(
                asList(new Column("column1", "String"), new Column("column2", "type2")),
                TableParser.parseHeader(" column1 | column2:type2 ")
        );
    }

    @Test
    public void parseColumn() throws Exception {
        assertReflectionEquals(new Column("name", "type"), TableParser.parseColumn("name:type"));
        assertReflectionEquals(new Column("name", "String"), TableParser.parseColumn("name"));
        assertReflectionEquals(new Column("name", "type"), TableParser.parseColumn(" name:type "));
        assertReflectionEquals(new Column("name", "String"), TableParser.parseColumn(" name "));
    }

    @Test
    public void isHeaderSeparator() throws Exception {
        assertTrue(TableParser.isHeaderSeparator("="));
        assertTrue(TableParser.isHeaderSeparator("======"));
        assertTrue(TableParser.isHeaderSeparator(" ="));
        assertTrue(TableParser.isHeaderSeparator("= "));
        assertTrue(TableParser.isHeaderSeparator("  ===  "));

        assertFalse(TableParser.isHeaderSeparator("=-="));
        assertFalse(TableParser.isHeaderSeparator("  =-=  "));

    }

    @Test
    public void isContentSeparator() throws Exception {
        assertTrue(TableParser.isContentSeparator("|---|", 1));
        assertTrue(TableParser.isContentSeparator("|:---|", 1));
        assertTrue(TableParser.isContentSeparator("|---:|", 1));
        assertTrue(TableParser.isContentSeparator("|:---:|", 1));

        assertTrue(TableParser.isContentSeparator("-----", 1));
        assertTrue(TableParser.isContentSeparator(":---", 1));
        assertTrue(TableParser.isContentSeparator("---:", 1));
        assertTrue(TableParser.isContentSeparator(":---:", 1));

        assertTrue(TableParser.isContentSeparator("---|:---|:---:|---:", 4));

        assertFalse(TableParser.isContentSeparator("-", 1));
        assertFalse(TableParser.isContentSeparator("--", 1));
        assertFalse(TableParser.isContentSeparator("abc", 1));
        assertFalse(TableParser.isContentSeparator("123", 1));
        assertFalse(TableParser.isContentSeparator("---|:---|:123:|---:", 4));
    }

    @Test
    public void parseTable() throws Exception {
        List<Column> expectedColumns = asList(new Column("col1", "type1"), new Column("col2", "type2"));
        List<Row> expectedRows = asList(new Row(asList("val11", "val12")), new Row(asList("val21", "val22")));
        List<String> lines = asList(
                "                          ",
                " name                     ",
                "==========================",
                "| col1:type1 | col2:type2 |",
                "|------------|------------|",
                "|    val11   |    val12   |",
                "|    val21   |    val22   |"
        );

        assertReflectionEquals(
                new Table("name", expectedColumns, expectedRows),
                TableParser.parseTable(lines.iterator())
        );

    }
}
