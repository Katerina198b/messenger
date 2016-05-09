package arhangel.dim.core.messages;

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;

import java.sql.Connection;

/**
 * отправить сообщение в заданный чат, чат должен быть в списке чатов пользователя
 * (только для залогиненных пользователей)
 */

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class TextCommand implements Command {
    @Override
    public void execute(Session session, Message message) throws CommandException {
        TextMessage msg = (TextMessage) message;
        Optional<User> optionalUser = Optional.of(session.getUser());
        try {
            if (optionalUser.isPresent()) {

                Class.forName("org.postgresql.Driver");

                Connection connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Katerina198b",
                        "trackuser", "trackuser");

                Statement stmt;
                stmt = connection.createStatement();
                String sql;
                sql = "INSERT INTO CHAT (ID,MESSAGES) +" +
                        "VALUES (" + msg.getChatId() + "," + msg.getText() + ");";

                stmt.executeUpdate(sql);

                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setStatus(Status.ACCEPTED);
                statusMessage.setSenderId(session.getUser().getId());
                session.send(statusMessage);
                stmt.close();
                stmt.close();
                connection.commit();

            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                session.send(errorMessage);
            }

        } catch (Exception e) {
            throw new CommandException("TextCommand " + e);
        }
    }
}
