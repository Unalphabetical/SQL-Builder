import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class OracleSQL {

    private Connection SQLConnection;
    private Statement statement;
    private String host;
    private String port;
    private String serviceType;
    private String username;
    private String password;
    private List<String> statements;
    private List<Table> tables;

    public OracleSQL() {
        this.tables = new ArrayList<>();
        this.statements = Collections.synchronizedList(new ArrayList<>());
    }

    public OracleSQL(String host, String port, String serviceType) {
        this.tables = new ArrayList<>();
        this.statements = Collections.synchronizedList(new ArrayList<>());
        this.host = host;
        this.port = port;
        this.serviceType = serviceType;
    }

    public OracleSQL(String host, String port, String serviceType, String username, String password) {
        this.tables = new ArrayList<>();
        this.statements = Collections.synchronizedList(new ArrayList<>());
        this.host = host;
        this.port = port;
        this.serviceType = serviceType;
        this.username = username;
        this.password = password;
    }

    /**
     * Get the host of the SQL server
     *
     * @return The host
     */

    public String getHost() {
        return host;
    }

    /**
     * Get the port of the SQL server
     *
     * @return The port
     */

    public String getPort() {
        return port;
    }

    /**
     * Get the service type of the SQL server
     *
     * @return The service type
     */

    public String getServiceType() {
        return serviceType;
    }

    /**
     * Get the username of the SQL server
     *
     * @return The username
     */

    public String getUsername() {
        return username;
    }

    /**
     * Get the password of the SQL server
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
     * Get the tables that are going to be in the SQL server
     *
     * @return The tables
     */

    public List<Table> getTables() {
        return tables;
    }

    /**
     * Sets the host of the SQL server
     *
     * @param host String
     */

    public OracleSQL setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets the port of the SQL server
     *
     * @param port String
     */

    public OracleSQL setPort(String port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the service type of the SQL server
     *
     * @param serviceType String
     */

    public OracleSQL setServiceType(String serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    /**
     * Sets the username of the SQL server
     *
     * @param username String
     */

    public OracleSQL setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Sets the password of the SQL server
     *
     * @param password String
     */

    public OracleSQL setPassword(String password) {
        this.password = password;
        return this;
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
            s.append(col).append(" ").append(dataTypes.get(columnIndex)).append(" ");

            List<String> keys = table.getKeys();
            List<Table> references = table.getReferences();
            if ((keys != null) && (keys.size() > 0)) {
                if (keys.size() > columnIndex && keys.get(columnIndex) != null) {
                    switch (keys.get(columnIndex)) {
                        case "UNIQUE":
                            s.append(" ").append("CONSTRAINT ")
                                    .append(table.getName()).append("_")
                                    .append(table.getColumns().get(columnIndex)).append("_uk UNIQUE");
                            break;
                        case "PRIMARY KEY":
                            s.append(" PRIMARY KEY");
                            if ((references != null) && (references.size() > 0)) s.append(" REFERENCES ")
                                    .append(references.get(columnIndex)).append("(")
                                    .append(references.get(columnIndex).getName()).append(")");
                            break;
                        case "FOREIGN KEY":
                            if ((references != null) && (references.size() > 0)) s.append(" ").append("CONSTRAINT ")
                                    .append(table.getName()).append("_")
                                    .append(table.getColumns().get(columnIndex)).append("_")
                                    .append(table.getReferences().get(columnIndex).getName()).append("_fk REFERENCES ")
                                    .append(table.getReferences().get(columnIndex).getName())
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

    /**
     * Returns a basic SQL drop table statement
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
     * Returns a basic SQL insert table statement
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
     * Returns a basic SQL select table statement that allows you to view many columns with equal
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
     * Returns a basic SQL select table statement that allows you to view a column with equal
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
     * Returns a SQL delete subquery table statement that allows you to delete the table if the column is IN the value of the select table statement
     *
     * @param table A table
     * @param column A column that will be compared with the value
     * @param selectStatement A select table statement that will be compared with equal
     *
     * @return A delete table subquery statement that will delete the table if the column equals the value of the select table statement
     */

    public String deleteInSubquery(Table table, String column, String selectStatement){
        statements.remove(selectStatement);
        String s = "DELETE FROM " + table.getName() + " WHERE " + column + " IN " + "(" + selectStatement.replace(";", "") + ";";

        statements.add(s);
        return s;
    }

    /**
     * Rearranges the statement so that foreign tables are dropped before reference tables
     */

    public void rearrangeStatements() {
        List<String> newlyArrangedExecutableStatements = new ArrayList<>();

        for (Table table : tables) {
            List<Table> references = table.getReferences();

            for (int i = 0; i < statements.size(); i++) {
                String statement = statements.get(i);
                if (statement.startsWith("DROP TABLE " + table.getName())) {
                    newlyArrangedExecutableStatements.add(statement);
                    statements.remove(statement);
                }
            }

            if (references != null) {
                for (Table reference : references) {
                    for (int i = 0; i < newlyArrangedExecutableStatements.size(); i++) {
                        String statement = newlyArrangedExecutableStatements.get(i);
                        if (statement.startsWith("DROP TABLE " + reference.getName())) {
                            newlyArrangedExecutableStatements.remove(statement);
                            newlyArrangedExecutableStatements.add(statement);
                        }
                    }
                }

            }
        }

        newlyArrangedExecutableStatements.addAll(statements);
        this.statements = newlyArrangedExecutableStatements;
    }

    /**
     * Rearranges and then debugs the statements by printing them out
     */
    public void printStatements(){
        this.rearrangeStatements();

        for (String statements : statements){
            System.out.println(statements);
        }
    }

    /**
     * This executes the list of statements
     */

    public void execute(){
        this.rearrangeStatements();
        try {
            for (String s : this.statements) {
                this.statement.execute(s.replace(";", ""));
            }
            this.statements = new ArrayList<>();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}