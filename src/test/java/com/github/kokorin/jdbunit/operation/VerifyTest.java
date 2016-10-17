package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.Column;
import com.github.kokorin.jdbunit.JdbUnitAssert;
import com.github.kokorin.jdbunit.Row;
import com.github.kokorin.jdbunit.Table;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class VerifyTest {
    @Test
    public void combine() throws Exception {
        Table users1 = new Table(
                "User",
                asList(
                        new Column("id", Column.Type.INTEGER),
                        new Column("name", Column.Type.STRING),
                        new Column("active", Column.Type.BOOLEAN)
                ),
                asList(
                        new Row(Arrays.<Object>asList(
                                123, "admin", true
                        )),
                        new Row(Arrays.<Object>asList(
                                234, "tester", false
                        ))
                )
        );

        Table users2 = new Table(
                "User",
                asList(
                        new Column("login", Column.Type.STRING),
                        new Column("name", Column.Type.STRING),
                        new Column("password", Column.Type.STRING)
                ),
                asList(
                        new Row(Arrays.<Object>asList(
                                "lordsnow", "Jon Snow", "youKnowNothing"
                        )),
                        new Row(Arrays.<Object>asList(
                                "dwarf", "Tyrion Lannister", "witAndIntellect"
                        ))
                )
        );

        Table other = new Table(
                "Other",
                singletonList(
                        new Column("id", Column.Type.LONG)
                ),
                Collections.<Row>emptyList()
        );

        Table expectedUsers = new Table(
                "User",
                asList(
                        new Column("id", Column.Type.INTEGER),
                        new Column("name", Column.Type.STRING),
                        new Column("active", Column.Type.BOOLEAN),
                        new Column("login", Column.Type.STRING),
                        new Column("password", Column.Type.STRING)
                ),
                asList(
                        new Row(asList(
                                123, "admin", true, Row.ANY_VALUE, Row.ANY_VALUE
                        )),
                        new Row(asList(
                                234, "tester", false, Row.ANY_VALUE, Row.ANY_VALUE
                        )),
                        new Row(asList(
                                Row.ANY_VALUE, "Jon Snow", Row.ANY_VALUE, "lordsnow", "youKnowNothing"
                        )),
                        new Row(asList(
                                Row.ANY_VALUE, "Tyrion Lannister", Row.ANY_VALUE, "dwarf", "witAndIntellect"
                        ))
                )
        );

        Table actualUsers = Verify.combine(users1, users2);
        JdbUnitAssert.assertTableEquals(expectedUsers, actualUsers);

        List<Table> tables = asList(users1, other, users2);
        List<Table> expectedAll = asList(expectedUsers, other);
        List<Table> actualAll = Verify.combineAll(tables);

        assertEquals(expectedAll.size(), actualAll.size());
        for (int i = 0; i < expectedAll.size(); ++i) {
            Table expTable = expectedAll.get(i);
            Table actTable = actualAll.get(i);

            JdbUnitAssert.assertTableEquals(expTable, actTable);
        }
    }

    @Test
    public void createSelectQuery() throws Exception {
        Table table = new Table(
                "TableName",
                asList(
                        new Column("id", Column.Type.INTEGER),
                        new Column("name", Column.Type.STRING),
                        new Column("birth", Column.Type.DATE)
                ),
                Collections.<Row>emptyList()
        );

        String actual = Verify.createSelectQuery(table);
        String expected = "SELECT id, name, birth FROM TableName";

        assertEquals(expected, actual);
    }
}
