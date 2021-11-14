package ballboy.view;

import ballboy.model.Entity;
import javafx.scene.Node;

public interface EntityView {
    void update(
            double xViewportOffset,
            double yViewportOffset);

    boolean matchesEntity(Entity entity);

    void markForDelete();

    Node getNode();

    boolean isMarkedForDelete();
}
