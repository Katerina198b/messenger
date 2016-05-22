package arhangel.dim.core.store;

import arhangel.dim.core.Chat;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;

public class MessageStoreTest {

    private static Connection connection;
    private static MessageOperations messageOperations;

    @BeforeClass
    public static void init() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Katerina198b",
                    "trackuser", "trackuser");
            connection.setAutoCommit(false);
            messageOperations = new MessageOperations(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getChatsByUserId() throws Exception {

        List<Long> chats = messageOperations.getChatsByUserId(24L);
        Assert.assertTrue(chats.get(0) - 1 == 0);
        chats = messageOperations.getChatsByUserId(1L);
        Assert.assertTrue(chats == null);


    }

    @Test
    public void getChatById() throws Exception {

        Chat chat = messageOperations.getChatById(2L);
        //Assert.assertTrue(chat.equals(null));

    }

    @Test
    public void getMessagesById() throws Exception {

        Message  message = messageOperations.getMessageById(3L);
        Assert.assertTrue(message == null);
        message = messageOperations.getMessageById(1L);
        Assert.assertTrue(message.getSenderId() == 12L);
    }

    @Test
    public void addMessage() throws Exception {

        TextMessage message = new TextMessage();
        message.setSenderId(12L);
        message.setText("The message");
        messageOperations.addMessage(1L, message);

    }


    //TODO если останется время то
    // припилить проверку на присутствие юзеа в чате

    @Test
    public void addUserToChat() throws Exception {

        messageOperations.addUserToChat(24L, 1L);

    }

    @Test
    public void addChat() throws Exception {

        messageOperations.addChat(24L);

    }

    @AfterClass
    public static void close() throws Exception{

        connection.close();

    }




}
