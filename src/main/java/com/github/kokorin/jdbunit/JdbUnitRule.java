package com.github.kokorin.jdbunit;


import com.github.kokorin.jdbunit.annotation.DataSet;
import com.github.kokorin.jdbunit.annotation.ExpectedDataSet;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import javax.sql.DataSource;
import java.io.InputStream;

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

        DataSet methodDataSetAnnotation = method.getAnnotation(DataSet.class);
        Class<?> clazz = target.getClass();
        DataSet classDataSetAnnotation = clazz.getAnnotation(DataSet.class);
        if (methodDataSetAnnotation == null && classDataSetAnnotation == null) {
            return base;
        }

        String dataSetLocation;
        if (methodDataSetAnnotation != null) {
            String location = methodDataSetAnnotation.value();
            if ("".equals(location)) {
                location = clazz.getSimpleName() + "." + method.getName() + ".md";
            }
            dataSetLocation = location;
        } else /*if (classDataSet != null)*/ {
            String location = classDataSetAnnotation.value();
            if ("".equals(location)) {
                location = clazz.getSimpleName() + ".md";
            }
            dataSetLocation = location;
        }

        ExpectedDataSet expectedDataSetAnnotation = method.getAnnotation(ExpectedDataSet.class);
        String expectedDataSetLocation;
        if (expectedDataSetAnnotation != null) {
            String location = expectedDataSetAnnotation.value();
            if ("".equals(location)) {
                location = clazz.getSimpleName() + "." + method.getName() + ".result.md";
            }
            expectedDataSetLocation = location;
        } else {
            expectedDataSetLocation = null;
        }

        InputStream dataSet = target.getClass().getResourceAsStream(dataSetLocation);
        InputStream expectedDataSet = null;
        if (expectedDataSetLocation != null) {
            expectedDataSet = target.getClass().getResourceAsStream(expectedDataSetLocation);
        }

        return new JdbUnitStatement(base, dataSet, expectedDataSet, dataSource);
    }
}
