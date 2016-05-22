package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.*;
import arhangel.dim.server.Server;
import com.sun.istack.internal.NotNull;
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


    private Socket socket;
    private Server server;

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
    private Connection connection;


    public Session(Socket socket, Server server) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
        protocol = server.getProtocol();
        connection = server.getDatabase().getConnection();
        this.server = server;
    }

    public void setUser(User user) {
        this.user = user;
        server.addSession(this);
    }

    public Connection getConnection() {
        return connection;
    }

    public User getUser() {
        return user;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {

        try {
            byte[] buf = protocol.encode(msg);
            log.info("send: The message is sent to client");
            out.write(buf);
            out.flush();

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

        try {
            byte[] buf = new byte[32 * 1024];
            in.read(buf);

            Message msg = protocol.decode(buf);
            if (msg == null) {
                close();
                return;
            }
            log.info("Message is received...");
            onMessage(msg);

        } catch (Exception e) {
            log.error("expectMessage: ", e);
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        server.removeUser(this);
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
