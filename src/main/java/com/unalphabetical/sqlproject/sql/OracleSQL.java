package com.unalphabetical.sqlproject.sql;

import com.unalphabetical.sqlproject.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class OracleSQL extends MySQL {

    private String serviceType;

    public OracleSQL() {
        super();
    }

    public OracleSQL(String host, String port, String serviceType) {
        super(host, port, serviceType);
        this.serviceType = serviceType;
    }

    public OracleSQL(String host, String port, String serviceType, String username, String password) {
        super(host, port, serviceType, username, password);
        this.serviceType = serviceType;
    }

    /**
     * Makes a connection towards the SQL server
     * and returns the current class to allow building
     *
     * @return OracleSQL
     */

    public OracleSQL estalishConnection() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            this.SQLConnection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@" + host + ":" + port + ":" + serviceType, username, password);
            this.statement = SQLConnection.createStatement();
        } catch (SQLException s) {
            throw new RuntimeException(s);
        }
        return this;
    }

    /**
     * Returns a SQL table statement
     *
     * @param table A String
     *
     * @return A create table statement with keys and references
     */

    public String createTable(Table table) {
        if (!tables.contains(table)) tables.add(table);

        StringBuilder s = new StringBuilder("CREATE TABLE ").append(table.getName()).append(" (");

        List<String> columns = table.getColumns();
        List<String> dataTypes = table.getDataTypes();

        int index = 0;

        for (String col : columns) {
            int columnIndex = columns.indexOf(col);
            s.append(col).append(" ").append(dataTypes.get(columnIndex));

            List<String> keys = table.getKeys();
            List<Table> references = table.getTableReferences();
            if ((keys != null) && (!keys.isEmpty())) {
                if (keys.size() > columnIndex && keys.get(columnIndex) != null) {
                    switch (keys.get(columnIndex)) {
                        case "UNIQUE":
                            s.append(" CONSTRAINT")
                                    .append(table.getName()).append("_")
                                    .append(table.getColumns().get(columnIndex)).append("_uk UNIQUE");
                            break;
                        case "PRIMARY KEY":
                            s.append(" PRIMARY KEY");
                            break;
                        case "FOREIGN KEY":
                            if ((references != null) && (!references.isEmpty())) s.append(" ").append("CONSTRAINT ")
                                    .append(table.getName()).append("_")
                                    .append(table.getColumns().get(columnIndex)).append("_")
                                    .append(table.getTableReferences().get(columnIndex).getName()).append("_fk REFERENCES ")
                                    .append(table.getTableReferences().get(columnIndex).getName())
                                    .append(" ON DELETE CASCADE");
                            break;
                    }
                }
            }
            if (index < columns.size() - 1) s.append(", ");
            index++;

        }

        s.append(");");
        statements.add(s.toString());
        return s.toString();
    }

}
