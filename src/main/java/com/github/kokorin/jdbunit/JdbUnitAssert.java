package com.github.kokorin.jdbunit;

import com.github.kokorin.jdbunit.table.Column;
import com.github.kokorin.jdbunit.table.Row;
import com.github.kokorin.jdbunit.table.Table;

import java.util.*;

import static org.junit.Assert.*;

public class JdbUnitAssert {
    private JdbUnitAssert() {}

    public static void assertTableEquals(List<Table> expectedTables, List<Table> actualTables) {
        assertNotNull("No expected table", expectedTables);
        assertNotNull("No actual table", actualTables);
        assertEquals(expectedTables.size(), actualTables.size());

        Map<String, Table> actualMap = new HashMap<>();
        for (Table actual : actualTables) {
            actualMap.put(actual.getName(), actual);
        }

        Map<String, Object> variables = new HashMap<>();
        for (Table expected : expectedTables) {
            Table actual = actualMap.get(expected.getName());
            assertNotNull("No table among actual: " + expected.getName(), actual);
            assertColumnsEqual(expected.getColumns(), actual.getColumns());
            assertContentsEqual(expected.getRows(), actual.getRows(), variables);
        }
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

    public static void assertContentsEqual(List<Row> expected, List<Row> actual, Map<String, Object> variables) {
        List<Row> notFound = new ArrayList<>(expected);
        List<Row> notExpected = new ArrayList<>(actual);

        for (Iterator<Row> expRowIt = notFound.iterator(); expRowIt.hasNext(); ) {
            Row expRow = expRowIt.next();

            for (Iterator<Row> actRowIt = notExpected.iterator(); actRowIt.hasNext(); ) {
                Row actRow = actRowIt.next();

                boolean equal = true;
                Map<String, Object> captures = new HashMap<>();
                for (int i = 0; i < expRow.getValues().size(); ++i) {
                    Object expValue = expRow.getValues().get(i);
                    Object actValue = actRow.getValues().get(i);

                    if (expValue instanceof Row.ValueCaptor) {
                        Row.ValueCaptor captor = (Row.ValueCaptor) expValue;
                        captures.put(captor.getName(), actValue);
                        continue;
                    }

                    if (expValue == Row.ANY_VALUE) {
                        continue;
                    }

                    if (expValue instanceof Row.ValueReference) {
                        Row.ValueReference reference = (Row.ValueReference) expValue;
                        expValue = variables.get(reference.getName());
                    }

                    if (!Objects.equals(expValue, actValue)) {
                        equal = false;
                        break;
                    }
                }

                if (equal) {
                    actRowIt.remove();
                    expRowIt.remove();
                    variables.putAll(captures);
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
