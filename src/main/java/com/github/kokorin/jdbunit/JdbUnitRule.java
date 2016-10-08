package com.github.kokorin.jdbunit;


import com.github.kokorin.jdbunit.annotation.DataSet;
import com.github.kokorin.jdbunit.annotation.ExpectedDataSet;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import javax.sql.DataSource;

public class JdbUnitRule implements MethodRule {
    private final DataSource dataSource;

    public JdbUnitRule(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        if (target == null) {
            return base;
        }

        Test test = method.getAnnotation(Test.class);
        if (test == null) {
            return base;
        }


        DataSet methodDataSet = method.getAnnotation(DataSet.class);
        Class<?> clazz = target.getClass();
        DataSet classDataSet = clazz.getAnnotation(DataSet.class);
        if (methodDataSet == null && classDataSet == null) {
            return base;
        }

        final String initialTablesLocation;
        if (methodDataSet != null) {
            String location = methodDataSet.value();
            if ("".equals(location)) {
                location = clazz.getSimpleName() + "." + method.getName() + ".md";
            }
            initialTablesLocation = location;
        } else /*if (classDataSet != null)*/ {
            String location = classDataSet.value();
            if ("".equals(location)) {
                location = clazz.getSimpleName() + ".md";
            }
            initialTablesLocation = location;
        }

        ExpectedDataSet expectedDataSet = method.getAnnotation(ExpectedDataSet.class);
        final String expectedTablesLocation;
        if (expectedDataSet != null) {
            String location = expectedDataSet.value();
            if ("".equals(location)) {
                location = clazz.getSimpleName() + "." + method.getName() + ".result.md";
            }
            expectedTablesLocation = location;
        } else {
            expectedTablesLocation = null;
        }

        return new JdbUnitStatement(base, null, null, dataSource);
    }
}
