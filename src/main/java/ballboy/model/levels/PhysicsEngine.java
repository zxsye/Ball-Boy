package ballboy.model.levels;

import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.StaticEntity;

/**
 * Primitive physics implementation for the game.
 */
public interface PhysicsEngine {

    /**
     * Resolves a collision between a dynamic entity and a static one.
     *
     * @param a     The dynamic entity.
     * @param b     The static entity that the dynamic entity collided into.
     * @param level The current level.
     */
    void resolveCollision(
            DynamicEntity a,
            StaticEntity b,
            Level level);

    /**
     * Resolves a collision between two dynamic entities, conserving their current momentum.
     *
     * @param a The first dynamic entity.
     * @param b The second dynamic entity.
     */
    void resolveCollision(
            DynamicEntity a,
            DynamicEntity b);

    /**
     * Enforces level boundaries on the provided dynamic entity.
     * When the dynamic entity is outside the level bounds, it will be redirected.
     *
     * @param a     The dynamic entity being enforced.
     * @param level The current level.
     */
    void enforceWorldLimits(
            DynamicEntity a,
            Level level);
}
