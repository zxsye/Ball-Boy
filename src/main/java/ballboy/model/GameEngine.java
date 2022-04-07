package ballboy.model;

/**
 * The base interface for interacting with the Ballboy model
 */
public interface GameEngine {
    /**
     * Return the currently loaded level
     *
     * @return The current level
     */
    Level getCurrentLevel();

    /**
     * Start the level
     */
    void startLevel();

    /**
     * Increases the bounce height of the current hero.
     *
     * @return boolean True if the bounce height of the hero was successfully boosted.
     */
    boolean boostHeight();

    /**
     * Reduces the bounce height of the current hero.
     *
     * @return boolean True if the bounce height of the hero was successfully dropped.
     */
    boolean dropHeight();

    /**
     * Applies a left movement to the current hero.
     *
     * @return True if the hero was successfully moved left.
     */
    boolean moveLeft();

    /**
     * Applies a right movement to the current hero.
     *
     * @return True if the hero was successfully moved right.
     */
    boolean moveRight();

    /**
     * Instruct the model to progress forward in time by one increment.
     */
    void tick();

    /**
     *
     * @return Printable string for total score: scores of completed levels
     */
    String getTotalScoreAsString();

    /**
     * Save the current state of game engine (and its levels)
     * @return Memento representing saved state
     */
    Memento saveGameState();

    /**
     * Finish the current level and go to next level.
     * If all levels are finished, then mark game as finished
     */
    void finishCurrentLevel();

    /**
     * Indicate finish status of game
     * @return True if game is finished with no more levels to complete
     */
    boolean isGameFinish();
}
