package client.net;

import java.net.InetSocketAddress;

/**
 * Handles incoming messages from server.
 */
public interface OutputHandler {
    /**
     * Called when a INFO message from the server has been received. That message originates
     * from one of the clients.
     *
     * @param msg The message from the server.
     */
    public void handleMsg(String msg);

    /**
     * Called when the local client is successfully connected to the server.
     *
     * @param serverAddress The address of the server to which connection is established.
     */
    public void connected(InetSocketAddress serverAddress);

    /**
     * Called when the local client is successfully disconnected from the server.
     */
    public void disconnected();
}
