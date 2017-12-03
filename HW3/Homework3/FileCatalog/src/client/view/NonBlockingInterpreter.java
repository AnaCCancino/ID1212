package client.view;

import common.*;
import server.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Scanner;

/**
 * Reads and interprets user commands. The command interpreter will run in a separate thread, which
 * is started by calling the <code>start</code> method. Commands are executed in a thread pool, a
 * new prompt will be displayed as soon as a command is submitted to the pool, without waiting for
 * command execution to complete.
 */
public class NonBlockingInterpreter implements Runnable {
    FileCatalogServer server;
    private long userId;
    private OutputHandler outputHandler;

    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private boolean receivingCmds = false;

    public NonBlockingInterpreter() throws RemoteException {
        this.outputHandler = new OutputHandler();
    }

    /**
     * Starts the interpreter. The interpreter will be waiting for user input when this method
     * returns. Calling <code>start</code> on an interpreter that is already started has no effect.
     *
     * @param server The server with which this client will communicate.
     */
    public void start(FileCatalogServer server) {
        this.server = server;
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
    }

    /**
     * Interprets and performs user commands.
     */
    @Override
    public void run() {
        //FileCatalogDTO fileCatalogDTO = null;
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case LOGIN:
                        login(cmdLine);
                        break;
                    case LOGOUT:
                        logout();
                        break;
                    case REGISTER:
                        register(cmdLine);
                        break;
                    case UNREGISTER:
                        unregister(cmdLine);
                        break;
                    case LIST:
                        listfiles();
                        break;
                    case UPLOAD:
                        uploadfile(cmdLine);
                        break;
                    case DOWNLOAD:
                        downloadfile(cmdLine);
                        break;
                    case MODIFY:
                        modifyfile(cmdLine);
                        break;
                    case DELETE:
                        deletefile(cmdLine);
                        break;
                    case HELP:
                        help();
                        break;
                    case NOTIFY:
                        notifyaboutfile(cmdLine);
                        break;
                    case QUIT:
                        quitClient();
                        break;
                    case ILLEGAL_COMMAND:
                        outMgr.println("Illegal command. Here is the list of commands: ");
                        help();
                        break;
                }
            } catch (AlreadyRegisteredException e) {
                outMgr.println("Username already in use. Please pick another username.");
            } catch (UnauthorizedException e) {
                outMgr.println("The user has not been identified please login first.");
            } catch (IOException e) {
                outMgr.println("There was an error reading the file, please try again.");
            } catch (FileException e) {
                outMgr.println("Operation failed");
                outMgr.println((e.getMessage()));
            } catch (PersonException  e) {
                outMgr.println("Operation failed");
                outMgr.println(e.getMessage());
            }
        }
    }

    /**
     * function to login
     * @param cmdLine
     */
    private void login (CmdLine cmdLine) throws RemoteException, PersonException {
        if(cmdLine.getNumberOfParameters() >= 2){
            Credentials c = new Credentials(cmdLine.getParameter(0),cmdLine.getParameter(1));
            long result = server.login(c, outputHandler);
            if(result == -1){
                outMgr.println("Incorrect credentials.");
            }
            else{
                outMgr.println("Successfully logged in.");
                userId= result;
            }
        }
        else{
            outMgr.println("Remember to input username and password.");
        }
    }

    private void logout() throws PersonException, RemoteException{
        server.logout(userId);
        outMgr.println("Person logged out.");
    }

    private void register (CmdLine cmdLine) throws AlreadyRegisteredException {
        try {
            if (cmdLine.getNumberOfParameters() >= 2) {
                server.register(new Credentials(cmdLine.getParameter(0), cmdLine.getParameter(1)));
                outMgr.println("Successfully registered account.");
            } else {
                outMgr.println("Not enough arguments. Provide both username and password.");
            }
        }catch (PersonException | RemoteException e){
            outMgr.println("Operation failed");
            outMgr.println(e.getMessage());
        }
    }

    private void unregister (CmdLine cmdLine){
        try {
            if (cmdLine.getNumberOfParameters() >= 1) {
                server.unregister(cmdLine.getParameter(0));
                outMgr.println("Successfully unregistered account.");
            } else {
                outMgr.println("Not enough arguments. Provide a username to unregister.");
            }
        }catch (PersonException | RemoteException e){
            outMgr.println("Operation failed");
            outMgr.println(e.getMessage());
        }
    }

    private void listfiles () throws RemoteException{
        Collection<File> files = server.listFiles(userId);
        if(files.size() == 0){
            outMgr.println("No files stored in remote directory.");
        }
        for(File file : files){
            outMgr.println("Name: " + file.getName() + ",  Size: " + file.getSize() + ", Owner: "+
                    file.getOwner().getUsername() + ", Type of access: " + file.getAccessPermission());
        }
    }

    private void uploadfile (CmdLine cmdLine) throws IOException , UnauthorizedException, FileException{
        if(cmdLine.getNumberOfParameters() >= 4){
            server.uploadFile(userId,cmdLine.getParameter(0),
                    Integer.parseInt(cmdLine.getParameter(1)),
                    AccessPermission.valueOf(cmdLine.getParameter(2)),
                    ReadWritePermission.valueOf(cmdLine.getParameter(3)));
            outMgr.println("Upload successful.");
        }
        else{
            outMgr.println("Not enough arguments.");
        }
    }

    private void downloadfile (CmdLine cmdLine) throws UnauthorizedException, IOException, PersonException{
            if (cmdLine.getNumberOfParameters() >= 1) {
                String filename = cmdLine.getParameter(0);
                String answer = server.downloadFile(userId, filename);
                outMgr.println("Successfully downloaded file.");
                outMgr.println(answer);
            } else {
                outMgr.println("Not enough arguments. Provide a valid filename to download.");
            }
    }

    private void modifyfile (CmdLine cmdLine) throws RemoteException, UnauthorizedException, PersonException, FileNotFoundException {
        if(cmdLine.getNumberOfParameters() >= 3){
            server.modifyFile(userId,cmdLine.getParameter(0),
                    AccessPermission.valueOf(cmdLine.getParameter(1)),
                    ReadWritePermission.valueOf(cmdLine.getParameter(2)));
            outMgr.println("Modification successful.");
        }
        else{
            outMgr.println("Not enough arguments. ");
        }
    }

    private void deletefile (CmdLine cmdLine) throws IOException, PersonException, UnauthorizedException, FileException{
        if(cmdLine.getNumberOfParameters() >= 1){
            server.deleteFile(userId,cmdLine.getParameter(0));
            outMgr.println("Successfully deleted file.");
        }
        else{
            outMgr.println("Not enough arguments. Provide a filename to delete.");
        }
    }

    private void help (){
        outMgr.println("LOGIN <username> <password> - Login to the system");
        outMgr.println("LOGOUT - Logout of the system");
        outMgr.println("REGISTER <username> <password> - To register to the system");
        outMgr.println("UNREGISTER <username> - Unregister to the system");
        outMgr.println("LIST - List all files accessible to the user");
        outMgr.println("UPLOAD <filename> <size> <PRIVATE or PUBLIC> <WRITE_AND_READ_PERMISSION, WRITE_ONLY_PERMISSION or READ_ONLY_PERMISSION>  - Upload file to the system");
        outMgr.println("DOWNLOAD <filename> - Download file to the client");
        outMgr.println("MODIFY <filename> <PRIVATE or PUBLIC> <WRITE_AND_READ_PERMISSION, WRITE_ONLY_PERMISSION or READ_ONLY_PERMISSION> - Modify file properties");
        outMgr.println("DELETE <filename> - Delete the file");
        outMgr.println("HELP - Lists all commands");
        outMgr.println("NOTIFY <filename> <true or false> - If the user wants to be notified of changes made to file");
        outMgr.println("QUIT - Leave the application");
    }

    private void notifyaboutfile (CmdLine cmdLine) throws RemoteException, PersonException, UnauthorizedException,FileNotFoundException{
        if(cmdLine.getNumberOfParameters() >= 2){
            server.notifyFile(userId,cmdLine.getParameter(0),Boolean.parseBoolean(cmdLine.getParameter(1)));
            outMgr.println("Notify registered for file: " + cmdLine.getParameter(0));
        }
        else{
            outMgr.println("Not enough arguments. Notify <filename> <True/False>.");
        }
    }

    private void quitClient () throws RemoteException {
        receivingCmds = false;
        outputHandler.disconnected();
    }

    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }

    private class OutputHandler extends UnicastRemoteObject implements FileCatalogClient {

        public OutputHandler() throws RemoteException {}

        @Override
        public void handleMessage(String message) throws RemoteException {
            outMgr.println("NOTIFICATION: " + message );
            outMgr.print(PROMPT);
        }

        public void disconnected() throws RemoteException {
            outMgr.println("Filesystem is closing.");
        }
    }

}
