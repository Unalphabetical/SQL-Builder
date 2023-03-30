public class OracleSQL {

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

}