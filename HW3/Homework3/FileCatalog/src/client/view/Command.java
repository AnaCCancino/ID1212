package client.view;

/**
 * Defines all commands that can be performed by a user of the Client Application.
 */
public enum Command {
    /**
     * Login to the system
     */
    LOGIN,
    /**
     * Logout of the system
     */
    LOGOUT,
    /**
     * To register a client
     */
    REGISTER,
    /**
     * Unregister a client
     */
    UNREGISTER,
    /**
     * List all files accessible to the user
     */
    LIST,
    /**
     * Upload file to the system
     */
    UPLOAD,
    /**
     * Download file to the client
     */
    DOWNLOAD,
    /**
     * Modify file properties
     */
    MODIFY,
    /**
     * Delete the file
     */
    DELETE,
    /**
     * Lists all commands.
     */
    HELP,
    /**
     * If the user wants to be notified of changes made to file.
     */
    NOTIFY,
    /**
     * Leave the application.
     */
    QUIT,
    /**
     * None of the valid commands above was specified.
     */
    ILLEGAL_COMMAND
}
