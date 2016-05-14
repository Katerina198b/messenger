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
                MessageOperations messageOperations = new MessageOperations(session.getConnection());
                List<Long> ids = chatCreateMessage.getIds();
                if (ids.size() == 1) {
                    Chat chat;
                    List<Long> participants;
                    List<Long> chatsId = messageOperations.getChatsByUserId(session.getUser().getId());
                    for (int i = 0; i < chatsId.size(); i++) {
                        chat = messageOperations.getChatById(chatsId.get(i));
                        participants = chat.getParticipants();
                        if (participants.size() == 1 && participants.get(0) == ids.get(0)) {
                            ChatGetResultMessage chatGetResultMessage = new ChatGetResultMessage();
                            //TODO другой тип
                            chatGetResultMessage.setType(Type.MSG_CHAT_LIST_RESULT);
                            chatGetResultMessage.setSenderId(session.getUser().getId());
                            chatGetResultMessage.setChatId(chatsId.get(i));
                            session.send(chatGetResultMessage);
                        }
                    }
                } else {
                    long chatId = messageOperations.addChat(session.getUser().getId());
                    messageOperations.addUserToChat(session.getUser().getId(), chatId);
                    for (int i = 0; i < ids.size(); i++) {
                        messageOperations.addUserToChat(ids.get(i), chatId);
                    }
                    StatusMessage statusMessage = new StatusMessage();
                    statusMessage.setStatus(Status.ACCEPTED);
                    session.send(statusMessage);
                }

            } else {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setType(Type.MSG_ERROR);
                errorMessage.setText("Sorry, this is available only for registered users.");
                session.send(errorMessage);
            }
        } catch (Exception e) {
            throw new CommandException("ChatCreateCommand " + e);
        }
    }
}
