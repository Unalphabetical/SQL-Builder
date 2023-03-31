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

        String table = "TestTable";
        String[] columns = {"FirstName", "LastName", "SSN", "Birthday"};
        String[] dataTypes = {"VARCHAR(20)", "VARCHAR(20)", "NUMBER", "DATE"};

        oracleSQL.createTable(table, columns, dataTypes);
        oracleSQL.createTable(table, columns, dataTypes, true);

        String[] values = {"John", "Doe", "519779675", "TO_DATE('1999/02/04','YYYY/MM/DD')"};
        oracleSQL.insert(table, values);

        oracleSQL.printStatements();
    }

}
