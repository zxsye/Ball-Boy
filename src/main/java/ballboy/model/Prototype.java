package ballboy.model;

/**
 * Interface to allow entities and other objects in the game to
 * be deep copied.
 */
public interface Prototype {
    Prototype copy();
}
