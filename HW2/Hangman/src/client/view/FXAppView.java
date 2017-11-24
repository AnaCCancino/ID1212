package client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Starts The FXApplication view
 */
public class FXAppView extends Application {

    /**
     * The application takes the arguments, if you have.
     * @param args
     */
    public static void main(String[] args) {
       launch(args);
    }

    /**
     * Show the window
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        System.out.println("Print Class" + getClass().getResource("/client/resources/FXMLHangmanView.fxml"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/resources/FXMLHangmanView.fxml"));

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();

    }


}

