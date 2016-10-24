package com.github.kokorin.jdbunit.table;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public enum StandardType implements Type {
    BOOLEAN {
        @Override
        public List<String> getAliases() {
            return Arrays.asList("Bit", "bit", "Boolean", "boolean");
        }

        @Override
        public Object parse(String string) {
            return Boolean.valueOf(string);
        }

        @Override
        public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value, Types.BOOLEAN);
        }

        @Override
        public Object getValue(ResultSet set, int index) throws SQLException {
            boolean notNull = set.getBoolean(index);
            if (set.wasNull()) {
                return null;
            }

            return notNull;
        }
    },

    STRING {
        @Override
        public List<String> getAliases() {
            return Arrays.asList("varchar", "Varchar", "string", "String");
        }

        @Override
        public Object parse(String string) {
            return string;
        }

        @Override
        public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value, Types.VARCHAR);
        }

        @Override
        public Object getValue(ResultSet set, int index) throws SQLException {
            return set.getString(index);
        }
    },

    INTEGER {
        @Override
        public List<String> getAliases() {
            return Arrays.asList("int", "Int", "integer", "Integer");
        }

        @Override
        public Object parse(String string) {
            return Integer.valueOf(string);
        }

        @Override
        public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value, Types.INTEGER);
        }

        @Override
        public Object getValue(ResultSet set, int index) throws SQLException {
            int notNull = set.getInt(index);
            if (set.wasNull()) {
                return null;
            }

            return notNull;
        }
    },

    LONG {
        @Override
        public List<String> getAliases() {
            return Arrays.asList("long", "Long");
        }

        @Override
        public Object parse(String string) {
            return Long.valueOf(string);
        }

        @Override
        public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value, Types.BIGINT);
        }

        @Override
        public Object getValue(ResultSet set, int index) throws SQLException {
            long notNull = set.getLong(index);
            if (set.wasNull()) {
                return null;
            }

            return notNull;
        }
    },

    DOUBLE {
        @Override
        public List<String> getAliases() {
            return Arrays.asList("Double", "double");
        }

        @Override
        public Object parse(String string) {
            return Double.valueOf(string);
        }

        @Override
        public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value, Types.DOUBLE);
        }

        @Override
        public Object getValue(ResultSet set, int index) throws SQLException {
            double notNull = set.getDouble(index);
            if (set.wasNull()) {
                return null;
            }

            return notNull;
        }
    },

    FLOAT {
        @Override
        public List<String> getAliases() {
            return Arrays.asList("float", "Float", "real", "Real");
        }

        @Override
        public Object parse(String string) {
            return Float.valueOf(string);
        }

        @Override
        public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value, Types.FLOAT);
        }

        @Override
        public Object getValue(ResultSet set, int index) throws SQLException {
            float notNull = set.getFloat(index);
            if (set.wasNull()) {
                return null;
            }

            return notNull;
        }
    },

    DATE {
        @Override
        public List<String> getAliases() {
            return Arrays.asList("date", "Date");
        }

        @Override
        public Object parse(String string) {
            return Date.valueOf(string);
        }

        @Override
        public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value, Types.DATE);
        }

        @Override
        public Object getValue(ResultSet set, int index) throws SQLException {
            return set.getDate(index);
        }
    },

    TIME {
        @Override
        public List<String> getAliases() {
            return Arrays.asList("time", "Time");
        }

        @Override
        public Object parse(String string) {
            return Time.valueOf(string);
        }

        @Override
        public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value, Types.TIME);
        }

        @Override
        public Object getValue(ResultSet set, int index) throws SQLException {
            return set.getTime(index);
        }
    },

    TIMESTAMP {
        @Override
        public List<String> getAliases() {
            return Arrays.asList("timestamp", "Timestamp");
        }

        @Override
        public Object parse(String string) {
            return Timestamp.valueOf(string);
        }

        @Override
        public void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
            statement.setObject(index, value, Types.TIMESTAMP);
        }

        @Override
        public Object getValue(ResultSet set, int index) throws SQLException {
            return set.getTimestamp(index);
        }
    }
}
