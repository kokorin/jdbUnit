# jdbUnit

Simple library for database integration testing. It is as simple as [dbUnit](http://dbunit.sourceforge.net/)
 with [unitils-dbunit](http://www.unitils.org/tutorial-database.html), but it:
* is implemented with pure JDBC, no special database handler is required
* loads data from [markdown tables](http://fletcher.github.io/MultiMarkdown-5/tables.html)
* allows to add [custom type](https://github.com/kokorin/jdbUnit/blob/master/src/main/java/com/github/kokorin/jdbunit/table/Type.java)
 if [standard ones](https://github.com/kokorin/jdbUnit/blob/master/src/main/java/com/github/kokorin/jdbunit/table/StandardType.java) aren't sufficient
* is simple, so you won't spend hours in debugging your tests

jdbUnit can be plugged-in via maven:
```xml
<dependency>
    <groupId>com.github.kokorin.jdbunit</groupId>
    <artifactId>jdbunit</artifactId>
    <version>1.0</version>
    <scope>test</scope>
</dependency>
```

#Usage

##Table declaration

To declare a table we use markdown header with markdown table. Here is an example. 

```
Tablename
==================================================================================================
| columnName:ColumnType | tInt:Integer | tLong:Long | tFloat:Float | tDouble:Double | tDate:Date |
|:---------------------:|:------------:|:----------:|:------------:|:--------------:|:----------:|
|     columnValue1      |      42      |    123     |   3.14159    |   2.71         | 2008-12-31 |
|     columnValue2      |      24      |    321     |    12.85     |   451.1        | 2018-11-3  |
```

Table can occur more than once with different columns:

```
Tablename
========================================
| columnName:ColumnType | tInt:Integer |
|:---------------------:|:------------:|
|     columnValue1      |      42      |
|     columnValue2      |      24      |

Tablename
===========================================================
| tLong:Long | tFloat:Float | tDouble:Double | tDate:Date |
|:----------:|:------------:|:--------------:|:----------:|
|    123     |   3.14159    |   2.71         | 2008-12-31 |
|    321     |    12.85     |   451.1        | 2018-11-3  |
```

It's possible to specify empty table:
```
Tablename
==========
```

##Testing

Testing is done with special jUnit4 Rule (`JdbUnitRule`) and two annotations (`@DataSet` and `@ExpectedDataSet`).
Annotations work the same as in [unitils-dbunit](http://www.unitils.org/tutorial-database.html#Loading_test_data_sets):
`@DataSet` can be put on Class-level or Method-level, and it specifies data to load *before* test. `@ExpectedDataSet` 
 can be put only on Method-level. 
 
 Default annotation values are the next:
 * Class-level `@DataSet` defaults to ClassName.md
 * Method-level `@DataSet` defaults to ClassName.methodName.md
 * `@ExpectedDataSet` defaults to ClassName.methodName.result.md

Here is short example
```java
@DataSet("Before.md")
public class H2IT {
    private static Connection connection;
    @Rule
    public final JdbUnitRule rule = new JdbUnitRule(connection);

    @BeforeClass
    public static void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:test");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void simpleTest() throws Exception {
        //...
    }

    @Test
    @ExpectedDataSet("After.md")
    public void expectationTest() throws Exception {
        //...
    }
}

```