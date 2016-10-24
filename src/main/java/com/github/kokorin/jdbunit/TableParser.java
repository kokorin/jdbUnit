package com.github.kokorin.jdbunit;

import com.github.kokorin.jdbunit.table.*;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

public class TableParser {
    public static List<Table> parseTables(InputStream inputStream) {
        Reader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        Iterator<String> lines = lineIterator(reader);

        List<Table> result = new ArrayList<>();
        while (true) {
            Table table = parseTable(lines);
            if (table == null) {
                break;
            }

            result.add(table);
        }

        return result;
    }

    static Table parseTable(Iterator<String> lines) {
        String name = null;
        List<Column> columns = null;
        List<Row> rows;

        //Rewinding empty lines until we will find table's name
        while (lines.hasNext()) {
            String line = lines.next().trim();
            if (line.isEmpty()) {
                continue;
            }
            name = line;
            break;
        }

        if (name != null && !lines.hasNext()) {
            throw new IllegalArgumentException();
        }

        if (name == null) {
            return null;
        }

        String headerSeparator = lines.next();
        if (!isHeaderSeparator(headerSeparator)) {
            throw new IllegalArgumentException();
        }

        String header = lines.next();
        columns = parseHeader(header);

        String contentSeparator = lines.next();
        if (!isContentSeparator(contentSeparator, columns.size())) {
            throw new IllegalArgumentException();
        }

        rows = new ArrayList<>();
        while (lines.hasNext()) {
            String line = lines.next();
            if (line.trim().isEmpty()) {
                break;
            }
            Row row = parseRow(line, columns);
            rows.add(row);
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

    static List<Column> parseHeader(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Header must be non empty");
        }

        List<Column> result = new ArrayList<>();
        List<String> cells = parseCells(value);
        for (String cell : cells) {
            Column column = parseColumn(cell);
            result.add(column);
        }

        return result;
    }

    static Column parseColumn(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Column declaration must be non empty");
        }

        String[] splits = value.split(":");
        if (splits.length > 2) {
            throw new IllegalArgumentException("Either columnName or columnName:Type expected");
        }

        String name = splits[0].trim();
        Type type = StandardType.STRING;
        if (splits.length > 1) {
            type = TypeRegistry.getType(splits[1].trim());
        }

        return new Column(name, type);
    }

    static Row parseRow(String line, List<Column> columns) {
        List<Object> values = new ArrayList<>();

        List<String> cells = parseCells(line);
        if (cells.size() != columns.size()) {
            throw new IllegalArgumentException("Expected " + columns.size() + " cells in a row, but got " + cells.size());
        }

        for (int i = 0; i < columns.size(); ++i) {
            String cell = cells.get(i);
            Type type = columns.get(i).getType();

            Object value = parseValue(cell, type);
            values.add(value);
        }

        return new Row(values);
    }

    static Object parseValue(String text, Type type) {
        Objects.requireNonNull(text);
        text = text.trim();

        if (text.isEmpty()) {
            return null;
        }

        if (type == StandardType.INTEGER) {
            return Integer.valueOf(text);
        }
        if (type == StandardType.LONG) {
            return Long.valueOf(text);
        }
        if (type == StandardType.BOOLEAN) {
            return Boolean.valueOf(text);
        }
        if (type == StandardType.FLOAT) {
            return Float.valueOf(text);
        }
        if (type == StandardType.DOUBLE) {
            return Double.valueOf(text);
        }
        if (type == StandardType.DATE) {
            return Date.valueOf(text);
        }
        if (type == StandardType.TIME) {
            return Time.valueOf(text);
        }
        if (type == StandardType.TIMESTAMP) {
            return Timestamp.valueOf(text);
        }

        return text;
    }

    static boolean isHeaderSeparator(String line) {
        Objects.requireNonNull(line);
        return line.matches("^\\s*=+\\s*$");
    }

    static boolean isContentSeparator(String line, int columns) {
        List<String> cells = parseCells(line);
        if (cells.size() != columns) {
            return false;
        }

        for (String cell : cells) {
            boolean match = cell.trim().matches(":?---+:?");
            if (!match) {
                return false;
            }
        }

        return true;
    }

    //TODO need better implementation
    static Iterator<String> lineIterator(Reader reader) {
        BufferedReader buffered = new BufferedReader(reader);

        List<String> lines = new ArrayList<>();
        try {
            String line = buffered.readLine();
            while (line != null) {
                lines.add(line);
                line = buffered.readLine();
            }
        } catch (IOException e){}

        return lines.iterator();
    }
}
