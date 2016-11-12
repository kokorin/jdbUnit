package com.github.kokorin.jdbunit.operation;

import com.github.kokorin.jdbunit.JdbUnitAssert;
import com.github.kokorin.jdbunit.table.Column;
import com.github.kokorin.jdbunit.table.Row;
import com.github.kokorin.jdbunit.table.StandardType;
import com.github.kokorin.jdbunit.table.Table;
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
                        new Column("id", StandardType.INTEGER),
                        new Column("name", StandardType.STRING),
                        new Column("active", StandardType.BOOLEAN)
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
                        new Column("login", StandardType.STRING),
                        new Column("name", StandardType.STRING),
                        new Column("password", StandardType.STRING)
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
                        new Column("id", StandardType.LONG)
                ),
                Collections.<Row>emptyList()
        );

        Table expectedUsers = new Table(
                "User",
                asList(
                        new Column("id", StandardType.INTEGER),
                        new Column("name", StandardType.STRING),
                        new Column("active", StandardType.BOOLEAN),
                        new Column("login", StandardType.STRING),
                        new Column("password", StandardType.STRING)
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
        JdbUnitAssert.assertTableEquals(asList(expectedUsers), asList(actualUsers));

        List<Table> tables = asList(users1, other, users2);
        List<Table> expectedAll = asList(expectedUsers, other);
        List<Table> actualAll = Verify.combineAll(tables);
        JdbUnitAssert.assertTableEquals(expectedAll, actualAll);
    }

    @Test
    public void createSelectQuery() throws Exception {
        Table table = new Table(
                "TableName",
                asList(
                        new Column("id", StandardType.INTEGER),
                        new Column("name", StandardType.STRING),
                        new Column("birth", StandardType.DATE)
                ),
                Collections.<Row>emptyList()
        );

        String actual = Verify.createSelectQuery(table);
        String expected = "SELECT id, name, birth FROM TableName";

        assertEquals(expected, actual);
    }

    @Test
    public void createSelectQueryWithoutColumns() throws Exception {
        Table table = new Table(
                "TableName",
                Collections.<Column>emptyList(),
                Collections.<Row>emptyList()
        );

        String actual = Verify.createSelectQuery(table);
        String expected = "SELECT * FROM TableName";

        assertEquals(expected, actual);
    }
}
