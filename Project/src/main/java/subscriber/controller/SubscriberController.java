package subscriber.controller;

import common.CommunicationMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import publisher.net.Publisher;
import subscriber.net.Subscriber;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This controller decouples the view from the network layer.
 */
public class SubscriberController implements Initializable {

    private final Subscriber subscriber = new Subscriber();
    private OutputHandler outputHandler =new OutputHandler();

    @FXML
    private Text messageText;
    @FXML
    private Button quitButton;
    @FXML
    private TextArea receivedMessages;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        quitButton.defaultButtonProperty().bind(quitButton.focusedProperty());

        receivedMessages.setEditable(false);
        receivedMessages.setMouseTransparent(true);
        receivedMessages.setFocusTraversable(false);

        messageText.setText("");
        receivedMessages.setText("");
        //connect(IP_NO ,PORT_NO, new ConsoleOutput());

        sendStart();

    }

    /**
     * When quiting the interface (conection closes)
     * @param ae
     */
    @FXML
    private void quitButtonHandler(ActionEvent ae) {
        try {
            disconnect();
        } catch (JMSException e) {
            e.printStackTrace();
            messageText.setText(e.getMessage());
        }
        shutdown();
    }

    private void shutdown() {
        Platform.exit();
    }

    /**
     * @see Publisher#disconnect() Blocks until disconnection is completed.
     */
    public void disconnect() throws JMSException {
        subscriber.disconnect();
    }

    /**
     * @see Subscriber#startSubscriber( Subscriber subscriber , OutputHandler outputHandler)
     */
    public void sendStart() {
        try {
            subscriber.startSubscriber(subscriber, outputHandler);
        } catch (JMSException e) {
            e.printStackTrace();
            messageText.setText(e.getMessage());
        } catch (NamingException e) {
            e.printStackTrace();
            messageText.setText(e.getMessage());
        }
        System.out.println("Send Start to server Connection");
    }

    public class OutputHandler implements MessageListener {
        public void onMessage(Message message) {
            try {
                ObjectMessage objectMessage =(ObjectMessage) message;
                CommunicationMessage communicationMessage = (CommunicationMessage)objectMessage.getObject();
                receivedMessages.setText(receivedMessages.getText() +
                        "\nSender:" + communicationMessage.getName() + " | Message: " + communicationMessage.getMessage());
            }catch (JMSException e) {
                e.printStackTrace();
                messageText.setText(e.getMessage());
            }
        }
    }

}

