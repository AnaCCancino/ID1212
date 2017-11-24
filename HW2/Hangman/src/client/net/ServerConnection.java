package client.net;

import common.Constants;
import common.MsgType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Manages all communication with the server NON-BLOCKING.
 */
public class ServerConnection implements Runnable {

    private final ByteBuffer fromServer = ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);
    private final Queue<ByteBuffer> toServer = new ArrayDeque<>();
    private OutputHandler listener;
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private Selector selector;
    private boolean connected;
    private volatile boolean timeToSend = false;


    /**
     * The communicating thread, all communication is non-blocking. First, server connection is
     * established. Then the thread sends messages submitted via one of the <code>send</code>
     * methods in this class and receives messages from the server.
     */
    @Override
    public void run() {

        try {
            startSelectorAndSocketChannels();
            while (connected || !toServer.isEmpty()) {
                if (timeToSend) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }
                selector.select();
                for (SelectionKey key :
                        selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid())
                        continue;
                    if (key.isConnectable()) {
                        completeConnection(key);
                    } else if (key.isReadable()) {
                        recvFromServer(key);
                    } else if (key.isWritable()) {
                        sendToServer(key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lost connection, error main loop.");
        }
        try {
            doDisconnect();
        } catch (IOException ex) {
            System.err.println("Could not disconnect, will leave ungracefully.");
        }
    }

    private void startSelectorAndSocketChannels() throws IOException{
        //Start Socket Channel
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        connected = true;

        //Start Selector
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

    }

    /**
     * Starts the communicating thread and connects to the server.
     *
     * @param host Host name or IP address of server.
     * @param port Server's port number.
     * @throws IOException If failed to connect.
     */
    public void connect(String host, int port, OutputHandler listener) throws IOException {
        serverAddress = new InetSocketAddress(host, port);
        this.listener =listener;
        new Thread(this).start();

    }

    private void completeConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
            Executor pool = ForkJoinPool.commonPool();
            pool.execute(() -> listener.connected(remoteAddress));
        } catch (IOException couldNotGetRemAddrUsingDefaultInstead) {
            Executor pool = ForkJoinPool.commonPool();
            pool.execute(() -> listener.connected(serverAddress));
        }
    }

    /**
     * Stops the communicating thread and closes the connection with the server.
     *
     * @throws IOException If failed to close connection.
     */
    public void disconnect() throws IOException {
        connected = false;
        sendMsg(MsgType.QUIT.toString());
    }

    private void doDisconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
        Executor pool = ForkJoinPool.commonPool();
        pool.execute(listener::disconnected);
    }

    /**
     * Sends the user's username to the server. That username will be prepended to all messages
     * originating from this client, until a new username is specified.
     */
    public void sendStart() {
        sendMsg(MsgType.START.toString());
    }

    /**
     * Sends a the GUESS the user entered to the server
     * @param msg
     */
    public void sendGuess(String msg) {
        sendMsg(MsgType.GUESS.toString() + "###" + msg.toString());
    }

    private void sendMsg(String string) {
        synchronized (toServer) {
            toServer.add(ByteBuffer.wrap(string.getBytes()));
        }
        timeToSend = true;
        selector.wakeup();
    }

    private void sendToServer(SelectionKey key) throws IOException {
        ByteBuffer msg;
        synchronized (toServer) {
            while ((msg = toServer.peek()) != null) {
                socketChannel.write(msg);
                if (msg.hasRemaining()) {
                    return;
                }
                toServer.remove();
            }
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void recvFromServer(SelectionKey key) throws IOException {
        fromServer.clear();
        int numOfReadBytes = socketChannel.read(fromServer);
        if (numOfReadBytes == -1) {
            throw new IOException("Lost connection.");
        }
        String recvdString = extractMessageFromBuffer();
        System.out.println(recvdString);

        Executor pool = ForkJoinPool.commonPool();
        pool.execute(() -> listener.handleMsg(recvdString));
    }

    private String extractMessageFromBuffer() {
        fromServer.flip();
        byte[] bytes = new byte[fromServer.remaining()];
        fromServer.get(bytes);
        String message = new String(bytes);
        String[] messageReceivedPartitioned = message.split("###");
        System.out.println(messageReceivedPartitioned[0] +"mmm" +messageReceivedPartitioned[1]);
        if (messageReceivedPartitioned[0].equals(MsgType.INFO)) {
            return messageReceivedPartitioned[1];
        }else {
            return "Error in received Message";
        }
    }

}

