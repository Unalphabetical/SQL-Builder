public class Main {

    public static void main(String[] args) {
        OracleSQL oracleSQL = new OracleSQL("192.168.1.1", "9999", "ORCL")
                .setUsername("admin")
                .setPassword("password");
//                .estalishConnection();

        System.out.println("Host: " + oracleSQL.getHost());
        System.out.println("Port: " + oracleSQL.getPort());
        System.out.println("Service: " + oracleSQL.getServiceType());
        System.out.println("Username: " + oracleSQL.getUsername());
        System.out.println("Password: " + oracleSQL.getPassword());
        System.out.println();

        String classTable = "ClassTable";
        String[] classColumns = {"ClassroomId", "ClassroomDescription"};
        String[] classDataTypes = {"NUMBER", "VARCHAR(70)"};
        String[] classValues = {"1", "Computer Science 5 - Basic class"};
        String[] classKeys = {"PRIMARY KEY", "UNIQUE"};

        oracleSQL.dropTable(classTable);
        oracleSQL.createTable(classTable, classColumns, classDataTypes, classKeys, null);
        oracleSQL.insert(classTable, classValues);

        String table = "TestTable";
        String[] columns = {"FirstName", "LastName", "SSN"};
        String[] dataTypes = {"VARCHAR(20)", "VARCHAR(20)", "NUMBER"};
        String[] values = {"John", "Doe", "519779675"};
        String[] keys = {"NULL", "NULL", "PRIMARY KEY"};

        oracleSQL.dropTable(table);
        oracleSQL.createTable(table, columns, dataTypes, keys, null);
        oracleSQL.insert(table, values);

        String foreignTable = "ForeignTable";
        String[] foreignColumns = {"SSN", "ClassroomId"};
        String[] foreignDataTypes = {"NUMBER", "NUMBER"};
        String[] foreignKeys = {"Primary Key", "Foreign Key"};
        String[] foreignValues = {"519779675", "1"};
        String[] foreignReferences = {table, classTable};

        oracleSQL.dropTable(foreignTable);
        oracleSQL.createTable(foreignTable, foreignColumns, foreignDataTypes, foreignKeys, foreignReferences);
        oracleSQL.insert(foreignTable, foreignValues);

        oracleSQL.selectIn(foreignTable, "*", "SSN", "519779675");

        String selectStatement = oracleSQL.select(foreignTable, "ClassroomId", "SSN", "519779675");
        oracleSQL.selectSubquery(classTable, "*", "ClassroomId", selectStatement);
        oracleSQL.deleteSubquery(classTable, "ClassroomId", selectStatement);

        oracleSQL.delete(foreignTable, new String[]{"SSN", "ClassroomId"}, new String[] {"519779675", "1"});

        values = new String[]{"Johnny", "Doey", "519779672"};
        oracleSQL.update(table, columns, values, "SSN", "519779675");

        values = new String[]{"John", "Dee", "519779672"};
        selectStatement = oracleSQL.select(foreignTable, "SSN", "ClassroomId", "1");
        oracleSQL.updateSubquery(table, columns, values, "SSN", selectStatement);

        oracleSQL.printStatements();
    }

}
