package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The callback methods of a File Catalog client.
 */
public interface FileCatalogClient extends Remote {

    /**
     * The specified message is received by the client.
     *
     * @param msg The message that shall be received.
     */
    void handleMessage(String msg) throws RemoteException;



}
