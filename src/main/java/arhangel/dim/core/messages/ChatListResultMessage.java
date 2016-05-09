package arhangel.dim.core.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatListResultMessage extends Message {
    // получить список чатов пользователя(только для залогиненных пользователей).
    // От сервера приходит список id чатов

    private List<Integer> chats = new ArrayList<Integer>();

    public int length() {
        return chats.size();
    }

    public Integer getChat(int id) {
        return chats.get(id);
    }

    public void setChat(String id) {
        chats.add(Integer.valueOf(id));
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
