package server.net;

import client.controller.Controller;
import common.MsgType;
import common.Message;
import common.MessageException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

/**
 * Handles all communication with one particular Hangman client.
 */
class ClientHandler implements Runnable {
    private final HangmanServer server;
    private final Socket clientSocket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;

    private final server.controller.Controller contr = new server.controller.Controller();
    private boolean connected;

    /**
     * Creates a new instance, which will handle communication with one specific client connected to
     * the specified socket.
     * @param clientSocket The socket to which this handler's client is connected.
     */
    ClientHandler(HangmanServer server, Socket clientSocket ) {
        this.server = server;
        this.clientSocket = clientSocket;
        connected = true;
    }

    /**
     * The run loop handling all communication with the connected client.
     */
    @Override
    public void run() {
        try {
            fromClient = new ObjectInputStream(clientSocket.getInputStream());
            toClient = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }

        while (connected) {
            try {
                Message msg = (Message) fromClient.readObject();
                switch (msg.getType()) {
                    case START:
                        System.out.println("SERVER RECEIVED START");
                        sendMsg(contr.start_game());
                        break;
                    case GUESS:
                        System.out.println("SERVER RECEIVED GUESS");
                        sendMsg(contr.handle_guess(msg.getBody().toString()));
                        break;
                    case QUIT:
                        System.out.println("SERVER RECEIVED QUIT");
                        disconnectClient();
                        break;
                    default:
                        throw new MessageException("Received corrupt message: " + msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                disconnectClient();
                throw new MessageException(e);
            }
        }
    }

    /**
     * Sends the INFO message to the connected client .
     *
     * @param msgBody The message to send.
     */
    void sendMsg(String msgBody) throws UncheckedIOException {
        try {
            Message msg = new Message(MsgType.INFO, msgBody);
            toClient.writeObject(msg);
            toClient.flush();
            toClient.reset();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    /**
     * To disconect a Client
     */
    private void disconnectClient() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
    }


}
