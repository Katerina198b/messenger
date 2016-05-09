/* получить всю информацию о пользователе,
 * без аргументов - о себе (только для залогиненных пользователей)
 */

package arhangel.dim.core.messages;

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class InfoCommand implements Command {
    @Override
    public void execute(Session session, Message message) throws CommandException {
        InfoMessage msg = (InfoMessage) message;
        Optional<User> optionalUser = Optional.of(session.getUser());
        try {
            if (optionalUser.isPresent()) {

                if (msg.getUserId() == -1) {
                    msg.setUserId(optionalUser.get().getId().toString());
                }

                Class.forName("org.postgresql.Driver");

                Connection connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Katerina198b",
                        "trackuser", "trackuser");

                Statement stmt;
                stmt = connection.createStatement();
                String sql;
                sql = "SELECT * FROM User" +
                        "WHERE id=" + msg.getUserId();

                ResultSet rs = stmt.executeQuery(sql);
                Optional<ResultSet> optional = Optional.of(rs);

                if (optional.isPresent()) {

                    InfoResultMessage infoResultMessage = new InfoResultMessage();
                    infoResultMessage.setLogin(optional.get().getString("Login"));
                    infoResultMessage.setUserId(optional.get().getLong("Id"));
                    infoResultMessage.setSenderId(session.getUser().getId());
                    session.send(infoResultMessage);
                } else {
                    ErrorMessage errorMessage = new ErrorMessage();
                    session.send(errorMessage);
                }
                stmt.close();

            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                session.send(errorMessage);
            }
        } catch (Exception e) {
            throw new CommandException("InfoCommand " + e);
        }
    }
}


