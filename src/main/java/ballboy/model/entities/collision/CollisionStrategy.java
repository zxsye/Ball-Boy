package ballboy.model.entities.collision;

import ballboy.model.Entity;
import ballboy.model.Prototype;

/**
 * Collision strategy injected into all concrete dynamic entities.
 * It should contain the non-physical entity-specific behaviour for collisions.
 */
public interface CollisionStrategy extends Prototype {

    void collideWith(
            Entity currentEntity,
            Entity hitEntity);

    @Override
    CollisionStrategy copy();
}
