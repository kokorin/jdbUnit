package com.github.kokorin.jdbunit.table;


import java.util.HashMap;
import java.util.Map;

public class TypeRegistry {
    private static final Map<String, Type> types = new HashMap<>();

    static {
        for (Type type : StandardType.values()) {
            register(type);
        }
    }

    private TypeRegistry() {
    }

    public static void register(Type type) {
        for (String alias : type.getAliases()) {
            types.put(alias, type);
        }
    }

    public static Type getType(String name) {
        return types.get(name);
    }
}
