package server.startup;

import server.controller.Controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main class of the Server
 */
public class Main {

    /**
     * Main method
     * @param args arguments (this time none)
     */
    public static void main(String[] args) {
        try {
            Main server = new Main();
            server.startRMIServant();
            System.out.println("File Catalog Server started.");
        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Failed to start File Catalog Server.");
        }
    }

    /**
     * To start the RMI and the connection to the DB
     * @throws RemoteException
     * @throws MalformedURLException
     */
    private void startRMIServant() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller contr = new Controller();
        Naming.rebind(Controller.SERVER_NAME_IN_REGISTRY, contr);
    }

}


