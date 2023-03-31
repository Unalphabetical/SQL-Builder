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

        String foreignTable = "ForeignTable";
        String[] foreignColumns = {"PartyId", "ClassroomId"};
        String[] foreignDataTypes = {"NUMBER", "NUMBER"};
        oracleSQL.createTable(foreignTable, foreignColumns, foreignDataTypes);

        String table = "TestTable";
        String[] columns = {"FirstName", "LastName", "SSN", "PartyId"};
        String[] dataTypes = {"VARCHAR(20)", "VARCHAR(20)", "NUMBER", "NUMBER"};
        String[] keys = {"NULL", "NULL", "Primary Key", "Foreign Key"};

        oracleSQL.dropTable(table);
        oracleSQL.createTable(table, columns, dataTypes);
        oracleSQL.createTable(table, columns, dataTypes, keys, foreignTable);

        String[] values = {"John", "Doe", "519779675", "TO_DATE('1999/02/04','YYYY/MM/DD')"};
        oracleSQL.insert(table, values);

        oracleSQL.removeUnnecessaryStatements();
        oracleSQL.printStatements();
    }

}
