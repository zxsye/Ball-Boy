package ballboy.model.levels;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.Memento;
import ballboy.model.entities.ControllableDynamicEntity;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.StaticEntity;
import ballboy.model.entities.utilities.EntityListener;
import ballboy.model.entities.utilities.Vector2D;
import ballboy.model.factories.EntityFactory;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Level logic, with abstract factor methods.
 */
public class LevelImpl implements Level, EntityListener {

    private final List<Entity> entities = new ArrayList<>();
    private final PhysicsEngine engine;
    private final EntityFactory entityFactory;
    private ControllableDynamicEntity<DynamicEntity> hero;
    private DynamicEntity squarecat; // EDITED

    private Entity finish;

    private double levelHeight;
    private double levelWidth;
    private double levelGravity;
    private double floorHeight;
    private Color floorColor;

    private final double frameDurationMilli;
    private boolean isFinish;

    private Map<String, Integer> scoreMap = new HashMap<>();

    /**
     * A callback queue for post-update jobs. This is specifically useful for scheduling jobs mid-update
     * that require the level to be in a valid state.
     */
    private final Queue<Runnable> afterUpdateJobQueue = new ArrayDeque<>();

    public LevelImpl(
            JSONObject levelConfiguration,
            PhysicsEngine engine,
            EntityFactory entityFactory,
            double frameDurationMilli) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.frameDurationMilli = frameDurationMilli;
        this.isFinish = false;

