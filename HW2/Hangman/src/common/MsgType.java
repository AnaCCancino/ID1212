package common;

/**
 * Defines all messages that can be sent between client and server
 */
public class MsgType {
    /**
     * To start a new game
     */
    public static final String START="START";
    /**
     * To quit the game
     */
    public static final String QUIT="QUIT";
    /**
     * To guess a letter
     */
    public static final String GUESS="GUESS";
    /**
     *To return User Info
     */
    public static final String INFO="INFO";
}
