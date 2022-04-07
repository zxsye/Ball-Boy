package ballboy.model;

/**
 * Class to manage the mementos of the gameEngine
 * - Does not have access to Memento instance
 */
public class GameCaretaker {

    private final GameEngine gameEngine;
    private Memento currentMemento;

    public GameCaretaker(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Restore the currently saved state of game engine
     */
    public void quickLoad() {
        if (currentMemento != null) {
            currentMemento.restore();
        }
    }

    /**
     * Save the current state of game engine
     */
    public void quickSave() {
        this.currentMemento = gameEngine.saveGameState();
    }



}
