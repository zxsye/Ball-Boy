package ballboy.model.entities.utilities;

import ballboy.model.Prototype;

/**
 * Discrete kinematic state implementation.
 */
public class KinematicStateImpl implements KinematicState, Prototype {
    private Vector2D position;
    private Vector2D previousPosition;
    private Vector2D velocity;
    private double horizontalAcceleration;

    private KinematicStateImpl(
            Vector2D position,
            Vector2D velocity,
            double horizontalAcceleration) {
        this.position = position;
        this.previousPosition = position;
        this.velocity = velocity;
        this.horizontalAcceleration = horizontalAcceleration;
    }

    private KinematicStateImpl(
            Vector2D position,
            Vector2D previousPosition,
            Vector2D velocity,
            double horizontalAcceleration) {
        this.position = position;
        this.previousPosition = previousPosition;
        this.velocity = velocity;
        this.horizontalAcceleration = horizontalAcceleration;
    }

    @Override
    public Vector2D getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Vector2D position) {
        this.position = position;
    }

    @Override
    public Vector2D getVelocity() {
        return this.velocity;
    }

    @Override
    public void setVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    @Override
    public double getHorizontalAcceleration() {
        return this.horizontalAcceleration;
    }

    @Override
    public void setHorizontalAcceleration(double acceleration) {
        this.horizontalAcceleration = acceleration;
    }

    @Override
    public void update(
            double milliSeconds,
            double levelGravity) {
        this.previousPosition = position;
        this.position = this.position.add(this.velocity.scale(milliSeconds).scale(1e-3));
        Vector2D acceleration = new Vector2D(horizontalAcceleration, levelGravity);
        this.velocity = this.velocity.add(acceleration.scale(milliSeconds).scale(1e-3));
    }

    @Override
    public KinematicStateImpl copy() {
        return new KinematicStateImpl(position.copy(),
                previousPosition.copy(),
                velocity.copy(),
                horizontalAcceleration);
    }

    @Override
    public Vector2D getPreviousPosition() {
        return previousPosition;
    }

    public static class KinematicStateBuilder {
        private Vector2D position = Vector2D.ZERO;
        private Vector2D velocity = Vector2D.ZERO;

        public KinematicStateBuilder setPosition(Vector2D position) {
            this.position = position;
            return this;
        }

        public KinematicStateBuilder setHorizontalVelocity(double velocityX) {
            this.velocity = this.velocity.setX(velocityX);
            return this;
        }

        public KinematicStateImpl build() {
            return new KinematicStateImpl(
                    this.position,
                    this.velocity,
                    0
            );
        }
    }
}
