package ballboy.model.factories;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.behaviour.PassiveEntityBehaviourStrategy;
import ballboy.model.entities.collision.BallboyCollisionStrategy;
import ballboy.model.entities.DynamicEntityImpl;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.AxisAlignedBoundingBoxImpl;
import ballboy.model.entities.utilities.KinematicState;
import ballboy.model.entities.utilities.KinematicStateImpl;
import ballboy.model.entities.utilities.Vector2D;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

/*
 * Concrete entity factory for Ballboy entities.
 */
public class BallboyFactory implements EntityFactory {

    @Override
    public Entity createEntity(
            Level level,
            JSONObject config) {
        try {
            double startX = ((Number) config.get("startX")).doubleValue();
            double startY = ((Number) config.get("startY")).doubleValue();

            String imageName = (String) config.getOrDefault("image", "ch_stand1.png");
            String size = (String) config.get("size");

            double height;
            if (size.equals("small")) {
                height = 10.0;
            } else if (size.equals("medium")) {
                height = 25.0;
            } else if (size.equals("large")) {
                height = 50.0;
            } else {
                throw new ConfigurationParseException(String.format("Invalid hero size %s", size));
            }

            Image image = new Image(imageName);
            // preserve image ratio
            double width = height * image.getWidth() / image.getHeight();

            Vector2D startingPosition = new Vector2D(startX, startY);

            KinematicState kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                    .setPosition(startingPosition)
                    .build();

            AxisAlignedBoundingBox volume = new AxisAlignedBoundingBoxImpl(
                    startingPosition,
                    height,
                    width
            );

            return new DynamicEntityImpl(
                    kinematicState,
                    volume,
                    Entity.Layer.FOREGROUND,
                    new Image(imageName),
                    new BallboyCollisionStrategy(level),
                    new PassiveEntityBehaviourStrategy()
            );

        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid ballboy entity configuration | %s | %s", config, e));
        }
    }
}
