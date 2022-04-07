package ballboy.model;

/**
 * Interface for a caretaker class to use. The memento
 * implementation usually will have direct access to the
 * originator implementation
 */
public interface Memento {
    /**
     * Restore saved gamestate of the stored originator
     */
    void restore();
}
