package ballboy.model.factories;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.StaticEntityImpl;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.AxisAlignedBoundingBoxImpl;
import ballboy.model.entities.utilities.Vector2D;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.util.Optional;

/*
 * Concrete entity factory for StaticEntityImpl objects.
 */
public class StaticEntityFactory implements EntityFactory {
    private final Entity.Layer layer;

    public StaticEntityFactory(Entity.Layer layer) {
        this.layer = layer;
    }

    @Override
    public Entity createEntity(
            Level level,
            JSONObject config) {
        try {
            double startX = ((Number) config.get("posX")).doubleValue();
            double startY = ((Number) config.get("posY")).doubleValue();
            Optional<Double> height = Optional.ofNullable(((Number) config.get("height"))).map(Number::doubleValue);

            String imageName = (String) config.getOrDefault("image", "blank.png");

            Vector2D startingPosition = new Vector2D(startX, startY);

            Image image = new Image(imageName);

            AxisAlignedBoundingBox volume = new AxisAlignedBoundingBoxImpl(
                    startingPosition,
                    height.orElse(image.getHeight()),
                    height.map(h -> h * image.getWidth() / image.getHeight()).orElse(image.getWidth())
            );

            return new StaticEntityImpl(
                    volume,
                    layer,
                    image
            );

        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid static entity configuration | %s | %s", config, e));
        }
    }
}
