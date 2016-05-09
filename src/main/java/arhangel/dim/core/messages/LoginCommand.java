package arhangel.dim.core.messages;

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;


public class LoginCommand implements Command {

    @Override
    public void execute(Session session, Message message) throws CommandException {
        LoginMessage msg = (LoginMessage) message;
        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Katerina198b",
                    "trackuser", "trackuser");

            Statement stmt;
            stmt = connection.createStatement();
            String sql;
            sql = "SELECT * FROM User" +
                    "WHERE login=" + msg.getLogin() + "," +
                    "password=" + msg.getPassword();

            ResultSet rs = stmt.executeQuery(sql);
            Optional<ResultSet> optional = Optional.of(rs);

            if (optional.isPresent()) {

                User user = new User(msg.getId(), msg.getLogin());
                session.setUser(user);
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setSenderId(session.getUser().getId());
                statusMessage.setStatus(Status.ACCEPTED);
                session.send(statusMessage);
            } else {
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setSenderId(session.getUser().getId());
                statusMessage.setStatus(Status.NOT_ACCEPTED);
                session.send(statusMessage);
            }
            stmt.close();
        } catch (Exception e) {
            throw new CommandException("LoginCommand " + e);
        }
    }
}
