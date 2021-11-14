package ballboy.model.entities.collision;

import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.Prototype;
import ballboy.model.entities.utilities.EntityNotifier;

/**
 * Collision logic for enemies.
 */
public class EnemyCollisionStrategy implements CollisionStrategy {
    private final Level level;
    private final String colour;

    public EnemyCollisionStrategy(Level level, String colour) {
        this.level = level;
        this.colour = colour;
    }

    @Override
    public void collideWith(
            Entity enemy,
            Entity hitEntity) {

        if (level.isHero(hitEntity)) {
            level.resetHero();

        } else if (level.isSquarecat(hitEntity)) {
            EntityNotifier notifier = enemy.getNotifier();
            if (notifier != null) {
                notifier.notifyAllListeners(colour, 100);
            }
            level.queueDeleteEntity(enemy);
        }
    }

    @Override
    public EnemyCollisionStrategy copy() {
        return new EnemyCollisionStrategy(level, this.colour);
    }
}
