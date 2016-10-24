package com.github.kokorin.jdbunit;

import com.github.kokorin.jdbunit.table.*;
import org.junit.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
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
    public void defaultColumnType() throws Exception {
        assertReflectionEquals(
                new Column("any", StandardType.STRING),
                TableParser.parseColumn("any")
        );

    }

    @Test
    public void parseHeader() throws Exception {
        assertReflectionEquals(
                asList(new Column("column1", StandardType.STRING), new Column("column2", StandardType.LONG)),
                TableParser.parseHeader(" column1 | column2:long ")
        );
    }

    @Test
    public void parseColumn() throws Exception {
        assertReflectionEquals(new Column("name", StandardType.INTEGER), TableParser.parseColumn("name:int"));
        assertReflectionEquals(new Column("name", StandardType.STRING), TableParser.parseColumn("name:varchar"));
        assertReflectionEquals(new Column("name", StandardType.LONG), TableParser.parseColumn(" name:Long "));
        assertReflectionEquals(new Column("name", StandardType.DATE), TableParser.parseColumn(" name : Date "));
    }

    @Test
    public void parseRow() throws Exception {
        List<Column> columns = asList(
                new Column("c1", StandardType.INTEGER),
                new Column("c2", StandardType.STRING),
                new Column("c3", StandardType.LONG),
                new Column("c4", StandardType.BOOLEAN)
        );

        Row expected = new Row(Arrays.<Object>asList(234, "some", 256L, false));
        assertReflectionEquals(expected, TableParser.parseRow("234|some|256|no", columns));
    }

    @Test
    public void parseValue() throws Exception {
        Object[][] data = {
                {"text", StandardType.STRING, "text"},
                {"", StandardType.STRING, null},
                {"123", StandardType.INTEGER, 123},
                {"", StandardType.INTEGER, null},
                {"321", StandardType.LONG, 321L},
                {"true", StandardType.BOOLEAN, true},
                {"", StandardType.BOOLEAN, null},
                {"3.1415", StandardType.FLOAT, 3.1415f},
                {"3.1415926", StandardType.DOUBLE, 3.1415926},
                {"1986-12-24", StandardType.DATE, new Date(86, 11, 24)},
                {"15:38:07", StandardType.TIME, new Time(15, 38, 7)},
                {"1986-12-24 15:38:07.042", StandardType.TIMESTAMP, new Timestamp(86, 11, 24, 15, 38, 7, 42_000_000)}
        };

        Set<? extends Type> notCheckedTypes = new HashSet<>(asList(StandardType.values()));
        for (Object[] test : data) {
            String text = (String) test[0];
            Type type = (Type) test[1];
            Object expected = test[2];

            assertEquals(expected, TableParser.parseValue(text, type));
            notCheckedTypes.remove(type);
        }

        assertEquals("The next types hasn't been checked: " + notCheckedTypes, 0, notCheckedTypes.size());
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
        List<Column> expectedColumns = asList(
                new Column("col1", StandardType.INTEGER),
                new Column("col2", StandardType.STRING)
        );
        List<Row> expectedRows = asList(new Row(Arrays.<Object>asList(123, "val12")), new Row(Arrays.<Object>asList(321, "val22")));
        List<String> lines = asList(
                "                          ",
                " name                     ",
                "==========================",
                "| col1:int | col2:string |",
                "|------------|------------|",
                "|     123    |    val12   |",
                "|     321    |    val22   |"
        );

        assertReflectionEquals(
                new Table("name", expectedColumns, expectedRows),
                TableParser.parseTable(lines.iterator())
        );

    }
}
