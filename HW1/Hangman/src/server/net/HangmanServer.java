package server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Receives Hangman messages and answers. All communication to/from any
 * hangman node pass this server.
 */
public class HangmanServer {
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private int portNo = 4444;

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
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }

    /**
     * Starts the client socket
     * @param clientSocket
     * @throws SocketException
     */
    private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(this, clientSocket);
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }


}
