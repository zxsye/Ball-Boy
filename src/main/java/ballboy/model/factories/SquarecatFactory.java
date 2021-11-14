package ballboy.model.factories;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.DynamicEntityImpl;
import ballboy.model.entities.behaviour.*;
import ballboy.model.entities.collision.CollisionStrategy;
import ballboy.model.entities.collision.EnemyCollisionStrategy;
import ballboy.model.entities.collision.PassiveCollisionStrategy;
import ballboy.model.entities.utilities.*;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

import java.util.Optional;

public class SquarecatFactory implements EntityFactory {

    private static final double RADIUS = 30;

    @Override
    public Entity createEntity(
            Level level,
            JSONObject config) {
        try {
            double heroX = ((Number) config.get("heroX")).doubleValue();
            double heroY = ((Number) config.get("heroY")).doubleValue();
//            double RADIUS = ((Number) config.get("RADIUS")).doubleValue();

            double startX = heroX - RADIUS;
            double startY = heroY - RADIUS;

//            double startVelocityX = ((Number) config.get("startVelocityX")).doubleValue();
            double startVelocityX = 10.0;
//            String behaviour = (String) config.get("behaviour");

            Optional<Double> height = Optional.ofNullable(((Number) config.get("height"))).map(Number::doubleValue);

            String imageName = (String) config.get("image");

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

            CollisionStrategy collisionStrategy = new PassiveCollisionStrategy();
            BehaviourStrategy behaviourStrategy = new SquarecatBehaviourStrategy(level, RADIUS);

            return new DynamicEntityImpl(
                    kinematicState,
                    volume,
                    Entity.Layer.FOREGROUND,
                    image,
                    collisionStrategy,
                    behaviourStrategy
            );

        } catch (Exception e) {
//            System.out.println("bovey");
//            e.printStackTrace();
            throw new ConfigurationParseException(
                    String.format("Invalid Squarecat entity configuration | %s | %s", config, e));
        }
    }
}
