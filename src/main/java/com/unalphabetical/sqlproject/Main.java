package com.unalphabetical.sqlproject;

import com.unalphabetical.sqlproject.sql.MySQL;

public class Main {

    public static void main(String[] args) {
        MySQL mySQL = new MySQL("127.0.0.1", "3306", "local")
                .setUsername("root").setPassword("password").establishConnection();

        System.out.println("Host: " + mySQL.getHost());
        System.out.println("Port: " + mySQL.getPort());
        System.out.println("Database: " + mySQL.getServiceType());
        System.out.println("Username: " + mySQL.getUsername());
        System.out.println("Password: " + mySQL.getPassword());
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
                .key("FOREIGN KEY").tableReference(studentTable).columnReference("SSN");

        mySQL.createTable(studentTable);
        mySQL.createTable(majorTable);

        mySQL.insert(studentTable, new String[] {"123456789", "Anna", "Tran"});
        mySQL.select(studentTable, new String[] {"*"}, "SSN", "123456789");

        mySQL.update(studentTable, new String[] {"SSN", "fName"}, new String[] {"987654321", "Kestine"}, "SSN", "123456789");
        mySQL.select(studentTable, new String[] {"*"}, "SSN", "987654321");

        mySQL.delete(studentTable, new String[] {"SSN", "fName"}, new String[] {"987654321", "Kestine"});

        mySQL.dropTable(studentTable);
        mySQL.dropTable(majorTable);

        mySQL.printStatements();
//        mySQL.execute();
    }

}
