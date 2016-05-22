package arhangel.dim.core.messages;

/**
 * отправить сообщение в заданный чат, чат должен быть в списке чатов пользователя
 * (только для залогиненных пользователей)
 */

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageOperations;
import arhangel.dim.core.store.UserOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

                MessageOperations messageOperations = new MessageOperations(session.getConnection());
                if (!messageOperations.getChatsByUserId(textMessage.getSenderId()).contains(textMessage.getChatId())){
                    ErrorMessage errorMessage = new ErrorMessage();
                    errorMessage.setSenderId(textMessage.getSenderId());
                    errorMessage.setText("Sorry, you are not included in this chat");
                    session.send(errorMessage);
                    return;
                }
                messageOperations.addMessage(textMessage.getChatId(), textMessage);
                Chat chat = messageOperations.getChatById(textMessage.getChatId());
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setType(Type.MSG_STATUS);
                statusMessage.setSenderId(session.getUser().getId());
                statusMessage.setStatus(Status.ACCEPTED);
                for (Session currentSession : session.getServer().getCurrentSessions()) {
                    if ((chat.getParticipants().contains(currentSession.getUser().getId()) ||
                            currentSession.getUser().getId() == chat.getAdmin()) &&
                            currentSession.getUser().getId() != textMessage.getSenderId()) {
                        new Thread(() -> {

                            try {
                                currentSession.send(textMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error("Failed to send message to {}",currentSession.getUser().getName());
                            }

                        }).start();
                    }
                }
                session.send(statusMessage);


            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setType(Type.MSG_ERROR);
                errorMessage.setSenderId(Long.valueOf(0));
                errorMessage.setText("Sorry, this action is available only for registered users");
                session.send(errorMessage);
            }

        } catch (Exception e) {
            throw new CommandException("TextCommand " + e);
        }
    }
}
