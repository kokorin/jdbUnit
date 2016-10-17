package com.github.kokorin.jdbunit;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class JdbUnitAssertTest {
    @Test
    public void assertColumnsEqualPass() throws Exception {
        List<Column> expected = asList(
                new Column("first", Column.Type.INTEGER),
                new Column("second", Column.Type.BOOLEAN)
        );

        List<Column> actual = asList(
                new Column("first", Column.Type.INTEGER),
                new Column("second", Column.Type.BOOLEAN)
        );

        JdbUnitAssert.assertColumnsEqual(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertColumnsEqualFail1() throws Exception {
        List<Column> expected = asList(
                new Column("first", Column.Type.INTEGER)
        );

        List<Column> actual = asList(
                new Column("first", Column.Type.INTEGER),
                new Column("unexpectedColumn", Column.Type.BOOLEAN)
        );

        JdbUnitAssert.assertColumnsEqual(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertColumnsEqualFail2() throws Exception {
        List<Column> expected = asList(
                new Column("first", Column.Type.INTEGER),
                new Column("second", Column.Type.BOOLEAN)
        );

        List<Column> actual = asList(
                new Column("first", Column.Type.INTEGER),
                new Column("second", Column.Type.DOUBLE)
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

        JdbUnitAssert.assertContentsEqual(expected, actual);
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

        JdbUnitAssert.assertContentsEqual(expected, actual);
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

        JdbUnitAssert.assertContentsEqual(expected, actual);
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

        JdbUnitAssert.assertContentsEqual(expected, actual);
    }

}
