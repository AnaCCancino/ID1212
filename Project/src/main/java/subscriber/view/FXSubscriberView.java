package subscriber.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts The FXApplication view
 */
public class FXSubscriberView extends Application {

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

        System.out.println("Print Class" + getClass().getClassLoader().getResource("main/java/subscriber/resources/FXMLSubscriberView.fxml"));

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main/java/subscriber/resources/FXMLSubscriberView.fxml"));

        Scene scene = new Scene((Parent) loader.load());
        stage.setScene(scene);
        stage.show();

    }


}

