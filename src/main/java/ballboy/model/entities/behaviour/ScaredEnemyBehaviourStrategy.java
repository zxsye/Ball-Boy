package ballboy.model.entities.behaviour;

import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;

/**
 * A scared behaviour strategy that makes the entity follow accelerate away
 * from the ballboy if within a threshold distance. A velocity cap is also
 * applied to keep the velocity within a reasonable threshold.
 */
public class ScaredEnemyBehaviourStrategy implements BehaviourStrategy {
    private static final double HORIZONTAL_ACCELERATION = 100;
    private static final double MAX_HOR_VELOCITY = 25;
    private static final double DISTANCE_THRESHOLD = 100;

    private final Level level;

    public ScaredEnemyBehaviourStrategy(Level level) {
        this.level = level;
    }

    @Override
    public void behave(
            DynamicEntity entity,
            double frameDurationMilli) {
        double distanceBetweenEntityAndHero = Math.abs(level.getHeroX() - entity.getPosition().getX());

        /*
         If distance is below the threshold, actively accelerate away until it is not.
         */
        if (distanceBetweenEntityAndHero < DISTANCE_THRESHOLD) {
            if (entity.getPosition().isRightOf(level.getHeroX())) {
                entity.setHorizontalAcceleration(HORIZONTAL_ACCELERATION);
            } else {
                entity.setHorizontalAcceleration(-HORIZONTAL_ACCELERATION);
            }
            double xVel = entity.getVelocity().getX();
            if (Math.abs(xVel) > MAX_HOR_VELOCITY) {
                entity.setVelocity(entity.getVelocity().setX(xVel < 0 ? -MAX_HOR_VELOCITY : MAX_HOR_VELOCITY));
                entity.setHorizontalAcceleration(0);
            }
        } else {
            entity.setHorizontalAcceleration(0);
        }
    }

    @Override
    public ScaredEnemyBehaviourStrategy copy() {
        return new ScaredEnemyBehaviourStrategy(level);
    }
}


