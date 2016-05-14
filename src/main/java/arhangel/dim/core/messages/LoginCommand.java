package arhangel.dim.core.messages;

/**
 * залогиниться (если логин не указан, то авторизоваться).
 * В случае успеха приходит вся инфа о пользователе
 */

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.UserOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;


public class LoginCommand implements Command {

    private Logger log = LoggerFactory.getLogger(LoginCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        Optional<User> optionalUser = Optional.ofNullable(session.getUser());
        try {
            if (!optionalUser.isPresent()) {
                LoginMessage loginMessage = (LoginMessage) message;
                UserOperations userOperations = new UserOperations(session.getConnection());
                User user = userOperations.getUser(loginMessage.getLogin(), loginMessage.getPassword());
                if (Optional.ofNullable(user).isPresent()) {
                    log.info("authorization..");
                    session.setUser(user);
                    InfoResultMessage infoMessage = new InfoResultMessage();
                    infoMessage.setType(Type.MSG_INFO_RESULT);
                    infoMessage.setSenderId(session.getUser().getId());
                    infoMessage.setUserId(session.getUser().getId());
                    infoMessage.setLogin(user.getName());
                    infoMessage.setNewSession(true);
                    session.send(infoMessage);

                } else {
                    if (userOperations.userIsPresentByLogin(loginMessage.getLogin())) {
                        ErrorMessage errorMessage = new ErrorMessage();
                        errorMessage.setType(Type.MSG_ERROR);
                        errorMessage.setSenderId(session.getUser().getId());
                        errorMessage.setText("Sorry, but password is incorrect or login is already used");
                        session.send(errorMessage);
                    } else {
                        log.info("Creating new user..");
                        User newUser = userOperations.addUser(loginMessage.getLogin(), loginMessage.getPassword());
                        InfoResultMessage infoMessage = new InfoResultMessage();
                        session.setUser(newUser);
                        infoMessage.setType(Type.MSG_INFO_RESULT);
                        infoMessage.setSenderId(session.getUser().getId());
                        infoMessage.setLogin(newUser.getName());
                        infoMessage.setUserId(newUser.getId());
                        infoMessage.setNewSession(true);
                        session.send(infoMessage);
                    }
                }
            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setType(Type.MSG_ERROR);
                errorMessage.setSenderId(session.getUser().getId());
                errorMessage.setText("Sorry, but you are login now. For logout enter \"q\" ");
                session.send(errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
