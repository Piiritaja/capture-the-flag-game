package game.maps;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class MapLayer {

    ImageView layerImage;

    MapLayer(String imageUrl) {
        this.layerImage = new ImageView(imageUrl);
    }

    void addToPane(StackPane pane) {
        layerImage.fitWidthProperty().bind(pane.widthProperty());
        layerImage.fitHeightProperty().bind(pane.heightProperty());
        pane.getChildren().add(layerImage);
    }
}
