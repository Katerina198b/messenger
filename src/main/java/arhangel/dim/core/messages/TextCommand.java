package arhangel.dim.core.messages;

/**
 * отправить сообщение в заданный чат, чат должен быть в списке чатов пользователя
 * (только для залогиненных пользователей)
 */

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageOperations;
import arhangel.dim.core.store.UserOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class TextCommand implements Command {

    private Logger log = LoggerFactory.getLogger(TextCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {
        TextMessage textMessage = (TextMessage) message;
        Optional<User> optionalUser = Optional.ofNullable(session.getUser());
        try {
            if (optionalUser.isPresent()) {

                MessageOperations messageOperations = new MessageOperations();
                messageOperations.addMessage(textMessage.getChatId(), textMessage);
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setType(Type.MSG_STATUS);
                statusMessage.setSenderId(session.getUser().getId());
                statusMessage.setStatus(Status.ACCEPTED);
                session.send(statusMessage);

            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setType(Type.MSG_ERROR);
                session.send(errorMessage);
            }

        } catch (Exception e) {
            throw new CommandException("TextCommand " + e);
        }
    }
}
