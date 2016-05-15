package arhangel.dim.core.messages;

/**
 * получить список чатов пользователя(только для залогиненных пользователей).
 * От сервера приходит список id чатов
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatListResultMessage extends Message {

    private List<Long> chats = new ArrayList<>();

    public void setChats(List<Long> chats) {
        this.chats.addAll(chats);
    }

    public ChatListResultMessage() {
        this.setType(Type.MSG_CHAT_LIST_RESULT);
    }

    public int length() {
        return chats.size();
    }

    public Long getChat(int id) {
        return chats.get(id);
    }

    public void addChat(String chat) {
        chats.add(Long.valueOf(chat));
    }

    public void addChat(long chat) {
        chats.add(chat);
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        ChatListResultMessage message = (ChatListResultMessage) other;
        return Objects.equals(chats, message.chats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chats);
    }

    @Override
    public String toString() {
        return "ChatListResultMessage{" +
                "chats=" +
                chats +
                '}';
    }

}
