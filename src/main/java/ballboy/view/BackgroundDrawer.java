package ballboy.view;

import ballboy.model.GameEngine;
import javafx.scene.layout.Pane;

public interface BackgroundDrawer {
    void draw(
            GameEngine model,
            Pane pane);

    void update(
            double xViewportOffset,
            double yViewportOffset);
}
