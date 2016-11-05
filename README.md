# jdbUnit

jdbUnit is a library for database integration testing. It is as simple as [dbUnit](http://dbunit.sourceforge.net/)
 with [unitils-dbunit](http://www.unitils.org/tutorial-database.html), but it:
* is implemented with pure JDBC, no special database handler is required
* loads data from [markdown tables](http://fletcher.github.io/MultiMarkdown-5/tables.html)
* allows to add [custom type](https://github.com/kokorin/jdbUnit/blob/master/src/main/java/com/github/kokorin/jdbunit/table/Type.java)
 if [standard ones](https://github.com/kokorin/jdbUnit/blob/master/src/main/java/com/github/kokorin/jdbunit/table/StandardType.java) aren't sufficient
* obeys [KISS principle](https://people.apache.org/~fhanik/kiss.html), so you won't spend hours in debugging your tests

jdbUnit can be plugged-in via maven central repository:
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

To declare a table we use markdown header with markdown table. Here is an example of *.md file: 

```
Tablename
==================================================================================================
| columnName:ColumnType | tInt:Integer | tLong:Long | tFloat:Float | tDouble:Double | tDate:Date |
|:---------------------:|:------------:|:----------:|:------------:|:--------------:|:----------:|
|     columnValue1      |      42      |    123     |   3.14159    |   2.71         | 2008-12-31 |
|     columnValue2      |      24      |    321     |    12.85     |   451.1        | 2018-11-3  |
```

Table can occur more than once in md-file with different columns:

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
public class UserDaoIT {
    private static Connection connection;
    private static EntityManager em;
    private final UserDao userDao = new UserDao();
    @Rule
    public final JdbUnitRule rule = new JdbUnitRule(connection);

    @BeforeClass
    public static void setUpClass() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:test");
        em = Persistence.createEntityManagerFactory("PersistenceUnitName").createEntityManager();
    }
    
    @Before
    public static void setUp() throws Exception {
        userDao.entityManager = em;
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        connection.close();
    }

    @Test
    public void read() throws Exception {
        User actual = userDao.read(1);

        User expected = new User();
        expected.setId(1);
        expected.setUsername("admin");
        expected.setPassword("admin");
        //fill properties of expected user

        ReflectionAssert.assertReflectionEquals(expected, actual);
    }

    @Test
    @ExpectedDataSet
    public void create() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        Role role = new Role();
        role.setId(1);

        EntityTransaction et = em.getTransaction();
        et.begin();
        user = userDao.create(user);
        assertNotEquals(user.getId(), 0);
        et.commit();
    }
}

```