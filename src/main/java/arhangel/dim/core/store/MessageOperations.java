package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;
import com.sun.istack.internal.NotNull;
import org.omg.CORBA.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.Pack200;

public class MessageOperations implements MessageStore {

    private Connection connection;
    private Logger log = LoggerFactory.getLogger(MessageOperations.class);

    public MessageOperations(Connection connection) throws Exception {
        this.connection = connection;
    }

    @Override
    public List<Long> getChatsByUserId(Long userId) {

        List<Long> chatList = new ArrayList<>();
        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT DISTINCT chat_id FROM ch_user WHERE user_id = ?;");
            statement.setLong(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Long chatId = rs.getLong("chat_id");
                chatList.add(chatId);
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            log.error("Caught SQLException in getChatsByUserId");
            e.printStackTrace();
        }
        return chatList;
    }


    @Override
    public Chat getChatById(Long chatId) {

        Chat chat = new Chat();

        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM CHAT WHERE chat_id = ?;");
            statement.setLong(1, chatId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                chat.setId(rs.getLong("chat_id"));
                chat.setAdmin(rs.getLong("owner_id"));
            }
            rs.close();
            statement.close();

            statement = connection
                    .prepareStatement("SELECT user_id FROM CH_USER WHERE chat_id= ?;");
            statement.setLong(1, chat.getId());
            rs = statement.executeQuery();
            while (rs.next()) {
                Long userId = rs.getLong("user_id");
                if (userId != chat.getAdmin()) {
                    chat.addParticipant(userId);
                }
            }
            List<Long> messages = getMessagesFromChat(chatId);
            chat.setMessages(messages);

        } catch (SQLException e) {
            log.error("Caught SQLException in getChatsById");
            e.printStackTrace();
        }
        return chat;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {

        List<Long> messages = new ArrayList<>();
        try {
            String sql = "SELECT id FROM MESSAGE " +
                    "WHERE chat_id = ? AND user_id IN (SELECT user_id FROM CH_USER WHERE chat_id = ?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, chatId);
            statement.setLong(2, chatId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                messages.add(rs.getLong("id"));
            }
            rs.close();
            statement.close();
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("Caught SQLException in getMessagesFromChat");
        }

        Chat chat = getChatById(chatId);
        return chat.getMessages();
    }

    @Override
    public Message getMessageById(Long messageId) {

        TextMessage textMessage = null;
        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM MESSAGE WHERE id = ?;");
            statement.setLong(1, messageId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                textMessage = new TextMessage();
                textMessage.setSenderId(rs.getLong("user_id"));
                textMessage.setChatId(rs.getLong("chat_id"));
                textMessage.setText(rs.getString("text"));
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            log.error("Caught SQLException in getMessageById");
            e.printStackTrace();
        }
        return textMessage;
    }

    @Override
    public void addMessage(Long chatId, Message message) {

        TextMessage textMessage = (TextMessage) message;
        if (textMessage.getText().length() >= 300) {
            log.info("Accepted too long message");
            textMessage.setText(textMessage.getText().substring(0, 298));
        }
        try {
            PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO MESSAGE (user_id,text,chat_id) " +
                            "VALUES (?, ?, ?);");
            statement.setLong(1, textMessage.getSenderId());
            statement.setString(2, textMessage.getText());
            statement.setLong(3, textMessage.getChatId());
            statement.executeUpdate();
            statement.close();
            connection.commit();

        } catch (SQLException e) {
            log.error("Caught SQLException in addMessage");
            e.printStackTrace();
        }

    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {

        try {
            PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO CH_USER (user_id,chat_id) VALUES (?, ?);");
            statement.setLong(1, userId);
            statement.setLong(2, chatId);
            statement.executeUpdate();
            statement.close();
            connection.commit();

        } catch (SQLException e) {
            log.error("Caught SQLException in addUserToChat");
            e.printStackTrace();
        }

    }

    public long addChat(long ownerId) {


        long id = 0;
        try {
            PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO CHAT (owner_id) VALUES (?);");
            statement.setLong(1, ownerId);
            statement.executeUpdate();
            connection.commit();
            statement = connection
                    .prepareStatement("SELECT chat_id FROM CHAT WHERE owner_id = ?;");
            statement.setLong(1, ownerId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                id = (rs.getLong("chat_id"));
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            log.error("Caught SQLException in addChat");
            e.printStackTrace();
        }

        return id;
    }
}
