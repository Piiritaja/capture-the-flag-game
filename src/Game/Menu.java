package Game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class Menu extends Application {
    private Stage mainStage;
    private Screen screen;
    private int chosenMapIndex;

    public Menu() {
        this.screen = new Screen();
    }

    // Constants for ctf image
    private static final int IMAGE_WIDTH = 600;
    private static final int IMAGE_HEIGHT = 300;

    // Screen constants
    private static final int MIN_SCREEN_HEIGHT = 600;
    private static final int MIN_SCREEN_WIDTH = 800;


    public static void main(String[] args) {
        launch(args);
    }


    public Scene setUpPrimaryScene() throws FileNotFoundException {
        Button button1 = new Button("Choose game mode");
        Button button2 = new Button("Quit");
        Button button3 = new Button("Full screen mode");

        FileInputStream inputCtfImage = new FileInputStream("src/assets/pngwave.png");
        Image ctfImage = new Image(inputCtfImage);
        ImageView imageViewCtf = new ImageView(ctfImage);
        imageViewCtf.setFitWidth(IMAGE_WIDTH);
        imageViewCtf.setFitHeight(IMAGE_HEIGHT);


        button1.getStyleClass().add("button");
        button2.getStyleClass().add("button");
        button3.getStyleClass().add("Button");

        button1.setOnAction(actionEvent -> {
            try {
                mapPicker();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        button2.setOnAction(actionEvent -> exitScreen(mainStage));
        button3.setOnAction(actionEvent -> toggleFullScreen(mainStage));

        VBox vbox = new VBox(imageViewCtf, button1, button3, button2);
        vbox.getStyleClass().add("container");

        return new Scene(vbox);
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

    public void mapPicker() throws FileNotFoundException {
        Text t = new Text(10, 50, "Choose a map");
        Group root = new Group();
        Button playButton = new Button("start game");
        playButton.setOnAction(actionEvent -> screen.start(mainStage));

        this.mainStage.getScene().setRoot(root);
        List<ImageView> images = loadMapImages();

        HBox hbox = new HBox();
        VBox vbox = new VBox(t, hbox, playButton);

        t.getStyleClass().add("choose");

        vbox.getStyleClass().add("container");
        hbox.getStyleClass().add("container");


        setImagePickEffect(images, hbox);

        root.getChildren().add(vbox);
        mainStage.getScene().setRoot(root);
        setImagesToScale(images, hbox);


    }

    private void setImagePickEffect(List<ImageView> images, HBox hbox) {
        for (ImageView image : images) {
            hbox.getChildren().add(image);
            image.setFitHeight(mainStage.getHeight() / 4);
            image.setFitWidth(mainStage.getWidth() / 4);
            image.setPreserveRatio(true);
            image.setStyle("-fx-opacity: 50%");
            image.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                this.chosenMapIndex = images.indexOf(image);
                screen.setMap(chosenMapIndex);

                for (ImageView image2 : images) {
                    image2.setStyle("-fx-opacity: 50%");
                }
                image.setStyle("-fx-opacity: 100%");
                event.consume();
            });
        }

    }

    private void setImagesToScale(List<ImageView> images, HBox hbox) {
        mainStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            for (ImageView image : images) {
                hbox.getChildren().remove(image);
                hbox.getChildren().add(image);
                image.setFitHeight(mainStage.getHeight() / 4);
                image.setFitWidth(mainStage.getWidth() / 4);
                image.setPreserveRatio(true);
            }
        });

        mainStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            for (ImageView image : images) {
                hbox.getChildren().remove(image);
                hbox.getChildren().add(image);
                image.setFitHeight(mainStage.getHeight() / 4);
                image.setFitWidth(mainStage.getWidth() / 4);
                image.setPreserveRatio(true);
            }
        });

    }


    public List<ImageView> loadMapImages() throws FileNotFoundException {
        FileInputStream map1InputStream = new FileInputStream("src/assets/map/2teams/map1/testmap1.png");
        Image map1Image = new Image(map1InputStream);
        ImageView map1ImageView = new ImageView(map1Image);

        FileInputStream map2InputStream = new FileInputStream("src/assets/map/2teams/map2/map2.png");
        Image map2Image = new Image(map2InputStream);
        ImageView map2ImageView = new ImageView(map2Image);

        return Arrays.asList(map1ImageView, map2ImageView);
    }

    private void toggleFullScreen(Stage stage) {
        stage.setFullScreen(!stage.isFullScreen());
    }

    private void exitScreen(Stage stage) {
        stage.close();
    }

    public int getChosenMapIndex() {
        return chosenMapIndex;
    }
}
