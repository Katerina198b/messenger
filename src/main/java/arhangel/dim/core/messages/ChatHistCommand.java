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
                List<Long> messages = messageOperations.getMessagesFromChat(chatHistMessage.getChatId());
                ChatHistResultMessage chatHistResultMessage = new ChatHistResultMessage();
                chatHistResultMessage.setType(Type.MSG_CHAT_HIST_RESULT);
                chatHistResultMessage.setSenderId(session.getUser().getId());

                for (int i = 0; i < messages.size(); i++) {
                    TextMessage textMessage = (TextMessage) messageOperations.getMessageById(messages.get(i));
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
