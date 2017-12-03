package server.integration;


import common.AccessPermission;
import common.Credentials;
import common.FileCatalogClient;
import common.ReadWritePermission;
import server.model.*;
import server.model.Person;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.*;

/**
 * This data access object (DAO) encapsulates all database calls in the FileCatalog application. No code
 * outside this class shall have any knowledge about the database.
 */
public class FileCatalogDAO {

    private final EntityManagerFactory emFactory;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();

    private final Map<Long, Person> usersLoggedIn = Collections.synchronizedMap(new HashMap<>());
    private final Map<Long, Long> userIDtoKey = Collections.synchronizedMap(new HashMap<>());
    private final Random idGenerator = new Random();

    private HashMap<Long, FileCatalogClient> notifyPersons = new HashMap<Long, FileCatalogClient> ();


    public FileCatalogDAO(){
        emFactory = Persistence.createEntityManagerFactory("fileCatalogPersistenceUnit");
    }

    public long login(Credentials credentials,  FileCatalogClient client){
        Person person =  findPersonByUsername(credentials.getUsername());
        if(person != null){
            addNotifyPerson(person.getUserID(),client);
            if(person.getPassword().equals(credentials.getPassword())){
                long id = idGenerator.nextLong();
                if(userIDtoKey.containsKey(person.getUsername())){
                    long tmp = userIDtoKey.get(person.getUserID());
                    usersLoggedIn.remove(tmp); //Remove old key
                    userIDtoKey.put(person.getUserID(), id);
                    usersLoggedIn.put(id, person);
                }
                else{
                    usersLoggedIn.put(id, person);
                    userIDtoKey.put(person.getUserID(),id);
                }
                return id;
            }
        }
        return -1;
    }

    public void logout(long key) throws PersonException {
        Person person = usersLoggedIn.remove(key);
        if(person == null){
            throw new PersonException("Person is not logged in.");
        }
        else{
            userIDtoKey.remove(person.getUserID());
        }
    }

    public void register(Credentials credentials) throws AlreadyRegisteredException {
        Person person = findPersonByUsername(credentials.getUsername());
        if(person == null){
            register(new Person(credentials.getUsername(),credentials.getPassword()));
        }
        else{
            throw new AlreadyRegisteredException("Person " + credentials.getUsername() + " is already registered!");
        }
    }

    public void unregister(String username) throws PersonException {
        try{
            EntityManager em = beginTransaction();
            int res = em.createNamedQuery("deleteUserByName",Person.class).setParameter("username",username).executeUpdate();
            if(res != 1){
                throw new PersonException("Could not delete user: " + username);
            }
        }
        finally{
            commitTransaction();
        }
    }

