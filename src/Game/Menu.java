package Game;

import com.esotericsoftware.kryonet.Client;
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
import networking.packets.Packet006RequestBotsLocation;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Menu extends Application {
    private Stage mainStage;
    private Screen screen;
    private int chosenMapIndex;
    private int currentConnections;
    private ServerClient serverClient;
    private Client client;

    // Constants for ctf image
    private static final int IMAGE_WIDTH = 600;
    private static final int IMAGE_HEIGHT = 300;

    // Screen constants
    private static final int MIN_SCREEN_HEIGHT = 600;
    private static final int MIN_SCREEN_WIDTH = 800;

    // Css class for container
    private static final String CONTAINER_CLASS = "container";
    private static final String NOT_CHOSEN_OPACITY = "-fx-opacity: 50%";
    private static final String CHOSEN_OPACITY = "-fx-opacity: 100%";

    private Text usersOnlineText = new Text(10, 50, "Offline mode");

    /**
     * Constructor for Menu class.
     * Create new server client and client from the server client.
     * Create new screen and assign it to this object.
     */
    public Menu() {
        this.serverClient = new ServerClient(this);
        this.client = this.serverClient.getClient();
        this.screen = new Screen(this.serverClient);

    }


    /**
     * Constructor for Menu class.
     * Usually called from the Screen class, when connection with the server has already been established.
     * Create new screen and assign it to this object.
     * Assign the given client to this object.
     *
     * @param serverClient server client to assign to this class object.
     */
    public Menu(ServerClient serverClient) {
        this.serverClient = serverClient;
        this.client = serverClient.getClient();
        this.screen = new Screen(this.serverClient);
        this.serverClient.setMenu(this);
    }

    /**
     * @return screen assigned to the Menu object.
     */
    public Screen getScreen() {
        return this.screen;
    }

    /**
     * Creates, styles, and adds elements.
     * Result is the menu screen.
     *
     * @return scene that was created in the method.
     */
    public Scene setUpPrimaryScene() {
        Button button1 = new Button("Choose game mode");
        Button button2 = new Button("Quit");
        Button button3 = new Button("Full screen mode");

        ImageView imageViewCtf = new ImageView();
        try {
            FileInputStream inputCtfImage = new FileInputStream("src/assets/pngwave.png");
            Image ctfImage = new Image(inputCtfImage);
            imageViewCtf.setImage(ctfImage);


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        imageViewCtf.setFitWidth(IMAGE_WIDTH);
        imageViewCtf.setFitHeight(IMAGE_HEIGHT);
        button1.getStyleClass().add("button");
        button2.getStyleClass().add("button");
        button3.getStyleClass().add("Button");
        VBox vbox = new VBox(imageViewCtf, button1, button3, button2, this.usersOnlineText);
        vbox.getStyleClass().add(CONTAINER_CLASS);

        button1.setOnAction(actionEvent -> gameChooser());
        button2.setOnAction(actionEvent -> exitScreen());
        button3.setOnAction(actionEvent -> toggleFullScreen());

        return new Scene(vbox);
    }

    /**
     * Updates users online text field.
     */
    public void changeNumberOfConnectionsText() {
        if (this.currentConnections == 1) {
            this.usersOnlineText.setText("No users online");
        } else if (this.currentConnections != 0) {
            this.usersOnlineText.setText("Users online: " + (this.currentConnections - 1));
        }
    }

    /**
     * Saves the amount of online users.
     *
     * @param connections the value to assign to currentConnections.
     */
    public void setNumberOfCurrentConnections(int connections) {
        this.currentConnections = connections;
        changeNumberOfConnectionsText();

    }


    /**
     * Sets initial main stage height and width.
     * Sets screen title.
     */
    private void configurePrimaryStage() {
        mainStage.setTitle("Capture the flag");

        mainStage.setX(0);
        mainStage.setY(0);

        mainStage.setMinHeight(MIN_SCREEN_HEIGHT);
        mainStage.setMinWidth(MIN_SCREEN_WIDTH);
        mainStage.initStyle(StageStyle.DECORATED);
    }


    /**
     * Screen for users to pick a map and a team.
     */
    public void gameChooser() {

        // Map picker
        Text t = new Text(10, 50, "Choose a map");
        Group root = new Group();
        Button playButton = new Button("start game");
        Button joinButton = new Button("Join a game");
        playButton.setOnAction(actionEvent -> startScreen());
        joinButton.setOnAction(actionEvent -> joinGame());

        this.mainStage.getScene().setRoot(root);
        List<ImageView> images = loadMapImages();

        HBox hbox = new HBox();

        t.getStyleClass().add("choose");

        hbox.getStyleClass().add(CONTAINER_CLASS);

        setImagePickEffect(images, hbox);

        setImagesToScale(images, hbox);

        // Team picker

        Text teamPickerTitle = new Text(10, 50, "Choose a team");
        teamPickerTitle.getStyleClass().add("choose");

        List<ImageView> teamColors = loadTeamColors();

        Text playerCount = new Text(10, 50, "Choose number of online players");
        playerCount.getStyleClass().add("choose");

        List<ImageView> playerNumbers = loadPlayerNumbers();

        HBox playerNumbersHBox = new HBox();

        setPlayerNumberEffect(playerNumbers, playerNumbersHBox);

        HBox teamPickerHbox = new HBox();
        teamPickerHbox.getStyleClass().add(CONTAINER_CLASS);

        setTeamPickEffect(teamColors, teamPickerHbox);
        setImagesToScale(teamColors, teamPickerHbox);
        setImagesToScale(playerNumbers, playerNumbersHBox);

        VBox vbox = new VBox(t, hbox, teamPickerTitle, teamPickerHbox, playerCount, playerNumbersHBox, playButton, joinButton);
        vbox.getStyleClass().add(CONTAINER_CLASS);
        root.getChildren().add(vbox);

        mainStage.getScene().setRoot(root);

    }

    /**
     * Requests bots from other clients already in that map.
     */
    public void joinGame() {
        if (getScreen().getPlayerCount() <= 1) {
            startScreen();
            return;
        }
        Packet006RequestBotsLocation requestBotsLocation = new Packet006RequestBotsLocation();
        requestBotsLocation.battlefield = this.screen.getChosenMap();
        client.sendTCP(requestBotsLocation);
        startScreen();
    }


    /**
     * Method to go from the Choose map screen to Game screen.
     */
    public void startScreen() {
        screen.start(mainStage);
    }

    /**
     * @param images Team colors as images.
     * @param hbox   Container where images should belong to.
     */
    private void setTeamPickEffect(List<ImageView> images, HBox hbox) {
        for (ImageView image : images) {
            hbox.getChildren().add(image);
            image.setFitHeight(mainStage.getHeight() / 4);
            image.setFitWidth(mainStage.getWidth() / 4);
            image.setPreserveRatio(true);
            image.setStyle(NOT_CHOSEN_OPACITY);
            image.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                int colorIndex = images.indexOf(image);
                screen.setPlayerColor(colorIndex);
                for (ImageView image2 : images) {
                    image2.setStyle(NOT_CHOSEN_OPACITY);
                }
                image.setStyle(CHOSEN_OPACITY);
                event.consume();
            });
        }
    }

    private void setPlayerNumberEffect(List<ImageView> images, HBox hbox) {
        for (ImageView image : images) {
            hbox.getChildren().add(image);
            image.setFitHeight(mainStage.getHeight() / 4);
            image.setFitWidth(mainStage.getWidth() / 4);
            image.setPreserveRatio(true);
            image.setStyle(NOT_CHOSEN_OPACITY);
            image.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                int playerCount = images.indexOf(image) + 1;
                screen.setPlayerCount(playerCount);
                for (ImageView image2 : images) {
                    image2.setStyle(NOT_CHOSEN_OPACITY);
                }
                image.setStyle(CHOSEN_OPACITY);
                event.consume();
            });
        }
    }

    /**
     * Loads team colors from the assets folder.
     *
     * @return Team colors as ImageView items.
     */
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
            return new ArrayList<>();
        }


    }

    /**
     * @param images Map images.
     * @param hbox   Container where images should belong to.
     */
    private void setImagePickEffect(List<ImageView> images, HBox hbox) {
        for (ImageView image : images) {
            hbox.getChildren().add(image);
            image.setFitHeight(mainStage.getHeight() / 4);
            image.setFitWidth(mainStage.getWidth() / 4);
            image.setPreserveRatio(true);
            image.setStyle(NOT_CHOSEN_OPACITY);
            image.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                this.chosenMapIndex = images.indexOf(image);
                screen.setMap(chosenMapIndex);

                for (ImageView image2 : images) {
                    image2.setStyle(NOT_CHOSEN_OPACITY);
                }
                image.setStyle(CHOSEN_OPACITY);
                event.consume();
            });
        }

    }

    /**
     * @param images map and teamColor images.
     * @param hbox   container where the images should belong to.
     */
    private void setImagesToScale(List<ImageView> images, HBox hbox) {
        mainStage.widthProperty().addListener((obs, oldVal, newVal) -> scaleListener(images, hbox));

        mainStage.heightProperty().addListener((obs, oldVal, newVal) -> scaleListener(images, hbox));
    }


    /**
     * Method used in listeners for changing images to scale with screen.
     *
     * @param images Map or tea, color images.
     * @param hbox   hbox that should contain the images.
     */
    public void scaleListener(List<ImageView> images, HBox hbox) {
        for (ImageView image : images) {
            hbox.getChildren().remove(image);
            hbox.getChildren().add(image);
            image.setFitHeight(mainStage.getHeight() / 4);
            image.setFitWidth(mainStage.getWidth() / 4);
            image.setPreserveRatio(true);
        }
    }

    /**
     * Loads map images from the assets folder.
     *
     * @return map images as ImaView items.
     */
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
            return new ArrayList<>();
        }


    }

    public List<ImageView> loadPlayerNumbers() {
        try {
            FileInputStream map1InputStream = new FileInputStream("src/assets/misc/1Player.png");
            Image map1Image = new Image(map1InputStream);
            ImageView map1ImageView = new ImageView(map1Image);

            FileInputStream map2InputStream = new FileInputStream("src/assets/misc/2players.png");
            Image map2Image = new Image(map2InputStream);
            ImageView map2ImageView = new ImageView(map2Image);

            FileInputStream map3InputStream = new FileInputStream("src/assets/misc/3players.png");
            Image map3Image = new Image(map3InputStream);
            ImageView map3ImageView = new ImageView(map3Image);

            FileInputStream map4InputStream = new FileInputStream("src/assets/misc/4players.png");
            Image map4Image = new Image(map4InputStream);
            ImageView map4ImageView = new ImageView(map4Image);
            return Arrays.asList(map1ImageView, map2ImageView, map3ImageView, map4ImageView);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Toggles screen between sized and full screen.
     */
    private void toggleFullScreen() {
        mainStage.setFullScreen(!mainStage.isFullScreen());
    }

    /**
     * Closes the stage.
     */
    public void exitScreen() {
        this.mainStage.close();
    }


    /**
     * Run when the class is called.
     * Acts as main method for javaFx applications.
     *
     * @param primaryStage default stage that gets passed to the start method
     */
    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;

        Scene scene = setUpPrimaryScene();
        scene.getStylesheets().add("assets/button-style.css");
        mainStage.setScene(scene);
        configurePrimaryStage();

        mainStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


}
