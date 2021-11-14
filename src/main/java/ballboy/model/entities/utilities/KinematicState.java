package ballboy.model.entities.utilities;

/**
 * Encapsulation of a physical objects motion.
 */
public interface KinematicState {

    /**
     * @return Vector2 The previous position before the last update.
     */
    Vector2D getPreviousPosition();

    /**
     * @return Vector2 The current position.
     */
    Vector2D getPosition();

    /**
     * @param position The new position of this state.
     */
    void setPosition(Vector2D position);

    /**
     * @return Vector2 The current velocity.
     */
    Vector2D getVelocity();

    /**
     * @param velocity The new velocity of this state.
     */
    void setVelocity(Vector2D velocity);

    /**
     * @return double The current horizontal acceleration of this state.
     */
    double getHorizontalAcceleration();

    /**
     * @param acceleration The new horizontal acceleration of this state.
     */
    void setHorizontalAcceleration(double acceleration);

    /**
     * @param milliSeconds The duration that the state is stepped forward by.
     * @param levelGravity The current gravity to be applied in the downwards direction.
     */
    void update(
            double milliSeconds,
            double levelGravity);

    /**
     * @return KinematicState A deep copy of this instance.
     */
    KinematicState copy();
}
