package ballboy.model;

/**
 * Class to store and save the state of the gameEngine's memento(s)
 * - Does not have access to Memento instance
 */
public class GameCaretaker {

    private final GameEngine gameEngine;
    private Memento currentMemento;

    public GameCaretaker(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public void quickLoad() {
        if (currentMemento != null) {
            currentMemento.restore();
        }
    }

    public void quickSave() {
        this.currentMemento = gameEngine.saveGameState();
    }



}
