package com.github.kokorin.jdbunit;

import java.util.ArrayList;
import java.util.List;

public class TablePrinter {
    static String print(Table table) {
        StringBuilder result = new StringBuilder();
        List<Integer> widths = calcColumnWidths(table);

        printName(result, table.getName());
        printHeaderSeparator(result, widths);

        printHeader(result, table.getColumns(), widths);
        printContentSeparator(result, widths);

        for (Row row : table.getRows()) {
            printRow(result, row, widths);
        }

        return result.toString();
    }

    static List<Integer> calcColumnWidths(Table table) {
        return new ArrayList<>();
    }

    static void printName(StringBuilder builder, String name) {

    }

    static void printHeaderSeparator(StringBuilder builder, List<Integer> widths) {

    }

    static void printHeader(StringBuilder builder, List<Column> columns, List<Integer> widths) {

    }

    static void printContentSeparator(StringBuilder builder, List<Integer> widths) {

    }

    static void printRow(StringBuilder builder, Row row, List<Integer> widths) {

    }
}
