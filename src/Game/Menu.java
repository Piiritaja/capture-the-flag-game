package Game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Menu extends Application {
    Stage mainStage;

    // Constants for ctf image
    private static final  int IMAGE_WIDTH = 600;
    private static final  int IMAGE_HEIGHT = 300;

    // Screen constants
    private static final  int MIN_SCREEN_HEIGHT = 600;
    private static final int MIN_SCREEN_WIDTH = 800;


    public static void main(String[] args) {
        launch(args);
    }


    public Scene setUpPrimaryScene() throws FileNotFoundException {
        Button button1 = new Button("Play game");
        Button button2 = new Button("Quit");
        Button button3 = new Button("Full screen mode");
        button1.getStyleClass().add("button");
        button2.getStyleClass().add("button");
        button3.getStyleClass().add("Button");
        Screen screen  = new Screen();
        button1.setOnAction(actionEvent -> screen.start(mainStage));
        button2.setOnAction(actionEvent -> exitScreen(mainStage));
        button3.setOnAction(actionEvent -> toggleFullScreen(mainStage));
        FileInputStream input = new FileInputStream("src/assets/pngwave.png");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(IMAGE_WIDTH);
        imageView.setFitHeight(IMAGE_HEIGHT);
        VBox vbox = new VBox(imageView, button1, button3, button2);
        vbox.getStyleClass().add("container");
        return new Scene(vbox);
    }

    public void showMenu(){

    }



    private void configurePrimaryStage(Stage stage) {
        stage.setTitle("Capture the flag");

        stage.setX(0);
        stage.setY(0);

        stage.setMinHeight(MIN_SCREEN_HEIGHT);
        stage.setMinWidth(MIN_SCREEN_WIDTH);
        stage.initStyle(StageStyle.DECORATED);
    }


    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        this.mainStage = primaryStage;

        Scene scene = setUpPrimaryScene();
        scene.getStylesheets().add("assets/button-style.css");
        mainStage.setScene(scene);
        configurePrimaryStage(mainStage);

        mainStage.show();


    }

    private void toggleFullScreen(Stage stage) {
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        } else {
            stage.setFullScreen(true);

        }
    }

    private void exitScreen(Stage stage) {
        stage.close();
    }
}
