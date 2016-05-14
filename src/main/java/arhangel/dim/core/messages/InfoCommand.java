package arhangel.dim.core.messages;

/**
 * получить всю информацию о пользователе,
 * без аргументов - о себе (только для залогиненных пользователей)
 */

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.UserOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class InfoCommand implements Command {

    private Logger log = LoggerFactory.getLogger(InfoCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        InfoMessage infoMessage = (InfoMessage) message;
        Optional<User> optionalUser = Optional.ofNullable(session.getUser());
        try {
            if (optionalUser.isPresent()) {
                UserOperations userOperations = new UserOperations(session.getConnection());
                User user = null;
                if (infoMessage.getUserId() == -1) {
                    user = userOperations.getUserById(session.getUser().getId());
                } else {
                    user = userOperations.getUserById(infoMessage.getUserId());
                }

                if (Optional.ofNullable(user).isPresent()) {

                    InfoResultMessage infoResultMessage = new InfoResultMessage();
                    infoResultMessage.setType(Type.MSG_INFO_RESULT);
                    infoResultMessage.setSenderId(session.getUser().getId());
                    infoResultMessage.setUserId(user.getId());
                    infoResultMessage.setLogin(user.getName());
                    session.send(infoResultMessage);
                } else {
                    ErrorMessage errorMessage = new ErrorMessage();
                    errorMessage.setType(Type.MSG_ERROR);
                    errorMessage.setText("Sorry, but this user is not exist.");
                    session.send(errorMessage);
                }
            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setType(Type.MSG_ERROR);
                errorMessage.setText("Sorry, this action is available only for registered users");
                session.send(errorMessage);
            }

        } catch (Exception e) {
            log.error("{}", e);
            throw new CommandException("InfoCommand " + e);
        }
    }
}


