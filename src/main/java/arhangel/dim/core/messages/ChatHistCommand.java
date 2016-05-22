package arhangel.dim.core.messages;

/**
 * список сообщений из указанного чата (только для залогиненных пользователей)
 */

import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * список сообщений из указанного чата (только для залогиненных пользователей)
 */

import java.util.List;
import java.util.Optional;

public class ChatHistCommand implements Command {

    private Logger log = LoggerFactory.getLogger(ChatHistCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {

        log.info("{}", message.toString());

        Optional<User> optionalUser = Optional.of(session.getUser());
        try {
            if (optionalUser.isPresent()) {
                ChatHistMessage chatHistMessage = (ChatHistMessage) message;
                MessageOperations messageOperations = new MessageOperations(session.getConnection());
                if (!messageOperations.getChatsByUserId(session.getUser()
                        .getId()).contains(chatHistMessage.getChatId())) {
                    ErrorMessage errorMessage = new ErrorMessage();
                    errorMessage.setSenderId(session.getUser().getId());
                    errorMessage.setText("Sorry, you are not included in this chat");
                    session.send(errorMessage);
                    return;
                }
                List<Long> messages = messageOperations.getMessagesFromChat(chatHistMessage.getChatId());
                ChatHistResultMessage chatHistResultMessage = new ChatHistResultMessage();
                chatHistResultMessage.setType(Type.MSG_CHAT_HIST_RESULT);
                chatHistResultMessage.setSenderId(session.getUser().getId());

                for (Long messageId : messages) {
                    TextMessage textMessage = (TextMessage) messageOperations.getMessageById(messageId);
                    chatHistResultMessage.addMessage(textMessage);
                }
                session.send(chatHistResultMessage);
            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setType(Type.MSG_ERROR);
                errorMessage.setText("Sorry, this action is available only for registered users");
                session.send(errorMessage);
            }
        } catch (Exception e) {
            throw new CommandException("ChatHistCommand " + e);
        }
    }
}
