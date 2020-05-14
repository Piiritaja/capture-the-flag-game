package Game;

import Game.maps.Battlefield;
import Game.maps.MapLayer;
import com.esotericsoftware.kryonet.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.stage.WindowEvent;
import networking.ServerClient;
import networking.packets.Packet002RequestConnections;
import networking.packets.Packet020CreateGame;
import networking.packets.Packet022JoinGame;
import networking.packets.Packet023RequestGame;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu extends Application {
    private Stage mainStage;
    private Screen screen;
    private int chosenMapIndex;
    private int currentConnections;
    private ServerClient serverClient;
    private Client client;

    private Map<String, Battlefield> availableMaps;

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

    // Map images
    private static final String MAP1_IMAGE_SRC = "/map/2teams/map1/testmap1.png";
    private static final String MAP2_IMAGE_SRC = "/map/2teams/map2/map2.png";

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
        this.availableMaps = new HashMap<>();

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
        Button button1 = new Button("Create a game");
        Button button4 = new Button("Join a game");
        Button button2 = new Button("Quit");
        Button button3 = new Button("Full screen mode");

        ImageView imageViewCtf = new ImageView();
        Image ctfImage = new Image(Menu.class.getResourceAsStream("/pngwave.png"));
        imageViewCtf.setImage(ctfImage);

        imageViewCtf.setFitWidth(IMAGE_WIDTH);
        imageViewCtf.setFitHeight(IMAGE_HEIGHT);
        button1.getStyleClass().add("button");
        button2.getStyleClass().add("button");
        button3.getStyleClass().add("Button");
        button4.getStyleClass().add("Button");
        VBox vbox = new VBox(imageViewCtf, button1, button4, button3, button2, this.usersOnlineText);
        vbox.getStyleClass().add(CONTAINER_CLASS);

        button1.setOnAction(actionEvent -> gameChooser());
        button2.setOnAction(actionEvent -> exitScreen());
        button3.setOnAction(actionEvent -> toggleFullScreen());
        button4.setOnAction(actionEvent -> joinGameScreen());

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
        playButton.setOnAction(actionEvent -> startScreen());

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

        HBox teamPickerHBox = new HBox();
        teamPickerHBox.getStyleClass().add(CONTAINER_CLASS);

        setTeamPickEffect(teamColors, teamPickerHBox);
        setImagesToScale(teamColors, teamPickerHBox);
        setImagesToScale(playerNumbers, playerNumbersHBox);

        VBox vbox = new VBox(t, hbox, teamPickerTitle, teamPickerHBox, playerCount, playerNumbersHBox, playButton);
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
        startScreen();
    }

    public void prepGame(String id) {
        if (screen.color == null) {
            return;
        }
        this.screen.setGameId(id);
        Packet022JoinGame joinGame = new Packet022JoinGame();
        joinGame.gameId = this.screen.getGameId();
        client.sendTCP(joinGame);
    }


    public void joinGameScreen() {
        TextField textField = new TextField();
        textField.setPromptText("Enter game id");
        Button searchButton = new Button();
        Label label = new Label();
        label.setText("Enter game id");
        searchButton.setText("Search");
        GridPane grid = new GridPane();
        VBox vBox = new VBox(label, textField, searchButton);
        vBox.getStyleClass().add(CONTAINER_CLASS);
        grid.setAlignment(Pos.CENTER);
        grid.getChildren().add(vBox);
        mainStage.getScene().setRoot(grid);
        searchButton.setOnAction(actionEvent -> searchGameWithId(textField.getText()));

    }

    public void searchGameWithId(String id) {
        Packet023RequestGame requestGame = new Packet023RequestGame();
        requestGame.gameId = id;
        client.sendTCP(requestGame);

    }

    public void displayGame(String gameId, int mapIndex, int playerCount) {
        ImageView mapImage = getMapImage(mapIndex);
        scaleImage(mapImage);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        VBox vBox = new VBox();
        Label gameIdLabel = new Label("Game id: " + gameId);
        Label playerCountLabel = new Label(playerCount + " player game");
        Label pickTeaLabel = new Label("Pick a team");
        List<ImageView> teamColors = loadTeamColors();
        HBox teamColorsHBox = new HBox();
        setTeamPickEffect(teamColors, teamColorsHBox);
        teamColorsHBox.getStyleClass().add(CONTAINER_CLASS);
        Button joinGameButton = new Button("Join game");
        vBox.getStyleClass().add(CONTAINER_CLASS);
        vBox.getChildren().add(mapImage);
        vBox.getChildren().add(gameIdLabel);
        vBox.getChildren().add(playerCountLabel);
        vBox.getChildren().add(pickTeaLabel);
        vBox.getChildren().add(teamColorsHBox);
        vBox.getChildren().add(joinGameButton);
        grid.getChildren().add(vBox);
        mainStage.getScene().setRoot(grid);
        joinGameButton.setOnAction(actionEvent -> prepGame(gameId));

    }

    public ImageView getMapImage(int mapIndex) {
        String location = mapIndex == 0 ? MAP1_IMAGE_SRC : MAP2_IMAGE_SRC;
        try {
            InputStream inputStream = Menu.class.getResourceAsStream(location);
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);
            return imageView;
        } catch (Exception e) {
            e.printStackTrace();
            return new ImageView();
        }
    }

    public Map<ImageView, String> loadMaps() {
        Map<ImageView, String> images = new HashMap<>();
        for (Map.Entry<String, Battlefield> entry : availableMaps.entrySet()) {
            String key = entry.getKey();
            Battlefield battlefield = entry.getValue();
            Label label = new Label();
            label.setText(key);
            String imageLocation;
            try {
                if (battlefield.equals(Battlefield.MAP1)) {
                    imageLocation = MAP1_IMAGE_SRC;
                } else {
                    imageLocation = MAP2_IMAGE_SRC;
                }
                FileInputStream fileInputStream = new FileInputStream(imageLocation);
                Image image = new Image(fileInputStream);
                ImageView imageView = new ImageView(image);
                images.put(imageView, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return images;
    }


    /**
     * Method to go from the Choose map screen to Game screen.
     */
    public void startScreen() {
        if (screen.color == null) {
            return;
        }
        Packet020CreateGame createGame = new Packet020CreateGame();
        createGame.gameId = screen.getGameId();
        createGame.battlefield = screen.getChosenMap();
        createGame.playerCount = screen.getPlayerCount();
        client.sendTCP(createGame);
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
        Image teamGreenImage = new Image(Menu.class.getResourceAsStream("/misc/green_team_circle.png"));
        ImageView teamGreenImageView = new ImageView(teamGreenImage);

        Image teamRedImage = new Image(Menu.class.getResourceAsStream("/misc/red_team_circle.png"));
        ImageView teamRedImageView = new ImageView(teamRedImage);

        return Arrays.asList(teamGreenImageView, teamRedImageView);


    }

    /**
     * @param images Map images.
     * @param hbox   Container where images should belong to.
     */
    private void setImagePickEffect(List<ImageView> images, HBox hbox) {
        for (ImageView image : images) {
            hbox.getChildren().add(image);
            image.setFitHeight(mainStage.getHeight() / 3.5);
            image.setFitWidth(mainStage.getWidth() / 3.5);
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
            scaleImage(image);
        }
    }

    public void scaleImage(ImageView image) {
        image.setFitHeight(mainStage.getHeight() / 4);
        image.setFitWidth(mainStage.getWidth() / 4);
        image.setPreserveRatio(true);
    }

    /**
     * Loads map images from the assets folder.
     *
     * @return map images as ImaView items.
     */
    public List<ImageView> loadMapImages() {

        Image map1Image = new Image(Menu.class.getResourceAsStream(MAP1_IMAGE_SRC));
        ImageView map1ImageView = new ImageView(map1Image);

        Image map2Image = new Image(Menu.class.getResourceAsStream(MAP2_IMAGE_SRC));
        ImageView map2ImageView = new ImageView(map2Image);
        return Arrays.asList(map1ImageView, map2ImageView);


    }

    public List<ImageView> loadPlayerNumbers() {
        Image map1Image = new Image(Menu.class.getResourceAsStream("/misc/1Player.png"));
        ImageView map1ImageView = new ImageView(map1Image);
        Image map2Image = new Image(Menu.class.getResourceAsStream("/misc/2players.png"));
        ImageView map2ImageView = new ImageView(map2Image);

        Image map3Image = new Image(Menu.class.getResourceAsStream("/misc/3players.png"));
        ImageView map3ImageView = new ImageView(map3Image);

        Image map4Image = new Image(Menu.class.getResourceAsStream("/misc/4players.png"));
        ImageView map4ImageView = new ImageView(map4Image);
        return Arrays.asList(map1ImageView, map2ImageView, map3ImageView, map4ImageView);
    }

    public void setAvailableMaps(Map<String, Battlefield> counts) {
        this.availableMaps = counts;
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
        System.exit(0);
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
        scene.getStylesheets().add(Menu.class.getResource("/buttonStyle.css").toExternalForm());
        mainStage.setScene(scene);
        configurePrimaryStage();

        mainStage.show();
        Packet002RequestConnections requestConnections = new Packet002RequestConnections();
        client.sendTCP(requestConnections);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


    }


    public static void main(String[] args) {
        launch(args);
    }


}
