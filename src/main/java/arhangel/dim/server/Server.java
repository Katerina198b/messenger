package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.DatabaseConnection;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageOperations;
import arhangel.dim.core.store.UserOperations;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.lections.socket.IoUtil;
import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = Runtime.getRuntime().availableProcessors();
    static Logger log = LoggerFactory.getLogger(Server.class);
    private List<Session> currentSessions = new ArrayList<>();

    private int port;
    private Protocol protocol;
    private ServerSocket serverSocket;
    private DatabaseConnection database;

    public void removeUser(Session session) {
        currentSessions.remove(session);
    }

    public DatabaseConnection getDatabase() {
        return database;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public List<Session> getCurrentSessions() {
        return currentSessions;
    }

    public void addSession(Session session) {
        currentSessions.add(session);
    }

    public void stop(ExecutorService pool) {
        pool.shutdown();
    }

    public static void main(String[] args) {

        try {
            Container context = new Container("server.xml");
            final Server server = (Server) context.getByName("server");
            server.serverSocket = new ServerSocket(server.port);
            log.info("Started, waiting for connection");
            // заставляем сервер ждать подключений и выводим сообщение когда
            // кто-то связался с сервером
            ExecutorService pool = Executors.newFixedThreadPool(DEFAULT_MAX_CONNECT);
            while (!Thread.currentThread().isInterrupted()) {
                // Блокируется до возникновения нового соединения:
                try {
                    Socket socket = server.serverSocket.accept();
                    pool.submit(() -> {
                        try {
                            log.info("Accepted. " + socket.getInetAddress());
                            Session session = new Session(socket, server);
                            while (!Thread.currentThread().isInterrupted()) {
                                session.expectMessage();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            log.error("Failed to creating session: ", e);
                        }
                    });
                } catch (NullPointerException e) {
                    log.error("Failed to create socket: ", e);
                }

            }
            server.stop(pool);

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to reading: {}", e);
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create server", e);
        }
    }
}


