package com.github.kokorin.jdbunit;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Column {
    private final String name;
    private final Type type;

    public Column(String name, Type type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Column name must be non empty");
        }

        this.name = name;
        this.type = type;
    }

    public enum Type {
        BOOLEAN(Types.BIT, "bit", "Bit", "boolean", "Boolean") {
            @Override
            protected Object parseNonNull(String value) {
                return Boolean.valueOf(value);
            }
        },
        INTEGER(Types.INTEGER, "int", "Int", "integer", "Integer") {
            @Override
            protected Object parseNonNull(String value) {
                return Integer.valueOf(value);
            }
        },
        LONG(Types.BIGINT, "long", "Long") {
            @Override
            protected Object parseNonNull(String value) {
                return Long.valueOf(value);
            }
        },
        FLOAT(Types.FLOAT, "float", "Float", "real", "Real") {
            @Override
            protected Object parseNonNull(String value) {
                return Float.valueOf(value);
            }
        },
        DOUBLE(Types.DOUBLE, "Double", "double") {
            @Override
            protected Object parseNonNull(String value) {
                return Double.valueOf(value);
            }
        },
        DATE(Types.DATE, "date", "Date") {
            @Override
            protected Object parseNonNull(String value) {
                return Date.valueOf(value);
            }
        },
        TIME(Types.TIME, "time", "Time") {
            @Override
            protected Object parseNonNull(String value) {
                return Time.valueOf(value);
            }
        },
        TIMESTAMP(Types.TIMESTAMP, "timestamp", "Timestamp") {
            @Override
            protected Object parseNonNull(String value) {
                return Timestamp.valueOf(value);
            }
        },
        STRING(Types.VARCHAR, "string", "String") {
            @Override
            protected Object parseNonNull(String value) {
                return value;
            }
        };

        private final int sqlType;
        private final List<String> aliases;

        protected abstract Object parseNonNull(String value);

        public Object parse(String value) {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return parseNonNull(value);
        }

        Type(int sqlType, String... aliases) {
            this.sqlType = sqlType;
            this.aliases = Arrays.asList(aliases);
        }

        public int getSqlType() {
            return sqlType;
        }

        public List<String> getAliases() {
            return aliases;
        }
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
