package arhangel.dim.core.messages;

/**
 * получить список чатов пользователя
 * (только для залогиненных пользователей).
 * От сервера приходит список id чатов
 */

import arhangel.dim.core.Chat;
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
import java.util.List;
import java.util.Optional;


public class ChatListCommand implements Command {

    private Logger log = LoggerFactory.getLogger(ChatListCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {

        ChatListMessage chatListMessage = (ChatListMessage) message;
        Optional<User> optionalUser = Optional.ofNullable(session.getUser());
        try {
            if (optionalUser.isPresent()) {
                // почему то кидает ошибку
                MessageOperations messageOperations = new MessageOperations();
                List<Long> chats = messageOperations.getChatsByUserId(session.getUser().getId());
                ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
                chatListResultMessage.setType(Type.MSG_CHAT_LIST_RESULT);
                chatListResultMessage.setSenderId(session.getUser().getId());
                for (int i = 0; i < chats.size(); i++) {
                    chatListResultMessage.addChat(chats.get(i));
                }
                session.send(chatListResultMessage);
            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setType(Type.MSG_ERROR);
                session.send(errorMessage);
            }

        } catch (Exception e) {
            throw new CommandException("ChatListCommand " + e);
        }
    }
}


