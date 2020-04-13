package Game.maps;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class for loading the base of the map.
 */
public class MapLayer extends ImageView {

    private double layerWidth;
    private double layerHeight;

    MapLayer(String imageUrl) {
        Image layerImage = new Image(imageUrl);
        this.layerWidth = layerImage.getWidth();
        this.layerHeight = layerImage.getHeight();
        this.setImage(layerImage);

    }

    /**
     * Add the layer to the given JavaFx Group.
     *
     * @param root Group that the layer is added to
     */
    void addToGroup(Group root) {
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
