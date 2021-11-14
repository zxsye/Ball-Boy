package ballboy.model.entities;

import ballboy.model.Entity;
import ballboy.model.Prototype;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.EntityNotifier;
import ballboy.model.entities.utilities.ProjectileKinematicsUtil;
import ballboy.model.entities.utilities.Vector2D;
import javafx.scene.image.Image;

import javax.naming.ldap.Control;
import java.util.Optional;

/**
 * Wrapper class to provide game controls to a DynamicEntity.
 *
 * @param <T> The entity class being controlled.
 */
public class ControllableDynamicEntity<T extends DynamicEntity> extends DynamicEntity {
    private static final double LEFT_ACC = -200;

    private final T entity;
    private final double maxHorizontalVelocity;
    private final double floorHeight;
    private final double gravity;
    private final Vector2D startingPosition;

    private Optional<Runnable> afterNextBounce = Optional.empty();

    public ControllableDynamicEntity(
            T entity,
            Vector2D startingPosition,
            double maxHorizontalVelocity,
            double floorHeight,
            double gravity) {
        this.entity = entity;
        this.maxHorizontalVelocity = Math.abs(maxHorizontalVelocity);
        this.floorHeight = Math.abs(floorHeight);
        this.gravity = gravity;
        this.startingPosition = startingPosition;
    }

    /**
     * @return int The hashcode of the wrapped entity instance.
     */
    @Override
    public int hashCode() {
        return entity.hashCode();
    }

    /**
     * Sets the vertical velocity of the current entity to a value that will increase its vertical bounce height
     * relative to the floor by a fixed amount.
     *
     * @return boolean True if boost was successfully applied.
     */
    public boolean boostHeight() {

        double deltaToMax = -ProjectileKinematicsUtil.getDeltaToMaxHeight(-this.entity.getVelocity().getY(), -gravity);
        double currentHeight = floorHeight - this.entity.getPosition().getY();
        double currentBounceHeight = deltaToMax + currentHeight;
        double newBounceHeight = currentBounceHeight * 2;
        double newDeltaToMax = Math.min(newBounceHeight - currentBounceHeight, floorHeight * 0.98 - currentHeight);
        double newVelYCart = ProjectileKinematicsUtil.getCurrentVelocityForMaxHeight(newDeltaToMax, gravity);

        /*
         * NaN in the instance when the equation doesn't have a real solution. This will occur if the
         * entity is within epsilon of the max height (level height)
         */
        if (Double.isNaN(newVelYCart)) {
            return false;
        }

        if (this.getVelocity().getY() > 0) { // traveling down
            this.entity.setVelocity(this.entity.getVelocity().setY(newVelYCart));
        } else { // travelling up
            this.entity.setVelocity(this.entity.getVelocity().setY(-newVelYCart));
        }

        return true;
    }

    /**
     * Sets the vertical velocity of the current entity to a value that decreases the current bounce height by a fixed
     * value.
     *
     * @return boolean True if the bounce height of the current entity was successfully reduced.
     */
    public boolean dropHeight() {
        if (afterNextBounce.isPresent()) {
            return false;
        }

        afterNextBounce = Optional.of(() -> {
            double bounceHeight = ProjectileKinematicsUtil.getDeltaToMaxHeight(-this.entity.getVelocity().getY(),
                    -gravity);
            double yVelCart = ProjectileKinematicsUtil.getCurrentVelocityForMaxHeight(bounceHeight * 0.5, -gravity);
            this.entity.setVelocity(this.entity.getVelocity().setY(-yVelCart));
        });

        return true;
    }

    /**
     * Applies a horizontal acceleration in the left direction until a maximum velocity is reached.
     *
     * @return boolean True if the entity was successfully moved left.
     */
    public boolean moveLeft() {
        entity.setHorizontalAcceleration(LEFT_ACC);
        return true;
    }

    /**
     * Applies a horizontal acceleration in the right direction until a maximum velocity is reached.
     *
     * @return boolean True if the entity was successfully moved right.
     */
    public boolean moveRight() {
        entity.setHorizontalAcceleration(-LEFT_ACC);
        return true;
    }

    /**
     * Resets the current state to its state when the current instance was instantiated.
     * The entity is moved back to its starting position, and the control inputs are reset.
     *
     * @return boolean True if the state was successfully reset.
     */
    public boolean reset() {
        entity.setVelocity(Vector2D.ZERO);
        entity.setPosition(startingPosition);
        entity.setHorizontalAcceleration(0);
        return true;
    }

    @Override
    public boolean collidesWith(Entity e) {
        return entity.collidesWith(e);
    }

    @Override
    public void collideWith(Entity e) {
        entity.collideWith(e);
    }

    @Override
    public Vector2D getPositionBeforeLastUpdate() {
        return this.entity.getPositionBeforeLastUpdate();
    }

    @Override
    public Vector2D getVelocity() {
        return entity.getVelocity();
    }

    @Override
    public void setVelocity(Vector2D vel) {
        entity.setVelocity(vel);
    }

    @Override
    public double getHorizontalAcceleration() {
        return entity.getHorizontalAcceleration();
    }

    @Override
    public void setHorizontalAcceleration(double horizontalAcceleration) {
        entity.setHorizontalAcceleration(horizontalAcceleration);
    }

    /**
     * Updates the wrapped entity and enforces velocity limits.
     *
     * @param durationNano The duration to update the entity by.
     */
    @Override
    public void update(
            double durationNano,
            double levelGravity) {
        entity.update(durationNano, levelGravity);

        if (this.entity.getVelocity().getY() < 0 && afterNextBounce.isPresent()) {
            afterNextBounce.get().run();
            afterNextBounce = Optional.empty();
        }

        enforceHorizontalVelocityLimit();
    }

    @Override
    public Image getImage() {
        return entity.getImage();
    }

    @Override
    public Vector2D getPosition() {
        return entity.getPosition();
    }

    @Override
    public void setPosition(Vector2D pos) {
        entity.setPosition(pos);
    }

    @Override
    public double getHeight() {
        return entity.getHeight();
    }

    @Override
    public double getWidth() {
        return entity.getWidth();
    }

    @Override
    public Layer getLayer() {
        return entity.getLayer();
    }

    @Override
    public AxisAlignedBoundingBox getVolume() {
        return entity.getVolume();
    }

    /**
     * @return Version of this Entity as a EntityNotifier
     */
    @Override
    public EntityNotifier getNotifier() {
        return null;
    }

    /**
     * Enforces the current horizontal velocity limits. If the limit is exceeded, horizontal acceleration
     * of the wrapped entity is set to 0.
     */
    private void enforceHorizontalVelocityLimit() {
        double velocityX = entity.getVelocity().getX();

        if (Math.abs(velocityX) > maxHorizontalVelocity) {
            entity.setVelocity(
                    entity.getVelocity().setX(velocityX < 0 ? -maxHorizontalVelocity : maxHorizontalVelocity));

            double accX = entity.getHorizontalAcceleration();
            if (velocityX < 0 && accX < 0 || velocityX > 0 && accX > 0) {
                entity.setHorizontalAcceleration(0);
            }
        }
    }

    @Override
    public ControllableDynamicEntity<DynamicEntity> copy() {
        return new ControllableDynamicEntity<>(
                entity.copy(),
                startingPosition.copy(),
                maxHorizontalVelocity,
                floorHeight,
                gravity
        );
    }
}
