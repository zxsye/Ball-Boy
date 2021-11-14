package ballboy.model.factories;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.behaviour.FloatingCloudBehaviourStrategy;
import ballboy.model.entities.collision.PassiveCollisionStrategy;
import ballboy.model.entities.DynamicEntityImpl;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.AxisAlignedBoundingBoxImpl;
import ballboy.model.entities.utilities.KinematicState;
import ballboy.model.entities.utilities.KinematicStateImpl;
import ballboy.model.entities.utilities.Vector2D;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

/*
 * Concrete entity factory for cloud entities.
 */
public class CloudFactory implements EntityFactory {

    @Override
    public Entity createEntity(
            Level level,
            JSONObject config) {
        try {
            double startX = ((Number) config.get("startX")).doubleValue();
            double startY = ((Number) config.get("startY")).doubleValue();
            double xVelocity = ((Number) config.get("horizontalVelocity")).doubleValue();
            String imageName = (String) config.getOrDefault("image", "cloud_1.png");

            Vector2D startingPosition = new Vector2D(startX, startY);

            KinematicState kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                    .setPosition(startingPosition)
                    .setHorizontalVelocity(xVelocity)
                    .build();

            Image image = new Image(imageName);

            AxisAlignedBoundingBox volume = new AxisAlignedBoundingBoxImpl(
                    startingPosition,
                    image.getHeight(),
                    image.getWidth()
            );

            return new DynamicEntityImpl(
                    kinematicState,
                    volume,
                    Entity.Layer.BACKGROUND,
                    new Image(imageName),
                    new PassiveCollisionStrategy(),
                    new FloatingCloudBehaviourStrategy()
            );

        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid cloud entity configuration | %s | %s", config, e));
        }
    }
}