    public Person findPersonByUsername(String username){
        if(username == null){
            return null;
        }
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findUserByName", Person.class).setParameter("username", username).getSingleResult();
            } catch (NoResultException noSuchPerson) {
                return null;
            }
        }
        finally{
            commitTransaction();
        }
    }

    private void commitTransaction() {
        threadLocalEntityManager.get().getTransaction().commit();
    }

    private EntityManager beginTransaction() {
        EntityManager em = emFactory.createEntityManager();
        threadLocalEntityManager.set(em);
        EntityTransaction transaction = em.getTransaction();
        if(!transaction.isActive()){
            transaction.begin();
        }
        return em;
    }

    public boolean register(Person newPerson) {
        try {
            EntityManager em = beginTransaction();
            em.persist(newPerson);
            return true;
        }
        catch(Exception e){
            return false;
        }
        finally{
            commitTransaction();
        }
    }

    private File findFileByName(String filename, boolean endTransactionAfterSearching){
        if(filename == null){
            return null;
        }
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findFileByName", File.class).setParameter("filename", filename).getSingleResult();
            } catch (NoResultException noSuchFile) {
                return null;
            }
        }
        finally{
            if(endTransactionAfterSearching){
                commitTransaction();
            }
        }
    }

    public void createFile(String name, int size, Person owner, AccessPermission accessPermission, ReadWritePermission readWritePermission ) throws FileException {
        File file1 = findFileByName(name, false);
        if (file1 == null){
            File file = new File(name, size, owner, accessPermission, readWritePermission);
            try {
                EntityManager em = beginTransaction();
                em.persist(file);
            } finally {
                commitTransaction();
            }
        }  else throw new FileException("The file is already made, change the name, there cannot be two files with the same name.");

    }

    public Person getUser(long key){
        return usersLoggedIn.get(key);
    }
    
    public Collection<File> listFiles(long userID){
        try{
            EntityManager em = beginTransaction();
            return em.createNamedQuery("getUserFiles",File.class).setParameter("userID",userID).getResultList();
        }
        finally {
            commitTransaction();
        }
    }

    public void deleteFile(Person user, String filename) throws FileException, IOException, UnauthorizedException, PersonException {
        File file = findFileByName(filename,true);
        if (file.getAccessPermission() == AccessPermission.PUBLIC || file.getOwner().getUsername().equals(user.getUsername()) ) {
            if (file != null) {
                if (file.getOwner().getUserID() == user.getUserID()) {
                    deleteFile(file);
                    notifyTheUserThatItChanged(file,user, "deleted");
                } else if (file.getAccessPermission() == AccessPermission.PUBLIC) {
                    deleteFile(file);
                    notifyTheUserThatItChanged(file,user, "deleted");
                } else {
                    throw new UnauthorizedException("Permission denied to delete file");
                }

            } else {
                throw new FileNotFoundException("File " + filename + " not found");
            }
        } else throw new PersonException("This user cannot delete this file");
    }

    public void notifyTheUserThatItChanged(File file, Person user,String action){
        if (file.isNotify()) {
            FileCatalogClient client = notifyPersons.get(file.getOwner().getUserID());
            if (client != null) {
                try {
                    client.handleMessage("The file: " + file.getName() + " has been "+action+" by " +
                            user.getUsername());
                } catch (RemoteException e) {
                    System.out.println("Could not notify user " + file.getOwner().getUsername());
                }
            }
        }
    }


    public void deleteFile(File file) throws FileException {
        String filename = file.getName();
        try{
            EntityManager em = beginTransaction();
            int res = em.createNamedQuery("deleteFileByName",File.class).setParameter("filename",filename).executeUpdate();
            if(res != 1){
                throw new FileException("Could not delete file: " + filename);
            }
        }
        finally{
            commitTransaction();
        }
    }

    public void modifyFile(Person user, String filename, AccessPermission accessPermission, ReadWritePermission readWritePermission) throws UnauthorizedException, FileNotFoundException, PersonException {
        File file = findFileByName(filename,false);
        if(file != null && ((file.getOwner().getUserID() == user.getUserID())
                || (file.getAccessPermission()== AccessPermission.PUBLIC && file.getReadWritePermission() != ReadWritePermission.READ_ONLY_PERMISSION))){
            file.setAccessPermission(accessPermission);
            file.setReadWritePermission(readWritePermission);
            commitTransaction();
            notifyTheUserThatItChanged(file,user, "modified");
        }
        else if(file != null){
            throw new PersonException("This user cannot delete this file");
        }
        else if(user == null){
            throw new UnauthorizedException("Permission denied to modify file.");
        }
        else{
            throw new FileNotFoundException("File "+ filename +" not found.");
        }
    }

    public String readFile (Person person,String filename) throws PersonException{
        File file = findFileByName(filename, true);
        if (file.getAccessPermission() == AccessPermission.PUBLIC || file.getOwner().getUsername().equals(person.getUsername())) {
            String fileProperties = "The file is called: " + file.getName() +
                    "/n Owned by the user: " + file.getOwner().getUserID() + ", name: " + file.getOwner().getUsername() +
                    "/n It is in size: " + file.getSize() +
                    "/n And it is: " + file.getAccessPermission().toString() + " and " + file.getReadWritePermission().toString();
            notifyTheUserThatItChanged(file,person, "downloaded");
            return fileProperties;
        } else throw new PersonException("This user cannot download this file");
    }

    public void notifyFile(Person user, String filename, boolean notify) throws UnauthorizedException, FileNotFoundException {
        System.out.println("Notifying user,");
        File file = findFileByName(filename,false);
        if(file != null && file.getOwner().getUserID() == user.getUserID()){
            file.setNotify(notify);
            commitTransaction();
        }
        else if(file != null){
            commitTransaction();
            throw new UnauthorizedException("Permission denied to modify file!");
        }
        else{
            commitTransaction();
            throw new FileNotFoundException("File "+ filename +" not found!");
        }
    }

    public void addNotifyPerson(long userID, FileCatalogClient client) {
        notifyPersons.put(userID,client);
    }

}

