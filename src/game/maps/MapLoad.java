package game.maps;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MapLoad extends Application {

    private static double height = 720;
    private static double width = 1280;
    static Scene scene;

    @Override
    public void start(Stage stage) {

        StackPane root = new StackPane();
        root.setId("pane");

        scene = new Scene(root, width, height);
        scene.getStylesheets().add("assets/map/2teams/map1.css");

        stage.setTitle("MapLoad");
        //Adding scene to the stage
        stage.setScene(scene);
        stage.setResizable(false);

        //Displaying the contents of the stage
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

    }
    public static void main(String args[]) {
        launch(args);

    }
}