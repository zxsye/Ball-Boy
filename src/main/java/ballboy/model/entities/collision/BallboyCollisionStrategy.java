package ballboy.model.entities.collision;

import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.Prototype;

/**
 * Non-physical collision behaviour for the hero. This is delegated to by the
 * hero instance whenever it collides with another entity, static or dynamic.
 */
public class BallboyCollisionStrategy implements CollisionStrategy {
    private final Level level;

    public BallboyCollisionStrategy(Level level) {
        this.level = level;
    }

    @Override
    public void collideWith(
            Entity ballboy,
            Entity hitEntity) {

        if (level.isFinish(hitEntity)) {
            level.finish();
        }
    }

    @Override
    public BallboyCollisionStrategy copy() {
        return new BallboyCollisionStrategy(level);
    }
}
