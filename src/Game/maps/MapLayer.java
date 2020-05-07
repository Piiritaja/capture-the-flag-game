package Game.maps;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * Class for loading the base of the map.
 */
public class MapLayer extends ImageView {

    private final double layerWidth = 1280;
    private final double layerHeight = 800;

    MapLayer(String imageUrl) {
        System.out.println(imageUrl);
        Image layerImage = new Image(MapLayer.class.getResourceAsStream(imageUrl));
        this.setFitHeight(layerHeight);
        this.setFitWidth(layerWidth);
        this.setImage(layerImage);

    }

    /**
     * Add the layer to the given JavaFx Group.
     *
     * @param root Group that the layer is added to
     */
    void addToGroup(AnchorPane root) {
        root.getChildren().add(this);
    }

    /**
     * @return The original height of the base/background image
     */
    public double getLayerHeight() {
        return this.layerHeight;
    }

    /**
     * @return The original width of the base/background image
     */
    public double getLayerWidth() {
        return layerWidth;
    }


}
