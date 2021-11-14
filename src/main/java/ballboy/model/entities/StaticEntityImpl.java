package ballboy.model.entities;

import ballboy.model.Entity;
import ballboy.model.Prototype;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.AxisAlignedBoundingBoxImpl;
import ballboy.model.entities.utilities.EntityNotifier;
import ballboy.model.entities.utilities.Vector2D;
import javafx.scene.image.Image;

/**
 * A static entity.
 */
public class StaticEntityImpl extends StaticEntity {
    private final AxisAlignedBoundingBox volume;
    private final Entity.Layer layer;
    private final Image image;

    public StaticEntityImpl(
            AxisAlignedBoundingBox volume,
            Entity.Layer layer,
            Image image
    ) {
        this.volume = volume;
        this.layer = layer;
        this.image = image;
    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public Vector2D getPosition() {
        return new Vector2D(volume.getLeftX(), volume.getTopY());
    }

    @Override
    public double getHeight() {
        return volume.getHeight();
    }

    @Override
    public double getWidth() {
        return volume.getWidth();
    }

    @Override
    public Entity.Layer getLayer() {
        return this.layer;
    }

    @Override
    public AxisAlignedBoundingBox getVolume() {
        return this.volume;
    }

    /**
     * @return Version of this Entity as a EntityNotifier
     */
    @Override
    public EntityNotifier getNotifier() {
        return null;
    }

    @Override
    public StaticEntity copy() {

        return new StaticEntityImpl(
                new AxisAlignedBoundingBoxImpl(
                        new Vector2D(volume.getLeftX(), volume.getTopY()),
                        volume.getHeight(),
                        volume.getWidth()),
                layer,
                new Image(image.getUrl()) // @TODO: not sure if this works
        );
    }
}
