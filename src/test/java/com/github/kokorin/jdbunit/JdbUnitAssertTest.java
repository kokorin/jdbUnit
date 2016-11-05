package com.github.kokorin.jdbunit;

import com.github.kokorin.jdbunit.table.Column;
import com.github.kokorin.jdbunit.table.Row;
import com.github.kokorin.jdbunit.table.StandardType;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class JdbUnitAssertTest {
    @Test
    public void assertColumnsEqualPass() throws Exception {
        List<Column> expected = asList(
                new Column("first", StandardType.INTEGER),
                new Column("second", StandardType.BOOLEAN)
        );

        List<Column> actual = asList(
                new Column("first", StandardType.INTEGER),
                new Column("second", StandardType.BOOLEAN)
        );

        JdbUnitAssert.assertColumnsEqual(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertColumnsEqualFail1() throws Exception {
        List<Column> expected = asList(
                new Column("first", StandardType.INTEGER)
        );

        List<Column> actual = asList(
                new Column("first", StandardType.INTEGER),
                new Column("unexpectedColumn", StandardType.BOOLEAN)
        );

        JdbUnitAssert.assertColumnsEqual(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertColumnsEqualFail2() throws Exception {
        List<Column> expected = asList(
                new Column("first", StandardType.INTEGER),
                new Column("second", StandardType.BOOLEAN)
        );

        List<Column> actual = asList(
                new Column("first", StandardType.INTEGER),
                new Column("second", StandardType.DOUBLE)
        );

        JdbUnitAssert.assertColumnsEqual(expected, actual);
    }

    @Test
    public void assertContentsPass() throws Exception {
        List<Row> expected = asList(
                new Row(Arrays.<Object>asList(
                        Row.ANY_VALUE, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 314
                ))
        );

        List<Row> actual = asList(
                new Row(Arrays.<Object>asList(
                        false, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 314
                ))
        );

        JdbUnitAssert.assertContentsEqual(expected, actual, new HashMap<String, Object>());
    }

    @Test(expected = AssertionError.class)
    public void assertContentsFail1() throws Exception {
        List<Row> expected = asList(
                new Row(Arrays.<Object>asList(
                        null, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 314
                ))
        );

        List<Row> actual = asList(
                new Row(Arrays.<Object>asList(
                        false, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 314
                ))
        );

        JdbUnitAssert.assertContentsEqual(expected, actual, new HashMap<String, Object>());
    }

    @Test(expected = AssertionError.class)
    public void assertContentsFail2() throws Exception {
        List<Row> expected = asList(
                new Row(Arrays.<Object>asList(
                        null, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 314
                )),
                new Row(Arrays.<Object>asList(
                        false, "not found row", 505
                ))
        );

        List<Row> actual = asList(
                new Row(Arrays.<Object>asList(
                        null, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 314
                ))
        );

        JdbUnitAssert.assertContentsEqual(expected, actual, new HashMap<String, Object>());
    }

    @Test(expected = AssertionError.class)
    public void assertContentsFail3() throws Exception {
        List<Row> expected = asList(
                new Row(Arrays.<Object>asList(
                        null, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 314
                ))
        );

        List<Row> actual = asList(
                new Row(Arrays.<Object>asList(
                        null, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 314
                )),
                new Row(Arrays.<Object>asList(
                        false, "not expected row", 404
                ))
        );

        JdbUnitAssert.assertContentsEqual(expected, actual, new HashMap<String, Object>());
    }

    @Test
    public void assertContentsEqualWithReference() throws Exception {
        List<Row> expected = asList(
                new Row(Arrays.<Object>asList(
                        null, "some text", new Row.ValueCaptor("X")
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", new Row.ValueReference("X")
                ))
        );

        List<Row> actual = asList(
                new Row(Arrays.<Object>asList(
                        null, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 42
                ))
        );

        Map<String, Object> variables = new HashMap<>();
        JdbUnitAssert.assertContentsEqual(expected, actual, variables);
        assertEquals(42, variables.get("X"));
    }

    @Test(expected = AssertionError.class)
    public void assertContentsFailWithReference() throws Exception {
        List<Row> expected = asList(
                new Row(Arrays.<Object>asList(
                        null, "some text", new Row.ValueCaptor("X")
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", new Row.ValueReference("X")
                ))
        );

        List<Row> actual = asList(
                new Row(Arrays.<Object>asList(
                        null, "some text", 42
                )),
                new Row(Arrays.<Object>asList(
                        true, "some other text", 13
                ))
        );

        Map<String, Object> variables = new HashMap<>();
        JdbUnitAssert.assertContentsEqual(expected, actual, variables);
        assertEquals(42, variables.get("X"));
    }

}
