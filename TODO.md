1. Test that in ExpectedDataSet the same table can occur twice with different columns

2. Test rollback on SQL Exceptions

3. In verification of expected empty table an incorrect error is thrown, if actual table isn't empty:
    IllegalArgumentException("At least one value in a row is required") at com/github/kokorin/jdbunit/table/Row.java:21
