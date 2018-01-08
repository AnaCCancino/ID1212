package publisher.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts The FXApplication view
 */
public class FXPublisherView extends Application {

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

        System.out.println("HOLA");
        //URL url = new File("/publisher/resources/FXMLPublisherView.fxml").toURL();
        /*System.out.println("Print Class: " +
                getClass().getClassLoader().getResource("C:/Users/Gigi/Documents/Database/Project/src/main/java/publisher/resources/FXMLPublisherView.fxml") +
                getClass().getResource("C:/Users/Gigi/Documents/Database/Project/src/main/java/publisher/resources/FXMLPublisherView.fxml") +
                        getClass().getClassLoader().getResource("main/java/publisher/resources/FXMLPublisherView.fxml") +
                        getClass().getResource("/publisher/resources/FXMLPublisherView.fxml")
                );
        */


        //URL url = Paths.get("/publisher/resources/FXMLPublisherView.fxml").toUri().toURL();  main/java/publisher/resources/FXMLPublisherView.fxml
        //Parent root = FXMLLoader.load(url);
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main/java/publisher/resources/FXMLPublisherView.fxml"));

        Scene scene = new Scene((Parent) loader.load());
        stage.setScene(scene);
        stage.show();

    }

}

