package common;

import java.io.Serializable;

/**
 * A message between a client and the  server.
 */
public class Message implements Serializable {
    private final MsgType type;
    private final String body;

    /**
     * Constructs a new <code>Message</code>, with the specified type and body.
     *
     * @param type The message type.
     * @param body The message body.
     */
    public Message(MsgType type, String body) {
        this.type = type;
        this.body = body;
    }

    /**
     * @return the message body
     */
    public String getBody() {
        return body;
    }

    /**
     * @return the message type
     */
    public MsgType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Message{" + "type=" + type + ", body=" + body + '}';
    }
}
