package com.unalphabetical.sqlproject.sql;

import com.unalphabetical.sqlproject.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public class MySQL {

    protected Connection SQLConnection;
    protected Statement statement;
    protected String host;
    protected String port;
    protected String database;
    protected String username;
    protected String password;
    protected List<String> statements;
    protected List<Table> tables;
    protected Map<String, Integer> referencedAmount;

    public MySQL() {
        this.tables = new ArrayList<>();
        this.statements = Collections.synchronizedList(new ArrayList<>());
        this.referencedAmount = new HashMap<>();
    }

    public MySQL(String host, String port, String database) {
        this.tables = new ArrayList<>();
        this.statements = Collections.synchronizedList(new ArrayList<>());
        this.referencedAmount = new HashMap<>();
        this.host = host;
        this.port = port;
        this.database = database;
    }

    public MySQL(String host, String port, String database, String username, String password) {
        this.tables = new ArrayList<>();
        this.statements = Collections.synchronizedList(new ArrayList<>());
        this.referencedAmount = new HashMap<>();
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Get the host of the server
     *
     * @return The host
     */

    public String getHost() {
        return host;
    }

    /**
     * Get the port of the server
     *
     * @return The port
     */

    public String getPort() {
        return port;
    }

    /**
     * Get the service type of the server
     *
     * @return The service type
     */

    public String getServiceType() {
        return database;
    }

    /**
     * Get the username of the server
     *
     * @return The username
     */

    public String getUsername() {
        return username;
    }

    /**
     * Get the password of the server
     *
     * @return The password
     */

    public String getPassword() {
        return password;
    }

    /**
     * Get the statements that are going to be executed
     *
     * @return The statements
     */

    public List<String> getStatements() {
        return statements;
    }

    /**
     * Get the tables that are going to be in the server
     *
     * @return The tables
     */

    public List<Table> getTables() {
        return tables;
    }

    /**
     * Sets the host of the server
     *
     * @param host String
     */

    public MySQL setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets the port of the server
     *
     * @param port String
     */

    public MySQL setPort(String port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the database of the server
     *
     * @param database String
     */

    public MySQL setDatabase(String database) {
        this.database = database;
        return this;
    }

    /**
     * Sets the username of the server
     *
     * @param username String
     */

    public MySQL setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Sets the password of the server
     *
     * @param password String
     */

    public MySQL setPassword(String password) {
        this.password = password;
        return this;
    }

    public MySQL establishConnection() {
        try {
            this.SQLConnection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port +"/" + database, username, password);
            this.statement = SQLConnection.createStatement();
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return this;
    }

    /**
     * Returns a table statement
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
        referencedAmount.put(table.getName(), 0);

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
                            if ((references != null) && (!references.isEmpty())) {
                                s.append(", FOREIGN KEY (").append(col).append(")")
                                        .append(" REFERENCES ")
                                        .append(table.getTableReferences().get(columnIndex).getName())
                                        .append(" (").append(table.getColumnReferences().get(columnIndex))
                                        .append(") ON DELETE CASCADE");
                                int amount = referencedAmount.getOrDefault(table.getTableReferences().get(columnIndex).getName(), 0);
                                referencedAmount.put(table.getTableReferences().get(columnIndex).getName(), amount + 1);
                            }
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

    /**
     * Returns a basic drop table statement
     *
     * @param table A table
     *
     * @return A drop table statement
     */

    public String dropTable(Table table){
        String s = "DROP TABLE " + table.getName() + ";";

        statements.add(s);
        return s;
    }

    /**
     * Returns a basic insert table statement
     *
     * @param table A table
     * @param values An array of values that will be inserted
     *
     * @return An insert table statement
     */

    public String insert(Table table, String[] values) {
        List<String> dataTypes = table.getDataTypes();

        StringBuilder s = new StringBuilder("INSERT INTO ").append(table.getName()).append(" VALUES (");

        int index = 0;

        for (String value : values) {
            if (dataTypes.get(index).startsWith("VARCHAR") && !value.equalsIgnoreCase("NULL")) {
                s.append("'");
                s.append(value.replace("'", "''"));
                s.append("'");
            } else {
                s.append(value);
            }

            if (index < values.length - 1) s.append(", ");
            index++;
        }

        s.append(");");
        statements.add(s.toString());
        return s.toString();
    }

    /**
     * Returns a basic select table statement that allows you to view many columns with equal
     *
     * @param table A table
     * @param displayColumns An array of columns that you want to see
     * @param column A column that will be compared with the value
     * @param value A value that will be compared with equal
     *
     * @return A select table statement that can view many columns with equal
     */

    public String select(Table table, String[] displayColumns, String column, String value){
        StringBuilder s = new StringBuilder("SELECT ");

        int index = 0;
        for (String display : displayColumns){
            s.append(display);

            if (index < displayColumns.length - 1){
                s.append(", ");
            }
            index++;
        }

        s.append(" FROM ").append(table.getName())
                .append(" WHERE ").append(column).append("=");

        List<String> columns = table.getColumns();
        List<String> dataTypes = table.getDataTypes();

        if (dataTypes.get(columns.indexOf(column)).startsWith("VARCHAR")) s.append('\'').append(value).append('\'');
        else s.append(value);

        s.append(";");
        statements.add(s.toString());
        return s.toString();
    }

    /**
     * Returns a basic select table statement that allows you to view a column with equal
     *
     * @param table A table
     * @param displayColumn A column that you want to see
     * @param column A column that will be compared with the value
     * @param value A value that will be compared with equal
     *
     * @return A select table statement that can view a column with equal
     */

    public String select(Table table, String displayColumn, String column, String value){
        String s = select(table, new String[] {displayColumn}, column, value);
        statements.remove(s);
        statements.add(s);
        return s;
    }

    /**
     * Returns a basic select table statement that allows you to view all columns without any conditions
     *
     * @param table A table
     *
     * @return A select table statement that can view all columns
     */

    public String select(Table table) {
        StringBuilder s = new StringBuilder("SELECT * FROM ").append(table.getName()).append(";");
        statements.add(s.toString());
        return s.toString();
    }

    /**
     * Returns a SQL delete table statement that allows you to delete the table if the columns matches the values
     *
     * @param table A table
     * @param column An array of columns that will be compared with the value
     * @param value An array of values that will be compared with equal
     *
     * @return A delete table statement that will delete the table if the columns matches the values
     */

    public String delete(Table table, String[] column, String[] value){
        StringBuilder s = new StringBuilder("DELETE FROM ").append(table.getName()).append(" WHERE ");

        List<String> columns = table.getColumns();
        List<String> dataTypes = table.getDataTypes();

        int index = 0;
        for (String display : column){
            s.append(display).append("=");

            if (dataTypes.get(columns.indexOf(display)).startsWith("VARCHAR")) s.append('\'').append(value[index]).append('\'');
            else s.append(value[index]);

            if (index < column.length - 1){
                s.append(" AND ");
            }
            index++;
        }

        s.append(";");
        statements.add(s.toString());
        return s.toString();
    }

    /**
     * Returns a SQL delete table statement that allows you to delete the table if the column matches the value
     *
     * @param table A table
     * @param column A column that will be compared with the value
     * @param values A value that will be compared with equal
     *
     * @return A delete table statement that will delete the table if the column matches the value
     */

    public String delete(Table table, String column, String values){
        return delete(table, new String[] {column}, new String[] {values});
    }

    /**
     * Returns a SQL update table statement that updates the columns of tables
     *
     * @param table A table
     * @param column An array of columns that will be updated
     * @param values An array of values that will replace the old values
     * @param columnCondition A column that will be compared with the value
     * @param valueCondition A value that will be compared with equal
     *
     * @return An update table statement that updates the table's columns
     */

    public String update(Table table, String[] column, String[] values, String columnCondition, String valueCondition){
        StringBuilder s = new StringBuilder("UPDATE ").append(table.getName()).append(" SET ");
        List<String> columns = table.getColumns();
        List<String> dataTypes = table.getDataTypes();

        int index = 0;

        for (String value : values) {
            s.append(column[index]).append("=");

            int dataIndex = columns.indexOf(column[index]);
            if (dataTypes.get(dataIndex).startsWith("VARCHAR") && !value.equalsIgnoreCase("NULL")) s.append('\'');
            s.append(value);
            if (dataTypes.get(dataIndex).startsWith("VARCHAR") && !value.equalsIgnoreCase("NULL")) s.append('\'');
            if (index < values.length - 1) s.append(", ");

            index++;
        }

        int dataIndex = columns.indexOf(columnCondition);
        s.append(" WHERE ").append(columnCondition).append("=");

        if (dataTypes.get(dataIndex).startsWith("VARCHAR") && !valueCondition.equalsIgnoreCase("NULL")) s.append("'");
        s.append(valueCondition);
        if (dataTypes.get(dataIndex).startsWith("VARCHAR") && !valueCondition.equalsIgnoreCase("NULL")) s.append("'");

        s.append(";");
        statements.add(s.toString());
        return s.toString();
    }

    /**
     * Returns a SQL update table statement that updates the column of a table
     *
     * @param table A table
     * @param columns A column that will be updated
     * @param values A value that will replace the old value
     * @param columnCondition A column that will be compared with a value
     * @param valueCondition A value that will be compared with equal
     *
     * @return An update table statement that updates the table's columns
     */

    public String update(Table table, String columns, String values, String columnCondition, String valueCondition){
        return update(table, new String[]{columns}, new String[]{values}, columnCondition, valueCondition);
    }

    /**
     * Sorts the statements so that foreign tables are dropped before reference tables
     *
     * @return A boolean
     */

    public boolean isSorted() {
        boolean sorted = false;

        List<String> keys = new ArrayList<>(referencedAmount.keySet());
        for (int i = 0; i < keys.size() - 1; i++) {
            sorted = referencedAmount.get(keys.get(i)) <= referencedAmount.get(keys.get(i + 1));
        }

        return sorted;
    }

    /**
     * Rearranges the statement so that foreign tables are dropped before reference tables
     * The method sums up the reference amount so the foreign tables are correctly dropped before the reference tables
     * The method also sorts the statements only when they are not sorted to improve performance
     */

    public void rearrangeStatements() {

        List<String> newlyArrangedExecutableStatements = new ArrayList<>();

        for (Table table : tables) {
            for (Table reference : table.getTableReferences()) {
                if (reference != null) {
                    int referenceAmount = referencedAmount.getOrDefault(reference.getName(), 0);
                    int tableAmount = referencedAmount.getOrDefault(table.getName(), 0);
                    referencedAmount.put(reference.getName(), referenceAmount + tableAmount);
                }
            }
        }

        if (!isSorted()) referencedAmount = referencedAmount
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));

        for (Map.Entry<String, Integer> s : referencedAmount.entrySet()) {
            Iterator<String> iterator = statements.iterator();
            while (iterator.hasNext()) {
                String statement = iterator.next();
                if (statement.startsWith("DROP TABLE " + s.getKey())) {
                    newlyArrangedExecutableStatements.add(statement);
                    iterator.remove();
                }
            }
        }

        newlyArrangedExecutableStatements.addAll(statements);
        this.statements = newlyArrangedExecutableStatements;
    }

    /**
     * Debugs the statements by printing them out
     */

    public void printStatements(){
        this.rearrangeStatements();

        for (String statements : statements){
            System.out.println(statements);
        }
    }

    /**
     * This rearranges the statements and executes them
     */

    public void execute() {
        this.rearrangeStatements();

        try {
            for (String s : this.statements) {
                this.statement.execute(s);
            }
            this.statements = new ArrayList<>();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
