package ballboy.model.entities.collision;

import ballboy.model.Entity;

/*
 * A passive collision strategy that does nothing.
 */
public class PassiveCollisionStrategy implements CollisionStrategy {

    @Override
    public void collideWith(
            Entity currentEntity,
            Entity hitEntity) {
        return;
    }

    @Override
    public PassiveCollisionStrategy copy() {
        return new PassiveCollisionStrategy();
    }
}
