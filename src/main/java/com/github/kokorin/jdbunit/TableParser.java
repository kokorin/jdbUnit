package com.github.kokorin.jdbunit;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
            Row row = parseRow(line);
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
        Column.Type type = Column.Type.STRING;
        if (splits.length > 1) {
            type = getColumnType(splits[1].trim());
        }

        return new Column(name, type);
    }

    static Column.Type getColumnType(String value) {
        for (Column.Type type : Column.Type.values()) {
            if (type.name().equals(value)) {
                return type;
            }
            for (String alias : type.getAliases()) {
                if (alias.equals(value)) {
                    return type;
                }
            }
        }

        throw new IllegalArgumentException("Nor column type neither alias: " + value);
    }

    static List<Row> parseContent(List<String> content) {
        List<Row> result = new ArrayList<>();
        if (content != null) {
            for (String line : content) {
                Row row = parseRow(line);
                result.add(row);
            }
        }

        return result;
    }

    static Row parseRow(String line) {
        List<String> values = new ArrayList<>();
        List<String> cells = parseCells(line);
        for (String cell : cells) {
            values.add(cell.trim());
        }

        return new Row(values);
    }

    static boolean isHeaderSeparator(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }

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
