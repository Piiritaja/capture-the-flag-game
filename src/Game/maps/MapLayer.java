package Game.maps;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MapLayer extends ImageView {

    private double layerWidth;
    private double layerHeight;

    MapLayer(String imageUrl) {
        Image layerImage = new Image(imageUrl);
        this.layerWidth = layerImage.getWidth();
        this.layerHeight = layerImage.getHeight();
        this.setImage(layerImage);

    }

    void addToGroup(Group root) {
        root.getChildren().add(this);
    }

    public double getLayerHeight() {
        return this.layerHeight;
    }

    public double getLayerWidth() {
        return layerWidth;
    }


}
