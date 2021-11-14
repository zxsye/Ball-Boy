package ballboy.model.entities.utilities;

/**
 * Kinematic Projectile Motion calculations, implemented in cartesian coordinates.
 */
public class ProjectileKinematicsUtil {

    /**
     * Calculates the current height of the projectile if it started at 0.0 altitude and had the provided
     * starting conditions.
     *
     * @param currentYVel The current cartesian vertical velocity.
     * @param gravity     The current cartesian vertical acceleration.
     * @return double The height maximum height achieved by the motion.
     */
    public static double getDeltaToMaxHeight(
            double currentYVel,
            double gravity) {
        return currentYVel * currentYVel / (2 * gravity);
    }

    /**
     * Calculates the required initial vertical velocity for a projectile to reach a maximum height of the provided
     * height, with the provided acceleration.
     *
     * @param height  The desired cartesian height.
     * @param gravity The current cartesian vertical acceleration.
     * @return double The starting velocity required to produce a projectile motion with the provided maximum height.
     */
    public static double getCurrentVelocityForMaxHeight(
            double height,
            double gravity) {
        return Math.sqrt(height * 2 * gravity);
    }
}
