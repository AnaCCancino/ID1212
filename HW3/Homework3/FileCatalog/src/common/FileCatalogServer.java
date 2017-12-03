package common;

import server.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * The remote methods of a File Catalog server.
 */
public interface FileCatalogServer extends Remote {

    /**
     * The default URI of the File Catalog server in the RMI registry.
     */
    public static final String SERVER_NAME_IN_REGISTRY = "FILE_CATALOG_SERVER";

    /**
     * New account manager
     *
     * @param credentials The credentials of the logging client
     * @return The id of the joining participant. A participant must use this id for identification
     *         in all communication with the server.
     * @throws RemoteException
     * @throws PersonException
     */
    long login( Credentials credentials, FileCatalogClient client) throws RemoteException, PersonException;

    /**
     * This is to logout the client
     *
     * @param userId this is the ID of the client
     * @throws RemoteException
     * @throws PersonException
     */
    void logout(long userId) throws RemoteException,PersonException;

    /**
     * This is to register a client
     *
     * @param credentials The user's information to register
     * @throws RemoteException
     * @throws PersonException
     * @throws AlreadyRegisteredException
     */
    void register(Credentials credentials) throws RemoteException, PersonException, AlreadyRegisteredException;

    /**
     * This is to unregister a client
     *
     * @param username The user's username (to know who to unregister)
     * @throws RemoteException
     * @throws PersonException
     */
    void unregister(String username) throws RemoteException, PersonException;

    /**
     *
     * @param userId
     * @param fileName
     * @param size
     * @param accessPermission
     * @param readWritePermission
     * @throws IOException
     * @throws UnauthorizedException
     */
    void uploadFile(long userId, String fileName, int size, AccessPermission accessPermission, ReadWritePermission readWritePermission) throws IOException, UnauthorizedException, FileException;

    /**
     *
     * @param userId
     * @param filename
     * @return
     * @throws IOException
     * @throws UnauthorizedException
     */
    String downloadFile(long userId, String filename) throws IOException, UnauthorizedException, PersonException;

    /**
     *
     * @param userId
     * @param filename
     * @throws IOException
     * @throws PersonException
     * @throws UnauthorizedException
     * @throws FileException
     */
    void deleteFile(long userId, String filename) throws IOException, PersonException, UnauthorizedException, FileException;

    /**
     *
     * @param userId
     * @param filename
     * @param accessPermission
     * @param readWritePermission
     * @throws RemoteException
     * @throws UnauthorizedException
     * @throws PersonException
     * @throws FileNotFoundException
     */
    void modifyFile(long userId, String filename, AccessPermission accessPermission, ReadWritePermission readWritePermission) throws RemoteException, UnauthorizedException, PersonException, FileNotFoundException;

    /**
     *
     * @param userId
     * @return
     * @throws RemoteException
     */
    Collection<File> listFiles(long userId) throws RemoteException;

    /**
     *
     * @param userId
     * @param filename
     * @param notify
     * @throws RemoteException
     * @throws PersonException
     * @throws UnauthorizedException
     * @throws FileNotFoundException
     */
    void notifyFile(long userId, String filename, boolean notify) throws RemoteException, PersonException, UnauthorizedException, FileNotFoundException;
}
