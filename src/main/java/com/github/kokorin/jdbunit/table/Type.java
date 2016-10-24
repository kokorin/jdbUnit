package com.github.kokorin.jdbunit.table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface Type {
    List<String> getAliases();

    Object parse(String string);

    void setParameter(PreparedStatement statement, int index, Object value) throws SQLException;

    Object getValue(ResultSet set, int index) throws SQLException;
}
