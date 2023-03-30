import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleSQL {

    private Connection SQLConnection;
    private Statement statement;
    private String host;
    private String port;
    private String serviceType;
    private String username;
    private String password;

    public OracleSQL() {
    }

    public OracleSQL(String host, String port, String serviceType) {
        this.host = host;
        this.port = port;
        this.serviceType = serviceType;
    }

    public OracleSQL(String host, String port, String serviceType, String username, String password) {
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

    public OracleSQL createTable(String table, String[] columns, String[] dataTypes){
        StringBuilder s = new StringBuilder("CREATE TABLE ").append(table).append(" (");
        int index = 0;

        for (String col : columns){
            if (index < columns.length - 1) s.append(col).append(" ").append(dataTypes[Utilities.findIndex(columns, col)]).append(", ");
            else s.append(col).append(" ").append(dataTypes[Utilities.findIndex(columns, col)]);
            index++;
        }

        s.append(")");
        System.out.println(s);
        return this;
    }

}
