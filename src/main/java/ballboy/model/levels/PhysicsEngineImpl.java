package ballboy.model.levels;

import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.StaticEntity;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.Vector2D;

/**
 * Primitive PhysicsEngine implementation.
 */
public class PhysicsEngineImpl implements PhysicsEngine {

    private final double frameDurationMilli;

    public PhysicsEngineImpl(double frameDurationMilli) {
        this.frameDurationMilli = frameDurationMilli;
    }

    @Override
    public void resolveCollision(
            DynamicEntity a,
            StaticEntity b,
            Level level) {
        if ((a.getLayer() == Entity.Layer.BACKGROUND || b.getLayer() == Entity.Layer.BACKGROUND)) {
            return;
        }

        // previous position before the last update was called
        Vector2D previousPosition = a.getPositionBeforeLastUpdate();

        AxisAlignedBoundingBox aPreviousPositionVolume = a.getVolume().copy();
        aPreviousPositionVolume.setTopLeft(previousPosition);

        /*
         * collided prior to the most recent update, so no need to
         * resolve the collision as it would have been delt with before
         */
        if (aPreviousPositionVolume.collidesWith(b.getVolume())) {
            return;
        }

        // volume with only the horizontal translation applied
        AxisAlignedBoundingBox aHorizontallySteppedVolume = aPreviousPositionVolume.copy();
        aHorizontallySteppedVolume.setTopLeft(a.getPosition().setY(previousPosition.getY()));

        if (aHorizontallySteppedVolume.collidesWith(b.getVolume())) { // horizontal collision
            reflectX(a);

            if (a.getPosition().isLeftOf(b.getVolume().getLeftX())) {
                a.setPosition(new Vector2D(b.getVolume().getLeftX() - a.getWidth(), a.getPosition().getY()));
            } else {
                a.setPosition(a.getPosition().setX(b.getVolume().getRightX()));
            }
        } else { // vertical collision
            reflectY(a, level.getGravity());

            if (a.getPosition().isAbove(b.getVolume().getTopY())) {
                a.setPosition(a.getPosition().setY(b.getVolume().getTopY() - a.getHeight()));
            } else {
                a.setPosition(a.getPosition().setY(b.getVolume().getBottomY()));
            }
        }
    }

    @Override
    public void resolveCollision(
            DynamicEntity a,
            DynamicEntity b) {
        if ((a.getLayer() == Entity.Layer.BACKGROUND || b.getLayer() == Entity.Layer.BACKGROUND)) {
            return;
        }

        /*
         * if they collided in a previous update cycle then dont handle it, as
         * it would have been handled then
         */
        AxisAlignedBoundingBox aVolumeBeforeUpdate = a.getVolume().copy();
        aVolumeBeforeUpdate.setTopLeft(a.getPositionBeforeLastUpdate());

        AxisAlignedBoundingBox bVolumeBeforeUpdate = b.getVolume().copy();
        bVolumeBeforeUpdate.setTopLeft(b.getPositionBeforeLastUpdate());

        if (aVolumeBeforeUpdate.collidesWith(bVolumeBeforeUpdate)) {
            return;
        }

        // assuming constant mass for now
        double aMass = 1.0;
        double bMass = 1.0;

        /*
         * step the previous volume only horizontally. If there is a collision it is treated as if the objects
         * collided in the horizontal axis. If not, then it is treated as if the objects collided vertically
         */
        AxisAlignedBoundingBox aVolumeOnlySteppedHorizontally = aVolumeBeforeUpdate.copy();
        aVolumeOnlySteppedHorizontally.setTopLeft(a.getPosition().setY(a.getPositionBeforeLastUpdate().getY()));

        boolean isHorizontalCollision = aVolumeOnlySteppedHorizontally.collidesWith(bVolumeBeforeUpdate);
        if (isHorizontalCollision) { // horizontal collision
            double aVelocityX = a.getVelocity().getX();
            double bVelocityX = b.getVelocity().getX();

            double aVelocityXNew =
                    ((aMass - bMass) / (aMass + bMass)) * aVelocityX + ((2 * bMass) / (aMass + bMass)) * bVelocityX;
            double bVelocityXNew =
                    ((2 * aMass) / (aMass + bMass)) * aVelocityX - ((aMass - bMass) / (aMass + bMass)) * bVelocityX;

            a.setVelocity(a.getVelocity().setX(aVelocityXNew));
            b.setVelocity(b.getVelocity().setX(bVelocityXNew));
        } else { // vertical collision
            double aVelocityY = a.getVelocity().getY();
            double bVelocityY = b.getVelocity().getY();

            double aVelocityYNew =
                    ((aMass - bMass) / (aMass + bMass)) * aVelocityY + ((2 * bMass) / (aMass + bMass)) * bVelocityY;
            double bVelocityYNew =
                    ((2 * aMass) / (aMass + bMass)) * aVelocityY - ((aMass - bMass) / (aMass + bMass)) * bVelocityY;

            a.setVelocity(a.getVelocity().setY(aVelocityYNew));
            b.setVelocity(b.getVelocity().setY(bVelocityYNew));
        }
    }

    @Override
    public void enforceWorldLimits(
            DynamicEntity a,
            Level level) {
        // if outside horizontal boundaries and travelling in the wrong direction
        if (a.getPosition().isRightOf(level.getLevelWidth()) && a.getVelocity().getX() > 0 || a.getPosition().isLeftOf(
                0.0) && a.getVelocity().getX() < 0) {
            reflectX(a);
        }

        // below ground
        if (a.getPosition().addY(a.getHeight()).isBelow(level.getFloorHeight()) && a.getVelocity().getY() > 0) {
            reflectY(a, level.getGravity());
            a.setPosition(a.getPosition().setY(level.getFloorHeight() - a.getHeight()));
        }

        // above max height
        if (a.getPosition().isAbove(0) && a.getVelocity().getY() < 0) {
            reflectY(a, level.getGravity());
        }
        return;
    }

    /**
     * Reflects the provided entities motion in the horizontal axis.
     * <p>
     * Acceleration before the collision is accounted for.
     *
     * @param entity The entity to be reflected horizontally.
     */
    private void reflectX(DynamicEntity entity) {
        entity.setVelocity(
                entity.getVelocity().addX(
                        -1 * entity.getHorizontalAcceleration() * frameDurationMilli * 1e-3).reflectX()
        );
    }

    /**
     * Reflects the provided entities motion in the horizontal axis.
     * <p>
     * Acceleration before the collision is accounted for.
     *
     * @param entity The entity to be reflected vertically.
     */
    private void reflectY(
            DynamicEntity entity,
            double levelGravity) {
        entity.setVelocity(
                entity.getVelocity().addY(-1 * levelGravity * frameDurationMilli * 1e-3).reflectY()
        );
    }

}
