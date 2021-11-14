package ballboy.model;

import ballboy.ConfigurationParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the GameEngine interface.
 * This provides a common interface for the entire game.
 */
public class GameEngineImpl implements GameEngine {
    private final List<Level> levelList;
    private Level level;
    private int levelIndex;

    private int totalScore;
    private boolean isGameFinish;

    public GameEngineImpl(List<Level> level, Integer startIndex) {
        this.levelList = level;
        if (startIndex >= level.size()) {
            throw new ConfigurationParseException("currentLevelIndex is invalid");
        }
        this.levelIndex = startIndex;
        this.level = level.get(startIndex);
        this.totalScore = 0;
    }

    public Level getCurrentLevel() {
        return level;
    }

    public void startLevel() {
        // TODO: Handle when multiple levels has been implemented
        return;
    }

    public boolean boostHeight() {
        return level.boostHeight();
    }

    public boolean dropHeight() {
        return level.dropHeight();
    }

    public boolean moveLeft() {
        return level.moveLeft();
    }

    public boolean moveRight() {
        return level.moveRight();
    }

    public void tick() {
        level.update();
        if (level.isLevelFinish()) {
            finishCurrentLevel();
        }
    }

    @Override
    public void finishCurrentLevel() {
        this.levelIndex++;

        if (levelIndex < levelList.size()) {
            this.totalScore += this.level.getTotalScore();
            this.level = levelList.get(levelIndex);
        } else {
            this.totalScore += this.level.getTotalScore();
            this.isGameFinish = true;
        }
    }

    public String getTotalScoreAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Total Score: ");
        sb.append(this.totalScore);
        sb.append("\n");
        return sb.toString();
    }

    public Memento saveGameState() {
        List<Memento> levelMementoList = new ArrayList<>();
        for (Level l : this.levelList) {
            levelMementoList.add(l.saveLevelState());
        }
        return new GameEngineMemento(this,
                levelMementoList,
                this.levelIndex,
                this.totalScore);
    }

    public void setGameState(GameEngineMemento memento) {
        // Load levels
        for (Memento m : memento.getLevelList()) {
            m.restore();
        }
        this.levelIndex = memento.getLevelIndex();
        this.level = this.levelList.get(this.levelIndex);
        this.totalScore = memento.getTotalScore();
    }

    public boolean isGameFinish() {
        return isGameFinish;
    }
}