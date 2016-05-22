package arhangel.dim.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import arhangel.dim.core.messages.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

/**
 * Клиент для тестирования серверного приложения
 */
public class Client implements ConnectionHandler {

    static Logger log = LoggerFactory.getLogger(Client.class);
    private ByteBuffer buffer = allocate(255);

    /**
     * Протокол, хост и порт инициализируются из конфига
     */
    private Protocol protocol;
    private int port;
    private String host;

    /**
     * Тред "слушает" сокет на наличие входящих сообщений от сервера
     */
    public  Selector selector;
    public  SocketChannel channel;
    private boolean interrupted;

    private long senderId;

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Инициализируем сокет и слушаем входной поток от сервера
     */
    public void initSocket() throws IOException {

        /**

         Socket socket = new Socket(host, port);
         in = socket.getInputStream();
         out = socket.getOutputStream();

         socketThread = new Thread(() -> {
         final byte[] buf = new byte[1024 * 64];
         log.info("Starting listener thread...");
         while (!Thread.currentThread().isInterrupted()) {
         try {
         //if (in.available() > 0)
         int read = in.read(buf);
         if (read > 0) {
         Message msg = protocol.decode(Arrays.copyOf(buf, read));
         onMessage(msg);
         }
         } catch (SocketException e) {
         return;

         } catch (IOException | ProtocolException e) {
         log.error("Failed into initSocket");
         e.printStackTrace();
         }
         }
         });

         socketThread.start();
         */
    }

    /**
     * Реагируем на входящее сообщение
     */
    @Override
    public void onMessage(Message msg) {
        try {
            if (msg.getType() == Type.MSG_INFO_RESULT) {
                InfoResultMessage infoResultMessage = (InfoResultMessage) msg;
                if (infoResultMessage.getNewSession()) {
                    this.setSenderId(infoResultMessage.getUserId());
                    System.out.println("Login is successful");
                }
            }
            System.out.println(msg.toString());
        } catch (Exception e) {
            System.out.println("Server is not available. Please restart.");
            interrupted = true;
        }
        System.out.println("$");

    }

    public void invalidInput() {

        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setText("Invalid input. For more information please enter \"/help\"");
        log.error("processInput : Invalid input");
        this.onMessage(errorMessage);

    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     *
     * @throws IOException, ProtocolException
     */
    public Message processInput(String line) throws IOException, ProtocolException {

        String[] tokens = line.split(" ");
        log.info("processInput: Tokens: {}", Arrays.toString(tokens));
        String cmdType = tokens[0];
        switch (cmdType) {

            case "/login":
                if (tokens.length == 3) {
                    LoginMessage loginMessage = new LoginMessage();
                    loginMessage.setSenderId(getSenderId());
                    loginMessage.setLogin(tokens[1]);
                    loginMessage.setPassword(tokens[2]);
                    return loginMessage;
                } else {
                    this.invalidInput();
                }
                return null;

            /**
             * Cообщение, которое не отправляется серверу
             */
            case "/help":
                HelpMessage helpMessage = new HelpMessage();
                helpMessage.setSenderId(getSenderId());
                onMessage(helpMessage);
                return null;

            case "/text":
                if (tokens.length > 2) {
                    TextMessage textMessage = new TextMessage();
                    textMessage.setSenderId(getSenderId());
                    textMessage.setChatId(tokens[1]);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 2; i < tokens.length; i++) {
                        builder.append(tokens[i]).append(" ");
                    }
                    textMessage.setText(builder.toString());
                    return textMessage;
                } else {
                    this.invalidInput();
                }
                return null;

            case "/info":
                switch (tokens.length) {

                    case 1:
                        InfoMessage infoMessage = new InfoMessage();
                        // число -1 будет обозначать запрос о себе
                        infoMessage.setSenderId(getSenderId());
                        infoMessage.setUserId(-1L);
                        return infoMessage;


                    case 2:
                        InfoMessage message = new InfoMessage();
                        message.setSenderId(getSenderId());
                        message.setUserId(tokens[1]);
                        return message;

                    default:
                        this.invalidInput();
                        return null;
                }

            case "/chat_list":
                if (tokens.length == 1) {
                    ChatListMessage chatListMessage = new ChatListMessage();
                    chatListMessage.setSenderId(getSenderId());
                    return chatListMessage;
                } else {
                    this.invalidInput();
                    return null;
                }

            case "/chat_create":
                if (tokens.length > 1) {
                    ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                    chatCreateMessage.setSenderId(this.getSenderId());
                    for (int i = 1; i < tokens.length; i++) {
                        chatCreateMessage.addId(tokens[i]);
                    }
                    return chatCreateMessage;
                } else {
                    this.invalidInput();
                }
                return null;

            case "/chat_history":
                if (tokens.length == 2) {
                    ChatHistMessage chatHistMessage = new ChatHistMessage();
                    chatHistMessage.setSenderId(getSenderId());
                    chatHistMessage.setChatId(tokens[1]);
                    return chatHistMessage;
                } else {
                    this.invalidInput();
                }
                return null;

            default:
                this.invalidInput();
                return null;
        }
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    @Override
    public void send(Message msg) throws IOException, ProtocolException {

    }

    /**
     * Молча (без проброса ошибок) закрываем соединение и освобождаем ресурсы
     */
    @Override
    public void close() {

        try {
            channel.close();
            selector.close();
        } catch (SocketException e) {
            return;
        } catch (IOException e) {
            log.error("Failed into close.");
            e.printStackTrace();
        }

    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Client client = (Client) other;
        return Objects.equals(port, client.port) &&
                Objects.equals(protocol, client.protocol) &&
                Objects.equals(host, client.host);
    }

    public static void main(String[] args) throws Exception {

        Client client = null;

        try {
            Container context = new Container("client.xml");
            client = (Client) context.getByName("client");
        } catch (InvalidConfigurationException e) {
            log.error("Client: main: Failed to create client.", e);
        }

        client.channel = SocketChannel.open();
        client.channel.configureBlocking(false);
        client.selector = Selector.open();
        client.channel.register(client.selector, OP_CONNECT);
        client.channel.connect(new InetSocketAddress(client.host, client.port));


        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);
        System.out.println("$");
        final Client finalClient = client;
        Thread enter = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    finalClient.interrupted = true;
                    finalClient.selector.wakeup();
                    return;
                }
                try {
                    queue.put(input);
                } catch (Exception e) {
                    log.error("Client: main: Failed to process user input.", e);
                }
                SelectionKey key = finalClient.channel.keyFor(finalClient.selector);
                key.interestOps(OP_WRITE);
                finalClient.selector.wakeup();
            }
        });
        enter.start();


        while (!client.interrupted) {
            client.selector.select();
            for (SelectionKey selectionKey : client.selector.selectedKeys()) {
                if (selectionKey.isConnectable()) {
                    // подключиться
                    client.channel.finishConnect();
                    selectionKey.interestOps(OP_WRITE);
                } else if (selectionKey.isReadable()) {
                    client.buffer.clear();
                    client.channel.read(client.buffer);
                    Message message = client.protocol.decode(client.buffer.array());
                    selectionKey.interestOps(OP_WRITE);
                    client.onMessage(message);
                    client.buffer.flip();
                    Thread.sleep(500);

                } else if (selectionKey.isWritable()) {
                    String line = queue.poll();
                    if (line != null) {
                        Message message = client.processInput(line);
                        if (message != null) {
                            client.channel.write(ByteBuffer.wrap(client.protocol.encode(message)));
                        }
                    }
                    selectionKey.interestOps(OP_READ);
                }
            }
        }
        client.close();
    }
}
