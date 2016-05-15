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

        Optional<User> optionalUser = Optional.ofNullable(session.getUser());
        try {
            if (optionalUser.isPresent()) {
                // почему то кидает ошибку
                MessageOperations messageOperations = new MessageOperations(session.getConnection());
                List<Long> chats = messageOperations.getChatsByUserId(session.getUser().getId());
                ChatListResultMessage chatListResultMessage = new ChatListResultMessage();
                chatListResultMessage.setSenderId(session.getUser().getId());
                chatListResultMessage.setChats(chats);
                session.send(chatListResultMessage);
            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setText("Sorry, this action is available only for registered users");
                session.send(errorMessage);
            }

        } catch (Exception e) {
            throw new CommandException("ChatListCommand " + e);
        }
    }
}


