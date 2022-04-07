package ballboy.model.entities.behaviour;

import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;

/**
 * An aggressive strategy that makes the entity follow the ballboy.
 * Acceleration is applied horizontally in the direction of the ballboy
 * until a maximum velocity is reached.
 */
public class AggressiveEnemyBehaviourStrategy implements BehaviourStrategy {
    private static final double HORIZONTAL_ACCELERATION = 100;
    private static final double MAX_HORIZONTAL_VELOCITY = 25;

    private final Level level;

    public AggressiveEnemyBehaviourStrategy(Level level) {
        this.level = level;
    }

    @Override
    public void behave(
            DynamicEntity entity,
            double frameDurationMilli) {
        if (entity.getPosition().isLeftOf(level.getHeroX())) {
            entity.setHorizontalAcceleration(HORIZONTAL_ACCELERATION);
        } else {
            entity.setHorizontalAcceleration(-HORIZONTAL_ACCELERATION);
        }
        double xVel = entity.getVelocity().getX();
        if (Math.abs(xVel) > MAX_HORIZONTAL_VELOCITY) {
            entity.setVelocity(
                    entity.getVelocity().setX(xVel < 0 ? -MAX_HORIZONTAL_VELOCITY : MAX_HORIZONTAL_VELOCITY));
            entity.setHorizontalAcceleration(0);
        }
    }

    @Override
    public AggressiveEnemyBehaviourStrategy copy() {
        return new AggressiveEnemyBehaviourStrategy(level);
    }
}
