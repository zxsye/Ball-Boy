package ballboy.model.factories;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.behaviour.AggressiveEnemyBehaviourStrategy;
import ballboy.model.entities.behaviour.BehaviourStrategy;
import ballboy.model.entities.behaviour.PassiveEntityBehaviourStrategy;
import ballboy.model.entities.behaviour.ScaredEnemyBehaviourStrategy;
import ballboy.model.entities.collision.CollisionStrategy;
import ballboy.model.entities.collision.EnemyCollisionStrategy;
import ballboy.model.entities.DynamicEntityImpl;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.AxisAlignedBoundingBoxImpl;
import ballboy.model.entities.utilities.KinematicState;
import ballboy.model.entities.utilities.KinematicStateImpl;
import ballboy.model.entities.utilities.Vector2D;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.util.Optional;

public class EnemyFactory implements EntityFactory {

    @Override
    public Entity createEntity(
            Level level,
            JSONObject config) {
        try {
            double startX = ((Number) config.get("startX")).doubleValue();
            double startY = ((Number) config.get("startY")).doubleValue();
            double startVelocityX = ((Number) config.get("startVelocityX")).doubleValue();
            String behaviour = (String) config.get("behaviour");

            Optional<Double> height = Optional.ofNullable(((Number) config.get("height"))).map(Number::doubleValue);

            String imageName = (String) config.getOrDefault("image", "slimePa.png");

            Vector2D startingPosition = new Vector2D(startX, startY);

            KinematicState kinematicState = new KinematicStateImpl.KinematicStateBuilder()
                    .setPosition(startingPosition)
                    .setHorizontalVelocity(startVelocityX)
                    .build();

            Image image = new Image(imageName);

            AxisAlignedBoundingBox volume = new AxisAlignedBoundingBoxImpl(
                    startingPosition,
                    height.orElse(image.getHeight()),
                    height.map(h -> h * image.getWidth() / image.getHeight()).orElse(image.getWidth())
            );

            String colour = null;
            if (imageName.contains("slimeR")) {
                colour = "RED";
            } else if (imageName.contains("slimeG")) {
                colour = "GREEN";
            } else if (imageName.contains("slimeB")) {
                colour = "BLUE";
            }

            CollisionStrategy collisionStrategy = new EnemyCollisionStrategy(level, colour);

            BehaviourStrategy behaviourStrategy;
            switch (behaviour) {
                case "scared":
                    behaviourStrategy = new ScaredEnemyBehaviourStrategy(level);
                    break;
                case "passive":
                    behaviourStrategy = new PassiveEntityBehaviourStrategy();
                    break;
                case "aggressive":
                    behaviourStrategy = new AggressiveEnemyBehaviourStrategy(level);
                    break;
                default:
                    throw new ConfigurationParseException(
                            String.format("%s is not a valid entity behaviour\n", behaviour));
            }


            Entity ret = new DynamicEntityImpl(
                    kinematicState,
                    volume,
                    Entity.Layer.FOREGROUND,
                    image,
                    collisionStrategy,
                    behaviourStrategy
            );

            ret.getNotifier().attach(level.getListener());
            return ret;

        } catch (Exception e) {
            throw new ConfigurationParseException(
                    String.format("Invalid cloud entity configuration | %s | %s", config, e));
        }
    }
}
