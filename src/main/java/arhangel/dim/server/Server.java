package arhangel.dim.server;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.lections.socket.IoUtil;
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
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;
    static Logger log = LoggerFactory.getLogger(Server.class);

    // Засетить из конфига
    private int port;
    private Protocol protocol;
    private int maxConnection = DEFAULT_MAX_CONNECT;
    private ServerSocket serverSocket;

    public Protocol getProtocol() {
        return protocol;
    }

    public void stop() {
        // TODO: закрыть все сетевые подключения, остановить потоки-обработчики, закрыть ресурсы, если есть.
    }
    /*
    public ServerSocketChannel serverSocket() throws IOException {

        // Это серверный сокет
        ServerSocketChannel socketChannel = ServerSocketChannel.open();

        // Привязали его к порту
        socketChannel.socket().bind(new InetSocketAddress(port));

        // Должен быть неблокирующий для работы через selector
        socketChannel.configureBlocking(false);

        // Нас интересует событие коннекта клиента (как и для Socket - ACCEPT)
        //socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        return socketChannel;

    }
    */

    public static void main(String[] args) {
        // создали сервер из конфига
        Server server = null;
        try {
            Container context = new Container("server.xml");
            server = (Server) context.getByName("server");
            ServerSocket serverSocket = null;
            server.serverSocket = new ServerSocket(server.port);
            log.info("Started, waiting for connection");
            // заставляем сервер ждать подключений и выводим сообщение когда
            // кто-то связался с сервером
            ExecutorService pool = Executors.newFixedThreadPool(DEFAULT_MAX_CONNECT);
            log.info("Started, waiting for connection");
            // заставляем сервер ждать подключений и выводим сообщение когда
            // кто-то связался с сервером
            while (true) {
                final Server finalServer = server;
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = serverSocket.accept();
                            log.info("Accepted. " + socket.getInetAddress());
                            Session session = new Session(socket, finalServer);
                            session.expectMessage();


                        } catch (IOException e) {
                            e.printStackTrace();
                            log.error("Failed to creating socket in server: {}", e);
                        }

                    }
                });


            }


        } catch (IOException e) {
            e.printStackTrace();
            log.error("Failed to reading: {}", e);
        } catch (InvalidConfigurationException e) {
            log.error("Failed to create server", e);
            return;
        }
    }
}


