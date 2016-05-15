package arhangel.dim.core.messages;

/**
 * получить список чатов пользователя
 * (только для залогиненных пользователей).
 * От сервера приходит список id чатов
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatListMessage extends Message {

    public ChatListMessage() {
        this.setType(Type.MSG_CHAT_LIST);
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
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
        return "ChatListMessage";
    }
}
