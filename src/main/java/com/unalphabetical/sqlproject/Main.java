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

        Table courseTable = new Table("CourseTable")
                .column("CourseID").dataType("VARCHAR(9)")
                .column("CourseName").dataType("VARCHAR(127)")
                .column("Description").dataType("VARCHAR(127)")
                .key("PRIMARY KEY");

        Table gradeTable = new Table("GradeTable")
                .column("SSN").dataType("VARCHAR(9)")
                .column("CourseID").dataType("VARCHAR(9)")
                .column("Grade").dataType("VARCHAR(3)")
                .key("FOREIGN KEY").tableReference(studentTable).columnReference("SSN")
                .key("FOREIGN KEY").tableReference(courseTable).columnReference("CourseID");

        mySQL.createTable(studentTable);
        mySQL.createTable(majorTable);
        mySQL.createTable(courseTable);
        mySQL.createTable(gradeTable);

        mySQL.insert(studentTable, new String[] {"123456789", "Anna", "Tran"});
        mySQL.insert(studentTable, new String[] {"999999999", "Emily", "Tran"});

        mySQL.insert(courseTable, new String[] {"319315", "CSC 15", "Programming Concepts and Methodology I"});
        mySQL.insert(courseTable, new String[] {"319320", "CSC 20", "Programming Concepts and Methodology II"});
        mySQL.insert(courseTable, new String[] {"319328", "CSC 28", "Discrete Structures for Computer Science"});
        mySQL.insert(courseTable, new String[] {"319335", "CSC 35", "Introduction to Computer Architecture"});

        mySQL.insert(gradeTable, new String[] {"123456789", "319315", "A"});
        mySQL.insert(gradeTable, new String[] {"123456789", "319320", "A"});
        mySQL.insert(gradeTable, new String[] {"123456789", "319328", "A"});
        mySQL.insert(gradeTable, new String[] {"123456789", "319335", "A"});

        mySQL.select(gradeTable);

        mySQL.update(studentTable, new String[] {"SSN", "fName"}, new String[] {"987654321", "Kestine"}, "SSN", "999999999");
        mySQL.select(studentTable);

        mySQL.dropTable(studentTable);
        mySQL.dropTable(majorTable);
        mySQL.dropTable(courseTable);
        mySQL.dropTable(gradeTable);

        mySQL.printStatements();
//        mySQL.execute();
    }

}
