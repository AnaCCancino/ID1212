package server.net;

import common.Constants;
import common.MsgType;
import common.MessageException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ForkJoinPool;


/**
 * Handles all communication with one particular Hangman client.
 */
class ClientHandler implements Runnable {
    private final HangmanServer server;
    private final SocketChannel clientChannel;
    private final ByteBuffer fromClient = ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);

    private final Queue<String> messagesReceived = new ArrayDeque<>();
    private final Queue<ByteBuffer> toClient = new ArrayDeque<>();
    private SelectionKey key;

    private final server.controller.Controller contr = new server.controller.Controller();
    private boolean connected;

    /**
     * Creates a new instance, which will handle communication with one specific client connected to
     * the specified socket.
     *
     * @param clientChannel The socket to which this handler's client is connected.
     */
    ClientHandler(HangmanServer server, SocketChannel clientChannel) {
        this.server = server;
        this.clientChannel = clientChannel;
        connected = true;
    }


    /**
     * The run loop handling all communication with the connected client.
     */
    @Override
    public void run() {
        while (!messagesReceived.isEmpty()) {
            try {
                String readLine;
                synchronized (messagesReceived) {
                    readLine = messagesReceived.poll();
                }
                //if (readLine == null)
                    //throw new Exception("No messagesReceived");
                String[] messageReceived = readLine.split("###");
                switch (messageReceived[0]) {//msg.msgType) {
                    case MsgType.START:
                        System.out.println("SERVER RECEIVED START");
                        //sendMsg(sendMsgToByteBuffer(contr.start_game()));
                        queueMsg(contr.start_game());
                        break;
                    case MsgType.GUESS:
                        System.out.println("SERVER RECEIVED GUESS");
                        //sendMsg(sendMsgToByteBuffer(contr.handle_guess(msg.msgBody)));
                        queueMsg(contr.handle_guess(messageReceived[1]));
                        break;
                    case MsgType.QUIT:
                        System.out.println("SERVER RECEIVED QUIT");
                        disconnectClient();
                        break;
                    default:
                        throw new MessageException("Received corrupt message: "); //+ msg.receivedString);
                }
            } catch (IOException  e) {
                try {
                    disconnectClient();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                throw new MessageException(e);
            }
        }
    }

    /**
     * Sends the INFO message to the connected client.
     * @throws IOException
     */
    void sendMsg() throws IOException {
        ByteBuffer msg = ByteBuffer.wrap("FAILED".getBytes());
        synchronized (toClient) {
            if (!toClient.isEmpty())
                msg = toClient.poll();
        }
        clientChannel.write(msg);

    }

    /**
     * Reads a message from the connected client, then submits a task to the default
     * <code>ForkJoinPool</code>. That task which will handle the received message.
     *
     * @throws IOException If failed to read message
     */
    void recvMsg() throws IOException {
        fromClient.clear();
        int numOfReadBytes = clientChannel.read(fromClient);
        if (numOfReadBytes == -1) {
            throw new IOException("Client has closed connection.");
        }

        fromClient.flip();
        byte[] bytes = new byte[fromClient.remaining()];
        fromClient.get(bytes);
        String recvdString = new String(bytes);

        synchronized (messagesReceived) {
            messagesReceived.add(recvdString);
        }
        ForkJoinPool.commonPool().execute(this);

    }


    /**
     * Closes this instance's client connection.
     *
     * @throws IOException If failed to close connection.
     */
    void disconnectClient() throws IOException {
        clientChannel.close();
        connected = false;
    }


    void registerKey(SelectionKey key) {
        this.key = key;
    }

    private void queueMsg(String s) {
        s=MsgType.INFO+"###" +s;
        ByteBuffer msg = ByteBuffer.wrap(s.getBytes());
        synchronized (toClient) {
            toClient.add(msg);
        }
        server.sendReady(key);
        server.wakeup();
    }
}
