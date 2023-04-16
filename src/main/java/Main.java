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

        Table studentTable = new Table("StudentTable")
                .column("SSN").dataType("VARCHAR(9)")
                .column("fName").dataType("VARCHAR(127)")
                .column("lName").dataType("VARCHAR(127)")
                .key("PRIMARY KEY");

        Table majorTable = new Table("MajorTable")
                .column("SSN").dataType("VARCHAR(9)")
                .column("Description").dataType("VARCHAR(127)")
                .tableReference(studentTable)
                .key("FOREIGN KEY");

        oracleSQL.createTable(majorTable);
        oracleSQL.createTable(studentTable);

        oracleSQL.insert(studentTable, new String[] {"Test"});
        oracleSQL.select(studentTable, new String[] {"SSN"}, "SSN", "123456789");

        oracleSQL.delete(studentTable, new String[] {"SSN", "fName"}, new String[] {"123456789", "Anna"});

        oracleSQL.update(studentTable, new String[] {"SSN", "fName"}, new String[] {"987654321", "Anna"}, "SSN", "123456789");

        oracleSQL.dropTable(studentTable);
        oracleSQL.dropTable(majorTable);

        oracleSQL.printStatements();
//        oracleSQL.execute();
    }

}
