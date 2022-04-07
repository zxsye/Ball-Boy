package ballboy.model;

/**
 * Interface to allow entities and other objects in the game to
 * be deep copied.
 */
public interface Prototype {

    /**
     *
     * @return A deep copy of this instance
     */
    Prototype copy();
}
