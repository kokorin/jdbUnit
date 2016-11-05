# jdbUnit

jdbUnit is a library for database integration testing. It is as simple as [dbUnit](http://dbunit.sourceforge.net/)
 with [unitils-dbunit](http://www.unitils.org/tutorial-database.html), but it:
* supports validation of foreign references in expected data
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
|    321     |    12.85     |   451.1        | 2018-11-30  |
```

It's possible to specify empty table:
```
Tablename
==========
```

##Data sets

Testing is done with special jUnit4 Rule (`JdbUnitRule`) and two annotations (`@DataSet` and `@ExpectedDataSet`).
Annotations work the same as in [unitils-dbunit](http://www.unitils.org/tutorial-database.html#Loading_test_data_sets):
`@DataSet` can be put on Class-level or Method-level, and it specifies data to load *before* test. `@ExpectedDataSet` 
 can be put only on Method-level. 
 
 Default annotation values are the next:
 * Class-level `@DataSet` defaults to ClassName.md
 * Method-level `@DataSet` defaults to ClassName.methodName.md
 * `@ExpectedDataSet` defaults to ClassName.methodName.result.md

##Handling references

jdbUnit allows to check that a reference from one new record (SUser_SRole)  was correctly set to another new record (SUser or SRole).
For that you have to specify value Captor in the form of `:name:` and value Reference in the form of `=name=`.

Here is an example of reference usage:

Before.md
```
SUser
===============================================
| id:Integer | login:String | password:String |
|:----------:|:------------:|:---------------:|
|     1      |   admin      |      admin      |

SRole
============================
| id:Integer | name:String |
|:----------:|:-----------:|
|     1      | ROLE_ADMIN  |

SUser_SRole
=====================================
| user_id:Integer | role_id:Integer |
|:---------------:|:---------------:|
|        1        |       1         |
```

Expected.md
```
SUser
===============================================
| id:Integer | login:String | password:String |
|:----------:|:------------:|:---------------:|
|     1      |   admin      |      admin      |
|    :X:     |    test      |       test      |

SRole
============================
| id:Integer | name:String |
|:----------:|:-----------:|
|     1      | ROLE_ADMIN  |
|    :Z:     | ROLE_TEST   |

SUser_SRole
=====================================
| user_id:Integer | role_id:Integer |
|:---------------:|:---------------:|
|        1        |       1         |
|       =X=       |      =Z=        |
```
 
##Test example

Here is an example of UserDao test class:
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