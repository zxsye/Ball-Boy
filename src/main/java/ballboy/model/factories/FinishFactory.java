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
 * Concrete entity factory for finish entities.
 */
public class FinishFactory implements EntityFactory {

    @Override
    public Entity createEntity(
            Level level,
            JSONObject config) {
        try {
            double posX = ((Number) config.get("posX")).doubleValue();
            double posY = ((Number) config.get("posY")).doubleValue();
            Optional<Double> height = Optional.ofNullable(((Number) config.getOrDefault("height", null))).map(
                    Number::doubleValue);

            String imageName = (String) config.getOrDefault("image", "finish.png");

            Vector2D finishPosition = new Vector2D(posX, posY);

            Image image = new Image(imageName);

            AxisAlignedBoundingBox volume = new AxisAlignedBoundingBoxImpl(
                    finishPosition,
                    height.orElse(image.getHeight()),
                    height.map(h -> h * image.getWidth() / image.getHeight()).orElse(image.getWidth())
            );

            return new StaticEntityImpl(
                    volume,
                    Entity.Layer.FOREGROUND,
                    image
            );

        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid static entity configuration | %s | %s", config, e));
        }
    }
}
