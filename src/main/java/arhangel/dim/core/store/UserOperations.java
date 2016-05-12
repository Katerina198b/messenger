package arhangel.dim.core.store;

import arhangel.dim.core.User;
import com.sun.glass.ui.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.*;
import java.sql.*;
import java.sql.Statement;
import java.util.Optional;

public class UserOperations implements UserStore {

    private Connection connection;
    private Logger log = LoggerFactory.getLogger(UserOperations.class);

    public UserOperations() throws Exception {
        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Katerina198b",
                "trackuser", "trackuser");
        connection.setAutoCommit(false);

    }

    public User addUser(String login, String password) {

        try {
            PreparedStatement stmt = connection
                    .prepareStatement("INSERT INTO users (login, password) VALUES(?,?)");
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.executeUpdate();
            stmt.close();
            connection.commit();
            return this.getUser(login, password);

        } catch (SQLException e) {
            log.error("Failed into addUser: {}", e);
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public User addUser(User user) {

        try {
            PreparedStatement stmt = connection
                    .prepareStatement("INSERT INTO Users(login, password) VALUES(?,?);");
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.executeUpdate();
            connection.commit();
            stmt.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Failed into addUser: {}", e);
        }

        return user;
    }

    @Override
    public User updateUser(User user) {

        try {
            PreparedStatement stmt = connection
                    .prepareStatement("UPDATE Users SET login = ?, password = ? WHERE id = ?;");
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setLong(3, user.getId());
            stmt.executeUpdate();
            connection.commit();
            stmt.close();
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Failed into updateUser: {}", e);
        }
        return null;
    }

    @Override
    public User getUser(String login, String password) {

        try {
            PreparedStatement stmt = connection
                    .prepareStatement("SELECT * FROM Users WHERE login = ? AND password = ?;");
            stmt.setString(1, login);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(login);
                user.setPassword(password);
                return user;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Failed into getUser: {}", e);
        }
        return null;
    }

    @Override
    public User getUserById(Long id) {

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Users WHERE id = ?;");
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(id);
                user.setName(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                rs.close();
                stmt.close();
                return user;
            }
            rs.close();
            stmt.close();
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Failed into getUserById: {}", e);
        }
        return null;
    }

    public boolean userIsPresentByLogin(String login) {

        try {
            PreparedStatement stmt = connection
                    .prepareStatement("SELECT FROM Users * WHERE login = ?;");
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Failed into getUserById: {}", e);
        }
        return false;
    }

}
