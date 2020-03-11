package Game.maps;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class MapLayer extends ImageView {

    MapLayer(String imageUrl) {
        super(imageUrl);

    }

    void addToGroup(Group root) {
        root.getChildren().add(this);
    }
}
