package common;

/**
 * Defines all messages that can be sent between client and server
 */
public enum MsgType {
    /**
     * To start a new game
     */
    START,
    /**
     * To quit the game
     */
    QUIT,
    /**
     * To guess a letter
     */
    GUESS,
    /**
     *To return User Info
     */
    INFO
}
