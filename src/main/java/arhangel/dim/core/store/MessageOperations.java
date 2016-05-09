package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;

import java.util.List;

public class MessageOperations implements MessageStore {
    @Override
    public List<Long> getChatsByUserId(Long userId) {
        return null;
    }

    @Override
    public Chat getChatById(Long chatId) {
        return null;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {
        return null;
    }

    @Override
    public Message getMessageById(Long messageId) {
        return null;
    }

    @Override
    public void addMessage(Long chatId, Message message) {

    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {

    }
}
