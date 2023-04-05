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

    List<String> tables;
    Map<String, String[]> tablesInformation;

    List<String> executableStatements;

    /**
     * Default constructor that initializes everything
     */

    public OracleSQL() {
        this.tables = new ArrayList<>();
        this.tablesInformation = new HashMap<>();
        this.executableStatements = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Constructor that initializes the host, port, service type, and everything else
     */

    public OracleSQL(String host, String port, String serviceType) {
        this.tables = new ArrayList<>();
        this.tablesInformation = new HashMap<>();
        this.executableStatements = Collections.synchronizedList(new ArrayList<>());
        this.host = host;
        this.port = port;
        this.serviceType = serviceType;
    }

    /**
     * Constructor that initializes the host, port, service type, username, password, and everything else
     */

    public OracleSQL(String host, String port, String serviceType, String username, String password) {
        this.tables = new ArrayList<>();
        this.tablesInformation = new HashMap<>();
        this.executableStatements = Collections.synchronizedList(new ArrayList<>());
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
     * Returns the basic SQL table statement
     *
     * @param table A table
     * @param columns An array of columns names for the table
     * @param dataTypes An array of data types for the table
     *
     * @return A create table statement
     */

    public String createTable(String table, String[] columns, String[] dataTypes) {
        int tableIndex = tables.indexOf(table);
        if (tableIndex == -1) tableIndex = 0;
        if (tableIndex >= tables.size()) tables.add(table);
        else tables.set(tableIndex, table);

        tablesInformation.put("table-" + table + "-columns", columns);
        tablesInformation.put("table-" + table + "-dataTypes", dataTypes);

        StringBuilder s = new StringBuilder("CREATE TABLE ").append(table).append(" (");
        int dateIndex = 0;
        int index = 0;

        for (String col : columns) {
            if (index < columns.length - 1)
                s.append(col).append(" ").append(dataTypes[Utilities.findIndex(columns, col)]).append(", ");
            else s.append(col).append(" ").append(dataTypes[Utilities.findIndex(columns, col)]);

            if (dataTypes[Utilities.findIndex(columns, col)].startsWith("DATE")) {
                tablesInformation.put("table-" + table + "-dateformat-" + dateIndex, new String[]{"DD-MON-YY"});
                dateIndex++;
            }
            index++;
        }

        s.append(");");
        executableStatements.add(s.toString());
        return s.toString();
    }

    /**
     * Returns a more advanced SQL table statement
     *
     * @param table A String
     * @param columns An array of column names for the table
     * @param dataTypes An array of data types for the table i.e. NUMBER
     * @param keys An array of key types for the columns i.e. FOREIGN
     * @param references An array of table references for the columns
     *
     * @return A create table statement with keys and references
     */

    public String createTable(String table, String[] columns, String[] dataTypes, String[] keys, String[] references) {
        String oldStatement = createTable(table, columns, dataTypes);
        executableStatements.remove(oldStatement);

        StringBuilder oldString = new StringBuilder();
        StringBuilder newString = new StringBuilder();
        int index = 0;

        for (String col : columns) {

            tablesInformation.put("foreignTable-" + table + "-references", references);

            oldString.append(col).append(" ").append(dataTypes[Utilities.findIndex(columns, col)]);
            newString.append(col).append(" ").append(dataTypes[Utilities.findIndex(columns, col)]);

            String key = keys[index];
            if (key.equals("NULL") && references != null && references[index] != null) newString.append(" REFERENCES ")
                    .append(references[index]).append(" (")
                    .append(columns[index]).append(")");

            switch (key.toUpperCase(Locale.ROOT)) {
                case "UNIQUE":
                    newString.append(" ").append("CONSTRAINT ")
                            .append(table).append("_")
                            .append(columns[index]).append("_uk UNIQUE");
                    break;
                case "PRIMARY KEY":
                    newString.append(" PRIMARY KEY");
                    if (references != null && references[index] != null) newString.append(" REFERENCES ")
                            .append(references[index]).append("(")
                            .append(columns[index]).append(")");
                    break;
                case "FOREIGN KEY":
                    if (references != null && references[index] != null) newString.append(" ").append("CONSTRAINT ")
                            .append(table).append("_")
                            .append(columns[index]).append("_")
                            .append(references[index]).append("_fk REFERENCES ")
                            .append(references[index])
                            .append(" ON DELETE CASCADE");
                    break;
            }

            if (index < columns.length - 1) {
                oldString.append(", ");
                newString.append(", ");
            }
            index++;
        }

        oldStatement = oldStatement.replace(oldString, newString);
        executableStatements.add(oldStatement);
        return oldStatement;
    }

    /**
     * Returns a basic SQL drop table statement
     *
     * @param table A table
     *
     * @return A drop table statement
     */

    public String dropTable(String table){
        String s = "DROP TABLE " + table + ";";

        executableStatements.add(s);
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

    public String insert(String table, String[] values) {
        String[] dataTypes = tablesInformation.get("table-" + table + "-dataTypes");

        StringBuilder s = new StringBuilder("INSERT INTO ").append(table).append(" VALUES (");

        int index = 0;
        int dateIndex = 0;

        for (String value : values) {
            if (dataTypes[index].startsWith("VARCHAR") && !value.equalsIgnoreCase("NULL")) {
                s.append("'");
                s.append(value.replace("'", "''"));
                s.append("'");
            }
            else {
                s.append(value);
            }

            if (dataTypes[index].startsWith("DATE")) {
                if (value.startsWith("TO_DATE")){
                    String[] splitDate = value.replace("TO_DATE", "")
                            .replace("(", "").replace(")", "")
                            .split(",");
                    tablesInformation.put("table-" + table + "-dateformat-" + dateIndex, new String[] {splitDate[1]});
                }
                dateIndex++;
            }

            if (index < values.length - 1) s.append(", ");

            index++;
        }

        s.append(");");
        executableStatements.add(s.toString());
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

    public String select(String table, String[] displayColumns, String column, String value){
        StringBuilder s = new StringBuilder("SELECT ");

        int index = 0;
        for (String display : displayColumns){
            s.append(display);

            if (index < displayColumns.length - 1){
                s.append(", ");
            }
            index++;
        }

        s.append(" FROM ").append(table)
                .append(" WHERE ").append(column).append("=");
        String[] columns = tablesInformation.get("table-" + table + "-columns");
        String[] dataTypes = tablesInformation.get("table-" + table + "-dataTypes");

        if (dataTypes[Arrays.asList(columns).indexOf(column)].startsWith("VARCHAR")) s.append('\'').append(value).append('\'');
        else s.append(value);

        s.append(";");
        executableStatements.add(s.toString());
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

    public String select(String table, String displayColumn, String column, String value){
        String s = select(table, new String[] {displayColumn}, column, value);
        executableStatements.remove(s);
        executableStatements.add(s);
        return s;
    }

    /**
     * Returns a SQL select table statement that allows you to view many columns with IN instead of equal.
     *
     * @param table A table
     * @param displayColumn An array of columns that you want to see
     * @param column A column that will be compared with the value
     * @param value A value that will be compared with IN
     *
     * @return A select table statement that can view many columns with IN
     */

    public String selectIn(String table, String[] displayColumn, String column, String value){
        String s = select(table, displayColumn, column, value);
        executableStatements.remove(s);

        s = s.replace("=", " IN ");
        executableStatements.add(s);
        return s;
    }

    /**
     * Returns a SQL select table statement that allows you to view a column with IN instead of equal.
     *
     * @param table A table
     * @param displayColumn A column that you want to see
     * @param column A column that will be compared with the value
     * @param value A value that will be compared with IN
     *
     * @return A select table statement that can view a column with IN
     */

    public String selectIn(String table, String displayColumn, String column, String value){
        String s = select(table, displayColumn, column, value);
        executableStatements.remove(s);

        s = s.replace("=", " IN ");
        executableStatements.add(s);
        return s;
    }

    /**
     * Returns a SQL select table statement that allows you to view many columns with LIKE instead of equal or IN.
     *
     * @param table A table
     * @param displayColumn A column that you want to see
     * @param column A column that will be compared with the value
     * @param value A value that will be compared with LIKE
     *
     * @return A select table statement that can view many columns with LIKE
     */

    public String selectLike(String table, String[] displayColumn, String column, String value){
        String s = select(table, displayColumn, column, value);
        executableStatements.remove(s);

        s = s.replace("=", " LIKE ");
        executableStatements.add(s);
        return s;
    }

    /**
     * Returns a SQL select table statement that allows you to view a column with LIKE instead of equal or IN.
     *
     * @param table A table
     * @param displayColumn A column that you want to see
     * @param column A column that will be compared with the value
     * @param value A value that will be compared with LIKE
     *
     * @return A select table statement that can view a column with LIKE
     */

    public String selectLike(String table, String displayColumn, String column, String value){
        String s = select(table, displayColumn, column, value);
        executableStatements.remove(s);

        s = s.replace("=", " LIKE ");
        executableStatements.add(s);
        return s;
    }

    /**
     * Returns a SQL delete table statement that allows you to delete the table if the columns matches the values
     *
     * @param table A table
     * @param columns An array of columns that will be compared with the value
     * @param values An array of values that will be compared with equal
     *
     * @return A delete table statement that will delete the table if the columns matches the values
     */

    public String delete(String table, String[] columns, String[] values){
        StringBuilder s = new StringBuilder("DELETE FROM ").append(table).append(" WHERE ");

        String[] storedColumns = tablesInformation.get("table-" + table + "-columns");
        String[] dataTypes = tablesInformation.get("table-" + table + "-dataTypes");

        int index = 0;
        for (String display : columns){

            if (dataTypes[Arrays.asList(storedColumns).indexOf(display)].startsWith("VARCHAR")) s.append('\'').append(display).append('\'');
            else s.append(display);
            s.append("=").append(values[index]);

            if (index < columns.length - 1){
                s.append(" AND ");
            }
            index++;
        }

        s.append(";");
        executableStatements.add(s.toString());
        return s.toString();
    }

    /**
     * Returns a SQL delete table statement that allows you to delete the table if the column matches the value
     *
     * @param table A table
     * @param column A column that will be compared with the value
     * @param value A value that will be compared with equal
     *
     * @return A delete table statement that will delete the table if the column matches the value
     */

    public String delete(String table, String column, String value){
        return delete(table, new String[] {column}, new String[] {value});
    }

    /**
     * Returns a SQL update table statement that updates the table's columns
     *
     * @param table A table
     * @param columns An array of columns that will be updated
     * @param values An array of values that will replace the old values
     * @param columnCondition A column that will be compared with the value
     * @param valueCondition A value that will be compared with equal
     *
     * @return An update table statement that updates the table's columns
     */

    public String update(String table, String[] columns, String[] values, String columnCondition, String valueCondition){
        StringBuilder s = new StringBuilder("UPDATE ").append(table).append(" SET ");
        String[] dataTypes = tablesInformation.get("table-" + table + "-dataTypes");

        int index = 0;

        for (String value : values) {
            s.append(columns[index]).append("=");
            if (dataTypes[index].startsWith("VARCHAR") && !value.equalsIgnoreCase("NULL")) s.append('\'');
            s.append(value);
            if (dataTypes[index].startsWith("VARCHAR") && !value.equalsIgnoreCase("NULL")) s.append('\'');
            if (index < values.length - 1) s.append(", ");

            index++;
        }

        s.append(" WHERE ").append(columnCondition).append("=").append(valueCondition).append(";");

        executableStatements.add(s.toString());
        return s.toString();
    }

    /**
     * Returns a SQL update table statement that updates a table's column
     *
     * @param table A table
     * @param columns A column that will be updated
     * @param values A value that will replace the old value
     * @param columnCondition A column that will be compared with a value
     * @param valueCondition A value that will be compared with equal
     *
     * @return An update table statement that updates the table's columns
     */

    public String update(String table, String columns, String values, String columnCondition, String valueCondition){
        return update(table, new String[]{columns}, new String[]{values}, columnCondition, valueCondition);
    }

    /**
     * Returns a SQL select subquery statement that allows you to view a column if the column equals the value of the select table statement
     *
     * @param table A table
     * @param column A column that you want to see
     * @param columnCondition A column that will be compared with another select table statement
     * @param selectStatement A select table statement that will be compared with equal
     *
     * @return A select subquery statement that that allows you to view a column if the column equals the value of the select table statement
     */

    public String selectSubquery(String table, String column, String columnCondition, String selectStatement){
        String s = "SELECT " + column + " FROM " + table +
                " WHERE " + columnCondition + "=" + "(" + selectStatement.replace(";", "") + ");";

        executableStatements.add(s);
        return s;
    }

    /**
     * Returns a SQL delete subquery table statement that allows you to delete the table if the column equals the value of the select table statement
     *
     * @param table A table
     * @param column A column that will be compared with the value
     * @param selectStatement A select table statement that will be compared with equal
     *
     * @return A delete table subquery statement that will delete the table if the column equals the value of the select table statement
     */

    public String deleteSubquery(String table, String column, String selectStatement){
        String s = "DELETE FROM " + table + " WHERE " + column + "=" + "(" + selectStatement.replace(";", "") + ");";

        executableStatements.add(s);
        return s;
    }

    /**
     * Returns a SQL update subquery table statement that updates a table's columns if the column equals the value of the select table statement
     *
     * @param table A table
     * @param columns An arrays of columns that will be updated
     * @param values An arrays of values that will replace the old values
     * @param columnCondition A column that will be compared with a value
     * @param selectStatement A select table statement that will be compared with equal
     *
     * @return An update subquery table statement that updates a table's columns if the column equals the value of the select table statement
     */

    public String updateSubquery(String table, String[] columns, String[] values, String columnCondition, String selectStatement){
        StringBuilder s = new StringBuilder("UPDATE ").append(table).append(" SET ");
        String[] dataTypes = tablesInformation.get("table-" + table + "-dataTypes");

        int index = 0;

        for (String value : values) {
            s.append(columns[index]).append("=");
            if (dataTypes[index].startsWith("VARCHAR") && !value.equalsIgnoreCase("NULL")) s.append('\'');
            s.append(value);
            if (dataTypes[index].startsWith("VARCHAR") && !value.equalsIgnoreCase("NULL")) s.append('\'');
            if (index < values.length - 1) s.append(", ");

            index++;
        }

        s.append(" WHERE ").append(columnCondition).append("=").append("(").append(selectStatement.replace(";", "")).append(");");

        executableStatements.add(s.toString());
        return s.toString();
    }

    /**
     * Rearranges the statement so that foreign or parent tables are dropped before child tables
     */
    public void rearrangeStatements() {
        List<String> newlyArrangedExecutableStatements = new ArrayList<>();

        for (String table : tables) {
            String[] references = tablesInformation.get("foreignTable-" + table + "-references");

            for (int i = 0; i < executableStatements.size(); i++) {
                String statements = executableStatements.get(i);
                if (statements.startsWith("DROP TABLE " + table)) {
                    newlyArrangedExecutableStatements.add(statements);
                    executableStatements.remove(statements);
                }
            }

            for (String reference : references) {
                for (int i = 0; i < executableStatements.size(); i++) {
                    String statements = executableStatements.get(i);
                    if (statements.startsWith("DROP TABLE " + reference)) {
                        newlyArrangedExecutableStatements.add(statements);
                        executableStatements.remove(statements);
                    }
                }
            }
        }

        newlyArrangedExecutableStatements.addAll(executableStatements);
        this.executableStatements = newlyArrangedExecutableStatements;
    }

    /**
     * Rearranges and then debugs the statements by printing them out
     */
    public void printStatements(){
        this.rearrangeStatements();

        for (String statements : executableStatements){
            System.out.println(statements);
        }
    }

}
