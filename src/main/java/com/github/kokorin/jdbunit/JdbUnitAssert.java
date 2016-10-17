package com.github.kokorin.jdbunit;

import org.junit.Assert;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class JdbUnitAssert {
    private JdbUnitAssert() {}

    public static void assertTableEquals(Table expected, Table actual) {
        assertNotNull("No expected table", expected);
        assertNotNull("No actual table", actual);
        assertEquals("Table names must be equal", expected.getName(), actual.getName());

        assertColumnsEqual(expected.getColumns(), actual.getColumns());
        assertContentsEqual(expected.getRows(), actual.getRows());
    }

    public static void assertColumnsEqual(List<Column> expected, List<Column> actual) {
        assertEquals("Column counts must be equal", expected.size(), actual.size());
        for (int i = 0; i < expected.size(); ++i) {
            Column expColumn = expected.get(i);
            Column actColumn = actual.get(i);

            assertEquals("Column not found: " + expColumn.getName(), expColumn.getName(), actColumn.getName());
            assertEquals("Column has wrong type: " + actColumn.getName(), expColumn.getType(), actColumn.getType());
        }
    }

    public static void assertContentsEqual(List<Row> expected, List<Row> actual) {
        List<Row> notFound = new ArrayList<>(expected);
        List<Row> notExpected = new ArrayList<>(actual);

        for (Iterator<Row> expRowIt = notFound.iterator(); expRowIt.hasNext(); ) {
            Row expRow = expRowIt.next();

            for (Iterator<Row> actRowIt = notExpected.iterator(); actRowIt.hasNext(); ) {
                Row actRow = actRowIt.next();

                boolean equal = true;
                for (int i = 0; i < expRow.getValues().size(); ++i) {
                    Object expValue = expRow.getValues().get(i);
                    Object actValue = actRow.getValues().get(i);

                    if (!equalsRegardingAny(expValue, actValue)) {
                        equal = false;
                        break;
                    }
                }

                if (equal) {
                    actRowIt.remove();
                    expRowIt.remove();
                    break;
                }
            }
        }

        if (!notFound.isEmpty() || !notExpected.isEmpty()) {
            StringBuilder message = new StringBuilder();

            if (!notFound.isEmpty()) {
                message.append("Not found rows: \n");
                printRows(message, notFound);
            }

            if (!notExpected.isEmpty()) {
                message.append("Not expected rows: \n");
                printRows(message, notExpected);
            }

            fail(message.toString());
        }
    }

    public static boolean equalsRegardingAny(Object expected, Object actual) {
        if (expected == Row.ANY_VALUE) {
            return true;
        }
        return Objects.equals(expected, actual);
    }

    public static void printRows(StringBuilder result, List<Row> rows) {
        for (Row row : rows) {
            for (int i = 0; i < row.getValues().size(); ++i) {
                if (i != 0) {
                    result.append("|");
                }
                Object value = row.getValues().get(i);
                result.append(value);
            }
            result.append("\n");
        }
        result.append("\n");
    }

}
