package server.controller;

import common.*;
import server.integration.FileCatalogDAO;
import server.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

/**
 * Implementations of the catalog's remote methods, this is the only server class that can be called
 * remotely
 */
public class Controller extends UnicastRemoteObject implements FileCatalogServer {

    FileCatalogDAO fileCatalogDAO;
    
    public Controller() throws RemoteException{
        super();
        fileCatalogDAO = new FileCatalogDAO();
    }

    @Override
    public long login(Credentials credentials, FileCatalogClient client) throws RemoteException {
        long userId = fileCatalogDAO.login(credentials, client);
        return userId;
    }

    @Override
    public void logout(long userId) throws RemoteException, PersonException {
        fileCatalogDAO.logout(userId);
    }

    @Override
    public void register(Credentials credentials) throws AlreadyRegisteredException, PersonException {
        fileCatalogDAO.register(credentials);
    }

    @Override
    public void unregister(String username) throws PersonException {
        fileCatalogDAO.unregister(username);
    }

    @Override
    public Collection<File> listFiles(long userId) throws RemoteException {
        Person person = fileCatalogDAO.getUser(userId);
        long userID;
        if(person != null){
            userID = person.getUserID();
        }
        else{
            userID = -1;
        }
        return fileCatalogDAO.listFiles(userID);
    }

    @Override
    public void deleteFile(long userId, String filename) throws IOException, PersonException, UnauthorizedException, FileException {
        Person person = fileCatalogDAO.getUser(userId);
        if(person != null){
            fileCatalogDAO.deleteFile(person,filename);
        }
        else{
            throw new PersonException("Person is not logged in!");
        }
    }

    @Override
    public void modifyFile(long userId, String filename, AccessPermission accessPermission, ReadWritePermission readWritePermission) throws RemoteException, UnauthorizedException, PersonException, FileNotFoundException {
        Person person = fileCatalogDAO.getUser(userId);
        if(person != null){
            fileCatalogDAO.modifyFile(person,filename,accessPermission,readWritePermission);
        }
        else{
            throw new PersonException("Person is not logged in.");
        }
    }

    @Override
    public void uploadFile(long userId, String fileName, int size, AccessPermission accessPermission, ReadWritePermission readWritePermission) throws IOException, UnauthorizedException, FileException {
        Person person = fileCatalogDAO.getUser(userId);
        if(person != null){
            fileCatalogDAO.createFile(fileName, size, person, accessPermission, readWritePermission);
        }
        else{
            throw new UnauthorizedException("Person not logged in.");
        }
    }

    @Override
    public String downloadFile(long userId, String filename) throws IOException, UnauthorizedException, PersonException {
        Person person = fileCatalogDAO.getUser(userId);
        if(person == null){
            throw new UnauthorizedException("Person not logged in!");
        }
        return fileCatalogDAO.readFile(person, filename);
    }

    @Override
    public void notifyFile(long userId, String filename, boolean notify) throws RemoteException, PersonException, UnauthorizedException, FileNotFoundException {
        Person person = fileCatalogDAO.getUser(userId);
        if(person != null){
            fileCatalogDAO.notifyFile(person,filename,notify);
        }
        else{
            throw new PersonException("Person is not logged in!");
        }

    }
}