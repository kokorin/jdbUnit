package com.github.kokorin.jdbunit;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableParser {
    public List<Table> parseTables(InputStream inputStream) {
        Reader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader lineReader = new BufferedReader(reader);

        List<Table> result = new ArrayList<>();
        try {

            String prevLine = null;

            String tableName = null;
            String header = null;
            List<String> rows = null;

            while (true) {
                String line = lineReader.readLine();
                boolean hasMoreLines = line != null;
                boolean tableEnded = !hasMoreLines;

                String trimmed = trim(line);

                if (containsOnly(trimmed, "=")) {
                    tableName = prevLine;
                    header = null;
                    rows = null;
                } else if (trimmed.contains("|")) {
                    if (rows != null) {
                        rows.add(trimmed);
                    }
                } else if (containsOnly(trimmed, "-")) {
                    if (tableName != null) {
                        header = prevLine;
                        rows = new ArrayList<>();
                    }
                } else {
                    tableEnded = true;
                }


                if ( tableEnded && tableName != null && header != null && rows != null ) {
                    Table table = createTable(tableName, header, rows);
                    result.add(table);
                    tableName = null;
                    header = null;
                    rows = null;
                }

                if (!hasMoreLines) {
                    break;
                }
                prevLine = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static Table createTable(String name, String header, List<String> lines) {
        List<Column> columns = new ArrayList<>();
        List<String> columnDecl = parseCells(header);
        for (String decl : columnDecl) {
            columns.add(parseColumn(decl));
        }

        List<Row> rows = new ArrayList<>();
        for (String line : lines) {
            rows.add(parseRow(line));
        }

        return new Table(name, columns, rows);
    }

    static List<String> parseCells(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Cells line must be non empty");
        }

        int begin = 0;
        int end = value.length();

        if (value.charAt(begin) == '|') {
            begin++;
        }
        if (value.charAt(end - 1) == '|') {
            end--;
        }

        String[] array = value.substring(begin, end).split("\\|");
        return Arrays.asList(array);
    }

    static Column parseColumn(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Column declaration must be non empty");
        }

        String[] splits = value.split(":");
        if (splits.length > 2) {
            throw new IllegalArgumentException("Either columnName or columnName:Type expected");
        }

        String name;
        String type;
        if (splits.length == 2) {
            name = splits[0];
            type = splits[1];
        } else {
            name = splits[0];
            type = "String";
        }

        return new Column(name, type);
    }

    private static Row parseRow(String value) {
        List<String> values = parseCells(value);
        return new Row(values);
    }

    private static String trim(String value) {
        return value;
    }

    private static boolean containsOnly(String value, String c) {
        return value.startsWith(c) && value.endsWith(c);
    }
}
