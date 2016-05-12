package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.User;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageOperations implements MessageStore {

    private Connection connection;
    private Logger log = LoggerFactory.getLogger(MessageOperations.class);

    public MessageOperations() throws Exception {
        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Katerina198b",
                "trackuser", "trackuser");

    }

    @Override
    public List<Long> getChatsByUserId(Long userId) {
        String sql = "SELECT chat_id FROM CHAT_USER WHERE user_id = " + userId + ";";
        List<Long> chatList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            statement.close();
            while (rs.next()) {
                Long chatId = rs.getLong("chat_id");
                chatList.add(chatId);
                return chatList;
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in getChatsByUserId");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Chat getChatById(Long chatId) {

        User admin = new User();
        Chat chat = new Chat();

        String sql = "SELECT * FROM CHAT WHERE id = " + chatId + ";";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            statement.close();
            if (rs.next()) {
                chat.setId(rs.getLong("chat_id"));
                admin.setId(rs.getLong("owner_id"));
            }

            sql = "SELECT * FROM Users WHERE id = " + admin.getId() + ";";
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                admin.setName(rs.getString("login"));
                admin.setPassword("password");
                chat.setAdmin(admin);
            }

            sql = "SELECT user_id FROM CH_USER WHERE chat_id=" + chatId + ";";
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                Long userId = rs.getLong("user_id");
                if (userId != admin.getId()) {
                    chat.addParticipant(userId);
                }
            }

            sql = "SELECT * FROM" +
                    "(SELECT user_id FROM CH_USER WHERE chat_id = " + chatId + ") as USERS, MESSAGES" +
                    "WHERE chat_id = " + chatId +
                    "AND user_id IN USERS.user_id";
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                chat.addMessage(rs.getLong("id"));
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            log.error("Caught SQLException in getChatsByUserId");
            e.printStackTrace();
        }
        return chat;
    }

    @Override
    public List<Long> getMessagesFromChat(Long chatId) {

        Chat chat = getChatById(chatId);
        return chat.getMessages();
    }

    @Override
    public Message getMessageById(Long messageId) {

        String sql = "SELECT chat_id FROM MESSAGES WHERE id = " + messageId + ";";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            statement.close();
            if (rs.next()) {
                TextMessage textMessage = new TextMessage();
                textMessage.setSenderId(rs.getLong("user_id"));
                textMessage.setChatId(rs.getLong("chat_id"));
            }
        } catch (SQLException e) {
            log.error("Caught SQLException in getChatsByUserId");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addMessage(Long chatId, Message message) {
        TextMessage textMessage = (TextMessage) message;
        String sql = "INSERT INTO MESSAGES (user_id,text,chat_id) VALUES (" +
                textMessage.getSenderId() + "," +
                textMessage.getText() + "," +
                chatId + ";";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();

        } catch (SQLException e) {
            log.error("Caught SQLException in addMessage");
            e.printStackTrace();
        }

    }

    @Override
    public void addUserToChat(Long userId, Long chatId) {
        String sql = "INSERT INTO CH_USER (user_id,chat_id) VALUES (" +
                userId + "," + chatId + ";";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();

        } catch (SQLException e) {
            log.error("Caught SQLException in addUserToChat");
            e.printStackTrace();
        }

    }

    public long addChat(long ownerId) {
        String sql = "INSERT INTO CHAT (owner_id) VALUES (" + ownerId + ");";
        long id = -1;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.commit();
            sql = "SELECT id FROM CHAT WHERE owner_id = " + ownerId + ";";
            ResultSet rs = statement.executeQuery(sql);
            connection.commit();
            if (rs.next()) {
                id = (rs.getLong("id"));
            }
            statement.close();

        } catch (SQLException e) {
            log.error("Caught SQLException in addChat");
            e.printStackTrace();
        }

        return id;

    }
}
