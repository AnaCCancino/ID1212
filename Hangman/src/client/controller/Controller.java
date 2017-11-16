package client.controller;

import client.net.OutputHandler;
import client.net.ServerConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * This controller decouples the view from the network layer. All methods, except
 * <code>disconnect</code>, submit their task to the common thread pool, provided by
 * <code>ForkJoinPool.commonPool</code>, and then return immediately.
 */
public class Controller implements Initializable {

    private final ServerConnection serverConnection = new ServerConnection();

    static final String IP_NO = "localhost";
    static final int PORT_NO = 4444;

    private static final String SCORE_LABEL_PREFIX = "Score: ";
    private static final String GUESSES_LEFT_LABEL_PREFIX = "Number of guesses left: ";


    @FXML
    private Text scoreLabel;
    @FXML
    private Text guessesLeftLabel;
    @FXML
    private Text messageText;
    @FXML
    private Text wordText;
    @FXML
    private Button startNewGameButton;
    @FXML
    private Button quitButton;
    @FXML
    private TextField guessField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startNewGameButton.defaultButtonProperty().bind(startNewGameButton.focusedProperty());
        quitButton.defaultButtonProperty().bind(quitButton.focusedProperty());

        messageText.setText("");
        guessesLeftLabel.setText("");
        scoreLabel.setText("");
        wordText.setText("");
        guessField.setVisible(false);
        connect(IP_NO ,PORT_NO, new ConsoleOutput());
    }

    @FXML
    private void startNewGameButtonHandler(ActionEvent ae) {
        System.out.println(wordText.getText());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        shutdown();
    }

    private void shutdown() {
        Platform.exit();
    }

    /**
     * When one Guesses a Word
     * @param ae
     */
    @FXML
    public void onEnter(ActionEvent ae){

        System.out.println("User entered Guess!");
        sendGuess(guessField.getText());
        guessField.setText("");
    }

    /**
     * @see ServerConnection#connect(java.lang.String, int,
     * client.net.OutputHandler)
     */
    public void connect(String host, int port, OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, outputHandler);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.handleMsg("Connected to " + host + ":" + port));
    }

    /**
     * @see ServerConnection#disconnect() Blocks until disconnection is completed.
     */
    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }

    /**
     * @see ServerConnection#sendStart() (java.lang.String)
     */
    public void sendStart() {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendStart();
                System.out.println("Send Start to server Connection");
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    /**
     * @see ServerConnection#sendGuess(String) (java.lang.String)
     */
    public void sendGuess(String guess) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendGuess(guess);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    private class ConsoleOutput implements OutputHandler {
        @Override
        public void handleMsg(String msg) {

            System.out.println((String) msg);
            String[] data = msg.split("##");
            messageText.setText(data[0]);
            //System.out.println("LA DATA ES: " +data[1]);
            guessesLeftLabel.setText(GUESSES_LEFT_LABEL_PREFIX + data[1]);
            scoreLabel.setText(SCORE_LABEL_PREFIX + data[2]);
            wordText.setText(data[3]);
            guessField.setVisible(true);

            if(data[0].contains("Congratulations") || data[0].contains("over")){
                guessesLeftLabel.setText("");
                guessField.setVisible(false);
                messageText.setText(data[0]);
            }

        }
    }


}

