package arhangel.dim.core.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private Logger log = LoggerFactory.getLogger(DatabaseConnection.class);
    private Connection connection;

    private String login;
    private String password;
    private String clas;
    private String url;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setClas(String clas) {
        this.clas = clas;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Connection getConnection() {

        try {
            Class.forName(clas);
            connection = DriverManager.getConnection(url, login, password);
            connection.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed into setConnection");
        }
        return connection;
    }

}

