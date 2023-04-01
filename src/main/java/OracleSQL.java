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

    public OracleSQL() {
        this.tables = new ArrayList<>();
        this.tablesInformation = new HashMap<>();
    }

    public OracleSQL(String host, String port, String serviceType) {
        this.tables = new ArrayList<>();
        this.tablesInformation = new HashMap<>();
        this.host = host;
        this.port = port;
        this.serviceType = serviceType;
    }

    public OracleSQL(String host, String port, String serviceType, String username, String password) {
        this.tables = new ArrayList<>();
        this.tablesInformation = new HashMap<>();
        this.host = host;
        this.port = port;
        this.serviceType = serviceType;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public OracleSQL setHost(String host) {
        this.host = host;
        return this;
    }

    public OracleSQL setPort(String port) {
        this.port = port;
        return this;
    }

    public OracleSQL setServiceType(String serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    public OracleSQL setUsername(String username) {
        this.username = username;
        return this;
    }

    public OracleSQL setPassword(String password) {
        this.password = password;
        return this;
    }

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
        return s.toString();
    }

    public String createTable(String table, String[] columns, String[] dataTypes, String[] keys, String[] references) {
        String oldStatement = createTable(table, columns, dataTypes);
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
        return oldStatement;
    }

    public String dropTable(String table){
        return "DROP TABLE " + table + ";";
    }

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
        return s.toString();
    }

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
        return s.toString();
    }

    public String select(String table, String displayColumn, String column, String value){
        return select(table, new String[] {displayColumn}, column, value);
    }

    public String selectIn(String table, String[] displayColumn, String column, String value){
        return select(table, displayColumn, column, value).replace("=", " IN ");
    }

    public String selectIn(String table, String displayColumn, String column, String value){
        return select(table, displayColumn, column, value).replace("=", " IN ");
    }

    public String selectLike(String table, String[] displayColumn, String column, String value){
        return select(table, displayColumn, column, value).replace("=", " LIKE ");
    }

    public String selectLike(String table, String displayColumn, String column, String value){
        return select(table, displayColumn, column, value).replace("=", " LIKE ");
    }

    public String subquery(String table, String displayColumn, String column, String selectStatement){
        return "SELECT " + displayColumn + " FROM " + table +
                " WHERE " + column + "=" + "(" + selectStatement.replace(";", "") + ");";
    }

}
