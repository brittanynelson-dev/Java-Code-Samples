/**
 * Created by Brittany Nelson on 6/12/2017. This is a simple example of a timer designed to display a popup after a set period of time.
 */

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;




public class TimerTest extends Application {

    public void start (Stage primaryStage) throws Exception {

        //Set up a new Group to contain all UI elements
        Group root = new Group();

        //Construct popup window
        Scene popup = new Scene(root, 400, 100);

        Pane p = new Pane();

        //Add the AnchorPane to root
        root.getChildren().addAll(p);

        //Link the main scene to the stage
        primaryStage.setScene(popup);

        //Create a Text object to display an error message
        Text t = new Text("Your session has expired, please copy all unsaved work to retain it.");
        t.setX(20);
        t.setY(50);

        //Create a Timeline object to display an error popup after a pre-defined time in milliseconds
        Timeline timer = new Timeline(new KeyFrame(
                Duration.millis(2500),
                ae -> {
                    //Add the text object to the observable pane
                    p.getChildren().addAll(t);
                    //Display the popup window
                    primaryStage.show();
                }));

        timer.play();

    }


    public static void main(String args[]) {

        launch(args);

    }
}




