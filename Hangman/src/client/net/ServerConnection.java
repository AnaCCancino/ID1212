package client.net;

import common.Message;
import common.MessageException;
import common.MsgType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Manages all communication with the server.
 */
public class ServerConnection {
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private boolean connected;

    /**
     * Creates a new instance and connects to the specified server. Also starts a listener thread
     * receiving INFO messages from server.
     *
     * @param host             Host name or IP address of server.
     * @param port             Server's port number.
     * @param infoHandler Called whenever a broadcast is received from server.
     * @throws IOException If failed to connect.
     */
    public void connect(String host, int port, OutputHandler infoHandler) throws
            IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        connected = true;
        toServer = new ObjectOutputStream(socket.getOutputStream());
        fromServer = new ObjectInputStream(socket.getInputStream());
        new Thread(new Listener(infoHandler)).start();
    }

    /**
     * Closes the connection with the server and stops the broadcast listener thread.
     *
     * @throws IOException If failed to close socket.
     */
    public void disconnect() throws IOException {
        sendMsg(MsgType.QUIT, null);
        socket.close();
        socket = null;
        connected = false;
    }

    /**
     * Start a new game on the server
     */
    public void sendStart() throws IOException {
        sendMsg(MsgType.START, null);
        System.out.println("Send Start to server");
    }

    /**
     * Sends a the GUESS the user entered to the server
     *
     * @param msg The message to broadcast.
     */
    public void sendGuess(String msg) throws IOException {
        sendMsg(MsgType.GUESS, msg);
    }

    /**
     * Method used to send a message to the server
     * @param type
     * @param body
     * @throws IOException
     */
    private void sendMsg(MsgType type, String body) throws IOException {
        Message msg = new Message(type, body);
        toServer.writeObject(msg);
        toServer.flush();
        toServer.reset();
    }

    /**
     * This is the thread that listens for the INFO
     */
    private class Listener implements Runnable {
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    outputHandler.handleMsg(extractMsgBody((Message) fromServer.readObject()));
                }
            } catch (Throwable connectionFailure) {
                if (connected) {
                    outputHandler.handleMsg("Lost connection.");
                }
            }
        }

        /**
         * If receives something different than INFO the message is corrupt
         * @param msg
         * @return
         */
        private String extractMsgBody(Message msg) {
            if (msg.getType() != MsgType.INFO) {
                throw new MessageException("Received corrupt message: " + msg);
            }
            return msg.getBody();
        }
    }
}
