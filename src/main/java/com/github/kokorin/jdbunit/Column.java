package com.github.kokorin.jdbunit;

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
        BOOLEAN(Types.BIT, "bit", "Bit", "boolean", "Boolean"),
        INTEGER(Types.INTEGER, "int", "Int", "integer", "Integer"),
        LONG(Types.BIGINT, "long", "Long"),
        FLOAT(Types.FLOAT, "float", "Float", "real", "Real"),
        DOUBLE(Types.DOUBLE, "Double", "double"),
        DATE(Types.DATE, "date", "Date"),
        TIME(Types.TIME, "time", "Time"),
        TIMESTAMP(Types.TIMESTAMP, "timestamp", "Timestamp"),
        STRING(Types.VARCHAR, "string", "String");

        private final int sqlType;
        private final List<String> aliases;

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

        public static Type fromSqlType(int sqlType) {
            for (Type type : values()) {
                if (type.getSqlType() == sqlType) {
                    return type;
                }
            }

            throw new IllegalArgumentException("Type not found for " + sqlType);
        }
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
