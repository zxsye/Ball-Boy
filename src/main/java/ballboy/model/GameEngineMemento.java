package ballboy.model;

import java.util.List;

public class GameEngineMemento implements Memento {

    private final GameEngineImpl gameEngine;
    private final List<Memento> levelList;
    private final Integer levelIndex;
    private final Integer totalScore;


    public GameEngineMemento(GameEngineImpl gameEngine,
                             List<Memento> levelList,
                             Integer levelIndex,
                             Integer totalScore) {

        this.gameEngine = gameEngine;
        this.levelList = levelList;
        this.levelIndex = levelIndex;
        this.totalScore = totalScore;
    }

    public Integer getLevelIndex() {
        return levelIndex;
    }

    public List<Memento> getLevelList() {
        return levelList;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    /**
     * Restore saved gamestate to the attached GameEngine
     */
    @Override
    public void restore() {
        this.gameEngine.setGameState(this);
    }
}
