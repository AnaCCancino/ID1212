package client.startup;

import client.view.NonBlockingInterpreter;
import common.FileCatalogServer;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Starts the Client Interface
 */
public class Main {

    /**
     * The application takes the arguments, if you have.
     * @param args
     */
    public static void main(String[] args){
        try{
            FileCatalogServer server = (FileCatalogServer) Naming.lookup(FileCatalogServer.SERVER_NAME_IN_REGISTRY);
            new NonBlockingInterpreter().start(server);
            System.out.println("Start Client");
        }
        catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }

}
