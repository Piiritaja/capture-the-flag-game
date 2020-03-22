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

import networking.ServerClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Menu extends Application {
    private Stage mainStage;
    private Screen screen;
    private int chosenMapIndex;
    private int currentConnections;
    ServerClient client;

    public Menu() {
        this.screen = new Screen();
        this.client = new ServerClient(this);
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


    public Scene setUpPrimaryScene() {
        Button button1 = new Button("Choose game mode");
        Button button2 = new Button("Quit");
        Button button3 = new Button("Full screen mode");


        ImageView imageViewCtf = new ImageView();
        try {
            FileInputStream inputCtfImage = new FileInputStream("src/assets/pngwave.png");
            Image ctfImage = new Image(inputCtfImage);
            imageViewCtf = new ImageView(ctfImage);


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        imageViewCtf.setFitWidth(IMAGE_WIDTH);
        imageViewCtf.setFitHeight(IMAGE_HEIGHT);
        button1.getStyleClass().add("button");
        button2.getStyleClass().add("button");
        button3.getStyleClass().add("Button");
        VBox vbox = new VBox(imageViewCtf, button1, button3, button2);
        vbox.getStyleClass().add("container");


        button1.setOnAction(actionEvent -> {
            GameChooser();
        });
        button2.setOnAction(actionEvent -> exitScreen());
        button3.setOnAction(actionEvent -> toggleFullScreen(mainStage));


        return new Scene(vbox);
    }

    public void setNumberOfCurrentConnections(int connections) {
        this.currentConnections = connections;

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
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;

        Scene scene = setUpPrimaryScene();
        scene.getStylesheets().add("assets/button-style.css");
        mainStage.setScene(scene);
        configurePrimaryStage(mainStage);

        mainStage.show();

    }

    public void GameChooser() {
        // Map picker
        Text t = new Text(10, 50, "Choose a map");
        Group root = new Group();
        Button playButton = new Button("start game");
        playButton.setOnAction(actionEvent -> screen.start(mainStage));

        this.mainStage.getScene().setRoot(root);
        List<ImageView> images = loadMapImages();

        HBox hbox = new HBox();

        t.getStyleClass().add("choose");

        hbox.getStyleClass().add("container");

        setImagePickEffect(images, hbox);

        setImagesToScale(images, hbox);

        // Team picker

        Text teamPickerTitle = new Text(10, 50, "Choose a team");
        teamPickerTitle.getStyleClass().add("choose");

        List<ImageView> teamColors = loadTeamColors();

        HBox teamPickerHbox = new HBox();
        teamPickerHbox.getStyleClass().add("container");

        setTeamPickEffect(teamColors, teamPickerHbox);
        setImagesToScale(teamColors, teamPickerHbox);

        VBox vbox = new VBox(t, hbox, teamPickerTitle, teamPickerHbox, playButton);
        vbox.getStyleClass().add("container");
        root.getChildren().add(vbox);

        mainStage.getScene().setRoot(root);

    }

    private void setTeamPickEffect(List<ImageView> images, HBox hbox) {
        for (ImageView image : images) {
            hbox.getChildren().add(image);
            image.setFitHeight(mainStage.getHeight() / 4);
            image.setFitWidth(mainStage.getWidth() / 4);
            image.setPreserveRatio(true);
            image.setStyle("-fx-opacity: 50%");
            image.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                int colorIndex = images.indexOf(image);
                screen.setPlayerColor(colorIndex);
                for (ImageView image2 : images) {
                    image2.setStyle("-fx-opacity: 50%");
                }
                image.setStyle("-fx-opacity: 100%");
                event.consume();
            });
        }
    }

    private List<ImageView> loadTeamColors() {

        try {
            FileInputStream teamGreenInputStream = new FileInputStream("src/assets/misc/green_team_circle.png");
            Image teamGreenImage = new Image(teamGreenInputStream);
            ImageView teamGreenImageView = new ImageView(teamGreenImage);

            FileInputStream teamRedInputStream = new FileInputStream("src/assets/misc/red_team_circle.png");
            Image teamRedImage = new Image(teamRedInputStream);
            ImageView teamRedImageView = new ImageView(teamRedImage);

            return Arrays.asList(teamGreenImageView, teamRedImageView);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


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


    public List<ImageView> loadMapImages() {

        try {
            FileInputStream map1InputStream = new FileInputStream("src/assets/map/2teams/map1/testmap1.png");
            Image map1Image = new Image(map1InputStream);
            ImageView map1ImageView = new ImageView(map1Image);

            FileInputStream map2InputStream = new FileInputStream("src/assets/map/2teams/map2/map2.png");
            Image map2Image = new Image(map2InputStream);
            ImageView map2ImageView = new ImageView(map2Image);
            return Arrays.asList(map1ImageView, map2ImageView);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    private void toggleFullScreen(Stage stage) {
        stage.setFullScreen(!stage.isFullScreen());
    }

    public void exitScreen() {
        this.mainStage.close();
    }

    public int getChosenMapIndex() {
        return chosenMapIndex;
    }
}
