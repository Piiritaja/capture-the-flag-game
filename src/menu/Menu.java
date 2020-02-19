package menu;

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

    public static void main(String[] args) {
        launch(args);
    }


    private Scene setUpPrimaryScene() throws FileNotFoundException {
        Button button1 = new Button("Play game");
        Button button2 = new Button("Quit");
        Button button3 = new Button("Full screen mode");
        button1.getStyleClass().add("button");
        button2.getStyleClass().add("button");
        button3.getStyleClass().add("Button");
        button2.setOnAction(actionEvent -> exitScreen(mainStage));
        button3.setOnAction(actionEvent -> setFullScreen(mainStage));
        FileInputStream input = new FileInputStream("src/assets/pngwave.png");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(600);
        imageView.setFitHeight(300);
        VBox vbox = new VBox(imageView,button1,button3,button2);
        vbox.getStyleClass().add("container");
        Scene scene = new Scene(vbox);
        return scene;
    }



    private void configurePrimaryStage(Stage stage){
        stage.setTitle("Capture the flag");

        stage.setX(0);
        stage.setY(0);

        stage.setMinHeight(600);
        stage.setMinWidth(800);
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

    private void setFullScreen(Stage stage){
        stage.setFullScreen(true);

    }

    private void exitScreen(Stage stage){
        stage.close();
    }
}
