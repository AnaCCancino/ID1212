package server.net;

import common.MessageException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Receives Hangman messages and answers. All communication to/from any
 * hangman node pass this server.
 */
public class HangmanServer {
    private static final int LINGER_TIME = 5000;
    private int portNo = 4444;
    private final Queue<SelectionKey> toSend = new ArrayDeque<>();
    private Selector selector;
    private ServerSocketChannel listeningSocketChannel;

    /**
     * Start the server
     */
    public static void main(String[] args) {
        HangmanServer server = new HangmanServer();
        server.serve();
        System.out.println("Client connected.");
    }

    /**
     * Starts the Handler Thread
     */
    private void serve() {
        try {
            //Start Selectors and Socket Channel
            startSelectorAndSocketChannels();
            while (true) {
                while(!toSend.isEmpty()){
                    toSend.poll().interestOps(SelectionKey.OP_WRITE);
                }
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startHandler(key);
                    } else if (key.isReadable()) {
                        recvFromClient(key);
                    } else if (key.isWritable()) {
                        sendToClient(key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Server failure.");
        }
    }

    private void startSelectorAndSocketChannels() throws IOException{
        //Start Selector
        selector = Selector.open();
        //Start Socket Channel
        listeningSocketChannel = ServerSocketChannel.open();
        listeningSocketChannel.configureBlocking(false);
        listeningSocketChannel.bind(new InetSocketAddress(portNo));
        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }


    private void startHandler(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        ClientHandler handler = new ClientHandler(this, clientChannel);
        handler.registerKey(clientChannel.register(selector, SelectionKey.OP_READ, handler));
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
    }

    private void recvFromClient(SelectionKey key) throws IOException {
        ClientHandler client = (ClientHandler) key.attachment();
        try {
            client.recvMsg();
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }


    private void sendToClient(SelectionKey key) throws IOException {
        ClientHandler client = (ClientHandler) key.attachment();
        try {
            client.sendMsg();
            key.interestOps(SelectionKey.OP_READ);
        } catch (MessageException couldNotSendAllMessages) {
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }

    private void removeClient(SelectionKey clientKey) throws IOException {
        ClientHandler client = (ClientHandler) clientKey.attachment();
        client.disconnectClient();
        clientKey.cancel();
    }


    void sendReady(SelectionKey key) {
        toSend.add(key);
    }

    void wakeup(){
        selector.wakeup();
    }


}