        initLevel(levelConfiguration);
    }

    /**
     * Instantiates a level from the level configuration.
     *
     * @param levelConfiguration The configuration for the level.
     */
    private void initLevel(JSONObject levelConfiguration) {
        this.levelWidth = ((Number) levelConfiguration.get("levelWidth")).doubleValue();
        this.levelHeight = ((Number) levelConfiguration.get("levelHeight")).doubleValue();
        this.levelGravity = ((Number) levelConfiguration.get("levelGravity")).doubleValue();

        JSONObject floorJson = (JSONObject) levelConfiguration.get("floor");
        this.floorHeight = ((Number) floorJson.get("height")).doubleValue();
        String floorColorWeb = (String) floorJson.get("color");
        this.floorColor = Color.web(floorColorWeb);

        JSONArray generalEntities = (JSONArray) levelConfiguration.get("genericEntities");
        for (Object o : generalEntities) {
            this.entities.add(entityFactory.createEntity(this, (JSONObject) o));
        }

        JSONObject heroConfig = (JSONObject) levelConfiguration.get("hero");
        double maxVelX = ((Number) levelConfiguration.get("maxHeroVelocityX")).doubleValue();

        Object hero = entityFactory.createEntity(this, heroConfig);
        if (!(hero instanceof DynamicEntity)) {
            throw new ConfigurationParseException("hero must be a dynamic entity");
        }
        DynamicEntity dynamicHero = (DynamicEntity) hero;
        Vector2D heroStartingPosition = dynamicHero.getPosition();
        this.hero = new ControllableDynamicEntity<>(dynamicHero, heroStartingPosition, maxVelX, floorHeight,
                levelGravity);
        this.entities.add(this.hero);

        // SquareCat time!!
        JSONObject squarecatConfig = (JSONObject) levelConfiguration.get("squarecat");
        Object squarecat = entityFactory.createEntity(this, squarecatConfig);
        if (!(squarecat instanceof DynamicEntity)) {
            throw new ConfigurationParseException("squarecat must be a dynamic entity");
        }
        this.squarecat = (DynamicEntity) squarecat;
        this.entities.add(this.squarecat);

        JSONObject finishConfig = (JSONObject) levelConfiguration.get("finish");
        this.finish = entityFactory.createEntity(this, finishConfig);
        this.entities.add(finish);

        // Scoreboard time
        JSONArray scoreColours = (JSONArray) levelConfiguration.get("scoreColours");
        for (Object o : scoreColours) {
            this.scoreMap.put((String) o, 0);
        }
    }

    @Override
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    private List<DynamicEntity> getDynamicEntities() {
        return entities.stream().filter(e -> e instanceof DynamicEntity).map(e -> (DynamicEntity) e).collect(
                Collectors.toList());
    }

    private List<StaticEntity> getStaticEntities() {
        return entities.stream().filter(e -> e instanceof StaticEntity).map(e -> (StaticEntity) e).collect(
                Collectors.toList());
    }

    @Override
    public double getLevelHeight() {
        return this.levelHeight;
    }

    @Override
    public double getLevelWidth() {
        return this.levelWidth;
    }

    @Override
    public double getHeroHeight() {
        return hero.getHeight();
    }

    @Override
    public double getHeroWidth() {
        return hero.getWidth();
    }

    @Override
    public double getFloorHeight() {
        return floorHeight;
    }

    @Override
    public Color getFloorColor() {
        return floorColor;
    }

    @Override
    public double getGravity() {
        return levelGravity;
    }

    @Override
    public void update() {
        List<DynamicEntity> dynamicEntities = getDynamicEntities();

        dynamicEntities.stream().forEach(e -> {
            e.update(frameDurationMilli, levelGravity);
        });

        // Collide all the dynamic entities with entities (dynamic / static)
        for (int i = 0; i < dynamicEntities.size(); ++i) {
            DynamicEntity dynamicEntityA = dynamicEntities.get(i);

            // Dynamic
            for (int j = i + 1; j < dynamicEntities.size(); ++j) {
                DynamicEntity dynamicEntityB = dynamicEntities.get(j);

                if (dynamicEntityA.collidesWith(dynamicEntityB)) {
                    dynamicEntityA.collideWith(dynamicEntityB);
                    dynamicEntityB.collideWith(dynamicEntityA);
                    if (!isHero(dynamicEntityA) && !isHero(dynamicEntityB) &&
                        !isSquarecat(dynamicEntityA) && !isSquarecat(dynamicEntityB)) {
                        engine.resolveCollision(dynamicEntityA, dynamicEntityB);
                    }
                }
            }
            // Static
            for (StaticEntity staticEntity : getStaticEntities()) {
                if (dynamicEntityA.collidesWith(staticEntity)) {
                    dynamicEntityA.collideWith(staticEntity);
                    if (isHero(dynamicEntityA) && isFinish(staticEntity)) {
                        System.out.println("LEVEL FINISH");
                    }
                    if (!isSquarecat(dynamicEntityA)) {
                        engine.resolveCollision(dynamicEntityA, staticEntity, this);
                    }
                }
            }
        }

        dynamicEntities.stream().forEach(e -> engine.enforceWorldLimits(e, this));

        afterUpdateJobQueue.forEach(j -> j.run());
        afterUpdateJobQueue.clear();

    }

    @Override
    public double getHeroX() {
        return hero.getPosition().getX();
    }

    @Override
    public double getHeroY() {
        return hero.getPosition().getY();
    }

    @Override
    public boolean boostHeight() {
        return hero.boostHeight();
    }

    @Override
    public boolean dropHeight() {
        return hero.dropHeight();
    }

    @Override
    public boolean moveLeft() {
        return hero.moveLeft();
    }

    @Override
    public boolean moveRight() {
        return hero.moveRight();
    }

    @Override
    public boolean isHero(Entity entity) {
        return entity == hero;
    }

    @Override
    public boolean isFinish(Entity entity) {
        return this.finish == entity;
    }

    /**
     * EDITED
     *
     * @param entity - The entity to be checked.
     * @return boolean True if the provided entity is the current squarecat.
     */
    @Override
    public boolean isSquarecat(Entity entity) {
        return this.squarecat == entity;
    }

    @Override
    public void resetHero() {
        afterUpdateJobQueue.add(() -> this.hero.reset());
    }

    /**
     * EDITED
     *
     * @param entity - entity to be deleted
     */
    @Override
    public void queueDeleteEntity(Entity entity) {
        afterUpdateJobQueue.add(() -> this.entities.remove(entity));
    }

    @Override
    public boolean isLevelFinish() {
        return isFinish;
    }

    public void finish() {
//        Platform.exit();
        isFinish = true;
    }

    /**
     * @return Version of this instance that is EntityListener, otherwise null
     */
    @Override
    public EntityListener getListener() {
        return this;
    }

    @Override
    public void updateFromNotifier(String colour, Integer pointUpdate) {
        if (scoreMap.containsKey(colour)) {
            Integer newScore = scoreMap.get(colour) + pointUpdate;
            scoreMap.put(colour, newScore);
        }

    }

    @Override
    public String getScoreString() {
        StringBuilder sb = new StringBuilder();
        for (String key : scoreMap.keySet()) {
            sb.append(key + "  ");
            sb.append(scoreMap.get(key));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Integer getTotalScore() {

        Integer sum = 0;
        for (Integer i : scoreMap.values()) {
            sum += i;
        }
        return sum;
    }

    @Override
    public Memento saveLevelState() {
        return new LevelMemento(this);
    }

    public static class LevelMemento implements Memento {
    
        private final LevelImpl level;
        
        private final List<Entity> entities = new ArrayList<>();
        private ControllableDynamicEntity<DynamicEntity> hero;
        private DynamicEntity squarecat; // EDITED
    
        private Entity finish;
    
        private final double levelHeight;
        private final double levelWidth;
        private final double levelGravity;
        private final double floorHeight;
        private final Color floorColor;
    
        private final boolean isFinish;
    
        private final Map<String, Integer> scoreMap = new HashMap<>();
        
        public LevelMemento(LevelImpl level) {
            this.level = level;
    
//            LevelImpl this = new LevelImpl(
//                    level.engine.copy(),
//                    level.frameDurationMilli,
//                    level.entityFactory); // same entityFactory is sufficient
    
            this.levelHeight = level.levelHeight;
            this.levelWidth = level.levelWidth;
            this.levelGravity = level.levelGravity;
            this.floorHeight = level.floorHeight;
            this.floorColor = Color.web(level.floorColor.toString());
            this.isFinish = level.isFinish;

            for (String key : level.scoreMap.keySet()) {
                this.scoreMap.put(key, level.scoreMap.get(key));
            }
    
            for (Entity entity : level.entities) {
                if (level.isHero(entity)) {
                    this.hero = level.hero.copy();
//                    this.entities.add(this.hero);
    
                } else if (level.isFinish(entity)) {
                    this.finish = level.finish.copy();
//                    this.entities.add(this.finish);
    
                } else if (level.isSquarecat(entity)) {
                    this.squarecat = level.squarecat.copy();
//                    this.entities.add(this.squarecat);
    
                } else {
                    this.entities.add(entity.copy());
                }
    
            }
        }
        
        /**
         * Restore saved gamestate to the attached GameEngine
         */
        @Override
        public void restore() {
            level.afterUpdateJobQueue.clear();
            level.entities.clear();

            level.levelHeight = this.levelHeight;
            level.levelWidth = this.levelWidth;
            level.levelGravity = this.levelGravity;
            level.floorHeight = this.floorHeight;
            level.floorColor = Color.web(this.floorColor.toString());
            level.isFinish = this.isFinish;

            for (String key : this.scoreMap.keySet()) {
                level.scoreMap.put(key, this.scoreMap.get(key));
            }

            for (Entity entity : this.entities) {
                level.entities.add(entity.copy());
            }

            // Add in special entities
            level.hero = this.hero.copy();
            level.finish = this.finish.copy();
            level.squarecat = this.squarecat.copy();

            level.entities.add(level.hero);
            level.entities.add(level.finish);
            level.entities.add(level.squarecat);


        }
    }
    
}

