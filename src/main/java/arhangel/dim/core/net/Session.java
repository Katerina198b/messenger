package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Optional;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler {

    /**
     * Пользователь сессии, пока не прошел логин, user == null
     * После логина устанавливается реальный пользователь
     */
    private User user;

    // сокет на клиента
    private Socket socket;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;
    private Protocol protocol;
    private Logger log = LoggerFactory.getLogger(Session.class);


    /**
     * Для каждого потока (пользователя, сессии) должен быть свой connection
     */
    public Connection connection;


    public Session(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.protocol = server.getProtocol();
        this.setConnection();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Katerina198b",
                    "trackuser", "trackuser");
            connection.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed into setConnection");
        }


    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {

        try {
            byte[] buf = protocol.encode(msg);
            log.info("send: The message is sent to client");
            out.write(buf);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMessage(Message msg) {

        log.info("onMessage: {}", msg.toString());
        Type type = msg.getType();
        try {
            switch (type) {
                case MSG_CHAT_CREATE:
                    ChatCreateCommand chatCreateCommand = new ChatCreateCommand();
                    chatCreateCommand.execute(this, msg);
                    break;

                case MSG_CHAT_HIST:
                    ChatHistCommand chatHistCommand = new ChatHistCommand();
                    chatHistCommand.execute(this, msg);
                    break;

                case MSG_CHAT_LIST:
                    ChatListCommand chatListCommand = new ChatListCommand();
                    chatListCommand.execute(this, msg);
                    break;

                case MSG_INFO:
                    InfoCommand infoCommand = new InfoCommand();
                    infoCommand.execute(this, msg);
                    break;

                case MSG_LOGIN:
                    LoginCommand loginCommand = new LoginCommand();
                    loginCommand.execute(this, msg);
                    break;

                case MSG_TEXT:
                    TextCommand textCommand = new TextCommand();
                    textCommand.execute(this, msg);
                    break;

                default:
                    throw new ProtocolException("Invalid type: " + type);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void expectMessage() {

        while (in != null) {
            try {
                byte[] buf = new byte[32 * 1024];
                in.read(buf);
                log.info("Message is received...");
                Message msg = protocol.decode(buf);
                log.info("expectMessage: msg = {}", msg.toString());
                onMessage(msg);

            } catch (Exception e) {
                log.error("expectMessage: ", e);
                e.printStackTrace();
            }
        }
       this.close();

    }

    @Override
    public void close() {
        // TODO: закрыть in/out каналы и сокет. Освободить другие ресурсы, если необходимо
        try {
            log.info("close: Trying to close in/out channels and socket in Session");
            in.close();
            out.close();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.error("close: Can't close in/out channels");
            e.printStackTrace();
        }
    }
}
