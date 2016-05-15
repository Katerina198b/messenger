package arhangel.dim.core.messages;

/**
 * создать новый чат, список пользователей приглашенных в чат
 * (только для залогиненных пользователей).
 * /chat_create 3 - создать чат с пользователем id=3,
 * если такой чат уже существует, вернуть существующий
 */
import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageOperations;
import arhangel.dim.core.store.UserOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ChatCreateCommand implements Command {

    private Logger log = LoggerFactory.getLogger(ChatCreateCommand.class);

    @Override
    public void execute(Session session, Message message) throws CommandException {

        log.info("{}", message.toString());

        Optional<User> optionalUser = Optional.ofNullable(session.getUser());

        try {
            if (optionalUser.isPresent()) {

                ChatCreateMessage chatCreateMessage = (ChatCreateMessage) message;
                List<Long> ids = chatCreateMessage.getIds();
                if (chatCreateMessage.getIds().contains(session.getUser().getId())) {
                    ErrorMessage errorMessage = new ErrorMessage();
                    errorMessage.setText("Sorry, you can't add yourself to the chat .");
                    session.send(errorMessage);
                    return;
                }
                UserOperations userOperations = new UserOperations(session.getConnection());
                for (int i = 0; i < ids.size(); i++) {
                    if (userOperations.getUserById(ids.get(i)) == null) {
                        ErrorMessage errorMessage = new ErrorMessage();
                        errorMessage.setText("Sorry, you can't add non-existent user to the chat .");
                        session.send(errorMessage);
                        return;
                    }
                }
                MessageOperations messageOperations = new MessageOperations(session.getConnection());

                if (ids.size() == 1) {
                    Chat chat;
                    List<Long> participants;
                    List<Long> chatsId = messageOperations.getChatsByUserId(session.getUser().getId());
                    for (int i = 0; i < chatsId.size(); i++) {
                        chat = messageOperations.getChatById(chatsId.get(i));
                        participants = chat.getParticipants();
                        if (participants.size() == 1 && (participants.get(0) - ids.get(0) == 0)) {
                            ErrorMessage errorMessage = new ErrorMessage();
                            errorMessage.setSenderId(session.getUser().getId());
                            errorMessage.setText("Chat is exist. Id = " + chatsId.get(i));
                            session.send(errorMessage);
                            return;
                        }
                    }
                }

                long chatId = messageOperations.addChat(session.getUser().getId());
                messageOperations.addUserToChat(session.getUser().getId(), chatId);
                for (int i = 0; i < ids.size(); i++) {
                    messageOperations.addUserToChat(ids.get(i), chatId);
                }
                StatusMessage statusMessage = new StatusMessage();
                statusMessage.setStatus(Status.ACCEPTED);
                session.send(statusMessage);
                return;

            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setText("Sorry, this is available only for registered users.");
                session.send(errorMessage);
                return;
            }
        } catch (Exception e) {
            log.error("ChatCreateCommand: {}", e);
            throw new CommandException("ChatCreateCommand " + e);
        }
    }
}
