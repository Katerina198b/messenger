package arhangel.dim.core.messages;

import com.sun.org.apache.xerces.internal.impl.dv.xs.IntegerDV;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatCreateMessage extends Message {
    private int chat;
    private List<Integer> ids = new ArrayList<>();


    public List<Integer> getIds() {
        return ids;
    }

    public int getIdsCount() {
        return ids.size();
    }


    public void addId(String id) {
        Integer intId = new Integer(id);
        ids.add(intId);
    }

    public int getId(int id) {
        return ids.get(id);
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
        ChatCreateMessage message = (ChatCreateMessage) other;
        return Objects.equals(ids, message.ids) && Objects.equals(chat, message.chat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ids);
    }

    @Override
    public String toString() {
        return "ChatCreateMessage{" +
                "chat=" +
                chat +
                "users ids=" +
                ids +
                '}';
    }
}
