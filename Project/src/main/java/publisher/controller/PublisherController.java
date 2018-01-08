package publisher.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import publisher.net.Publisher;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This controller decouples the view from the network layer.
 */
public class PublisherController implements Initializable {

    private final Publisher publisher = new Publisher();
    private static final String FIRST_MESSAGE ="Please enter your username:";
    private int times =0;
    private String username ="";

    @FXML
    private Text messageText;
    @FXML
    private Button sendButton;
    @FXML
    private Button quitButton;
    @FXML
    private TextField messageField;
    @FXML
    private TextArea sentMessagesArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sendButton.defaultButtonProperty().bind(sendButton.focusedProperty());
        quitButton.defaultButtonProperty().bind(quitButton.focusedProperty());

        sentMessagesArea.setEditable(false);
        sentMessagesArea.setMouseTransparent(true);
        sentMessagesArea.setFocusTraversable(false);

        messageText.setText("");
        messageField.setText("");
        sentMessagesArea.setText("");

        sendStart();

        //connect(IP_NO ,PORT_NO, new ConsoleOutput());
        if (times==0) {
            messageText.setText(FIRST_MESSAGE);
        }
    }

    /**
     * When we want to send a message
     * @param ae
     */
    @FXML
    private void sendButtonHandler(ActionEvent ae) {
        getMessages();
    }

    /**
     * When one Guesses a Word
     * @param ae
     */
    @FXML
    public void onEnter(ActionEvent ae){
        getMessages();
    }

    private void getMessages (){
        System.out.println("The user has entered: "+ sentMessagesArea.getText() +messageField.getText());
        String oldMessages = sentMessagesArea.getText();
        if (times ==0){
            username = messageField.getText();
            times++;
            messageField.setText("");
            messageText.setText("");
        }else {
            sendMessage( messageField.getText() );
            sentMessagesArea.setText(oldMessages +"\n> "+messageField.getText());
            messageField.setText("");
            messageText.setText("");
        }
    }

    /**
     * When quiting the interface (conection closes)
     * @param ae
     */
    @FXML
    private void quitButtonHandler(ActionEvent ae) {
        try {
            publisher.disconnect();
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
     * @see Publisher#startPublisher(Publisher publisher)
     */
    public void sendStart() {

        try {
            publisher.startPublisher(publisher);
        } catch (JMSException e) {
            e.printStackTrace();
            messageText.setText(e.getMessage());
        } catch (NamingException e) {
            e.printStackTrace();
            messageText.setText(e.getMessage());
        }
        System.out.println("Send Start to server Connection");
    }

    /**
     * @see Publisher#sendMessage(String, String)
     */
    public void sendMessage(String message) {

        try {
            publisher.sendMessage(username,message);
        } catch (JMSException e) {
            e.printStackTrace();
            messageText.setText(e.getMessage());
        }

    }


}

