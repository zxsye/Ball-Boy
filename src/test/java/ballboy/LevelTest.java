package ballboy;

import ballboy.ConfigurationParseException;
import ballboy.ConfigurationParser;
import ballboy.model.*;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.StaticEntity;
import ballboy.model.entities.utilities.Vector2D;
import ballboy.model.factories.*;
import ballboy.model.levels.LevelImpl;
import ballboy.model.levels.PhysicsEngine;
import ballboy.model.levels.PhysicsEngineImpl;
import ballboy.view.GameWindow;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class LevelTest {

    EntityFactoryRegistry entityFactoryRegistry;
    PhysicsEngine engine;
    final double frameDurationMilli = 17;
    JSONObject parsedConfiguration = null;
    ConfigurationParser configuration;

    Integer indexOfStartLevel;
    JSONArray levelConfigs;

    List<Level> levelList;
    LevelImpl level;

    GameEngine ge;

    @BeforeAll
    public static void setupJavaFx() throws InterruptedException {
        Semaphore available = new Semaphore(0, true);
        Platform.startup(available::release);
        available.acquire();
    }

    @AfterAll
    public static void cleanupJavaFx() {
        Platform.exit();
    }

    @BeforeEach
    void genericSetup() {
        setup("config.json");
    }

    void setup(String configFileName) {
        configuration = new ConfigurationParser();

        try {
            parsedConfiguration = configuration.parseConfig(configFileName);
        } catch (ConfigurationParseException e) {
            System.out.println(e);
            System.exit(-1);
        }

        engine = new PhysicsEngineImpl(frameDurationMilli);

        entityFactoryRegistry = new EntityFactoryRegistry();
        entityFactoryRegistry.registerFactory("cloud", new CloudFactory());
        entityFactoryRegistry.registerFactory("enemy", new EnemyFactory());
        entityFactoryRegistry.registerFactory("background", new StaticEntityFactory(Entity.Layer.BACKGROUND));
        entityFactoryRegistry.registerFactory("static", new StaticEntityFactory(Entity.Layer.FOREGROUND));
        entityFactoryRegistry.registerFactory("finish", new FinishFactory());
        entityFactoryRegistry.registerFactory("hero", new BallboyFactory());
        entityFactoryRegistry.registerFactory("squarecat", new SquarecatFactory());

        indexOfStartLevel = ((Number) parsedConfiguration.get("currentLevelIndex")).intValue();
        levelConfigs = (JSONArray) parsedConfiguration.get("levels");

        // Create levels
        levelList = new ArrayList<>();
        for (int i = 0; i < levelConfigs.size(); i++) {
            levelList.add(
                    new LevelImpl((JSONObject) levelConfigs.get(i), engine, entityFactoryRegistry, frameDurationMilli)
            );
        }

        level = new LevelImpl((JSONObject) levelConfigs.get(0), engine, entityFactoryRegistry, frameDurationMilli);

        // Create game engine
        ge = new GameEngineImpl(levelList, 0);

    }


    @Test
    void badConfig() {
        assertThrows(ConfigurationParseException.class, ()->configuration.parseConfig(""));
        assertThrows(ConfigurationParseException.class, ()->configuration.parseConfig("tree.png"));
    }

    @Test
    void initLevelTest() {

        assertEquals(level.getEntities().size(), 17);
        assertEquals(level.getEntities().size(), 17);
        assertFalse(level.isLevelFinish());
        assertEquals(level.getTotalScore(), 0);
        assertEquals("RED  0\nBLUE  0\nGREEN  0\n",
                level.getScoreString());
        assertEquals(620, level.getLevelHeight());
        assertEquals(Color.web("#001100"), level.getFloorColor());
    }

    @Test
    void resetHero() {

        double y = level.getHeroY();

        for (int i = 0; i < 100; i++) {
            level.update();
        }

        assertNotEquals(y, level.getHeroY());
        level.resetHero();
        level.update();
        assertEquals(y, level.getHeroY());

    }

    // Factory testing
    @Test
    void FactoryTest() {
        // Make generic entity
        JSONObject mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "enemy");
        mockAggJSON.put("startX", 10.0);
        mockAggJSON.put("startY", 550.1);
        mockAggJSON.put("startVelocityX", -10.0);
        mockAggJSON.put("height", 20.0);
        mockAggJSON.put("image", "slimeGa.png");
        mockAggJSON.put("behaviour", "aggressive");

        Entity ent = entityFactoryRegistry.createEntity(level, mockAggJSON);
        assertEquals(ent.getHeight(), 20.0);
        assertEquals(ent.getPosition().getX(), 10.0);
        assertEquals(ent.getPosition().getY(), 550.1);

        // Make Hero
        mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "hero");
        mockAggJSON.put("startX", 10.0);
        mockAggJSON.put("startY", 550.1);
        mockAggJSON.put("size", "small");
        mockAggJSON.put("image", "ch_stand2.png");

        ent = entityFactoryRegistry.createEntity(level, mockAggJSON);
        assertEquals(ent.getHeight(), 10.0);
        assertEquals(ent.getPosition().getX(), 10.0);
        assertEquals(ent.getPosition().getY(), 550.1);

        // Make Hero
        mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "hero");
        mockAggJSON.put("startX", 10.0);
        mockAggJSON.put("startY", 550.1);
        mockAggJSON.put("size", "medium");
        mockAggJSON.put("image", "ch_stand2.png");

        ent = entityFactoryRegistry.createEntity(level, mockAggJSON);
        assertEquals(ent.getHeight(), 25.0);
        assertEquals(ent.getPosition().getX(), 10.0);
        assertEquals(ent.getPosition().getY(), 550.1);

    }

    @Test
    void BadFactoryTest() {
        JSONObject mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "FAKETYPE");
        mockAggJSON.put("startX", 10.0);
        mockAggJSON.put("startY", 550.1);
        mockAggJSON.put("startVelocityX", -10.0);
        mockAggJSON.put("height", 20.0);
        mockAggJSON.put("image", "slimeGa.png");
        mockAggJSON.put("behaviour", "aggressive");

        assertThrows(IllegalStateException.class,
                () -> entityFactoryRegistry.createEntity(level, mockAggJSON));

        // Bad hero size
        JSONObject mockAggJSON2 = new JSONObject();
        mockAggJSON2.put("type", "hero");
        mockAggJSON2.put("startX", 10.0);
        mockAggJSON2.put("startY", 550.1);
        mockAggJSON2.put("size", "ASDF");
        mockAggJSON2.put("image", "ch_stand2.png");

        assertThrows(ConfigurationParseException.class,
                () -> entityFactoryRegistry.createEntity(level, mockAggJSON2));

        // Bad hero image
        JSONObject mockAggJSON3 = new JSONObject();
        mockAggJSON3.put("type", "hero");
        mockAggJSON3.put("startX", 10.0);
        mockAggJSON3.put("startY", 550.1);
        mockAggJSON3.put("size", "small");
        mockAggJSON3.put("image", "asdf.png");

        assertThrows(ConfigurationParseException.class,
                () -> entityFactoryRegistry.createEntity(level, mockAggJSON3));

        // BAD enemy image
        JSONObject mockAggJSON4 = new JSONObject();
        mockAggJSON4.put("type", "enemy");
        mockAggJSON4.put("startX", 10.0);
        mockAggJSON4.put("startY", 550.1);
        mockAggJSON4.put("startVelocityX", -10.0);
        mockAggJSON4.put("height", 20.0);
        mockAggJSON4.put("image", "asdf.png");
        mockAggJSON4.put("behaviour", "aggressive");

        assertThrows(ConfigurationParseException.class,
                () -> entityFactoryRegistry.createEntity(level, mockAggJSON4));

        // Bad squarecat image
        JSONObject mockAggJSON5 = new JSONObject();
        mockAggJSON5.put("type", "squarecat");
        mockAggJSON5.put("startX", 10.0);
        mockAggJSON5.put("startY", 550.1);
        mockAggJSON5.put("startVelocityX", -10.0);
        mockAggJSON5.put("height", 20.0);
        mockAggJSON5.put("image", "asdf.png");
        mockAggJSON5.put("behaviour", "aggressive");

        assertThrows(ConfigurationParseException.class,
                () -> entityFactoryRegistry.createEntity(level, mockAggJSON5));


    }

    // Behaviour testing
    @Test
    void aggressiveEnemyBehaviourStrategyTest() {
        JSONObject mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "enemy");
        mockAggJSON.put("startX", 10.0);
        mockAggJSON.put("startY", 550.1);
        mockAggJSON.put("startVelocityX", -10.0);
        mockAggJSON.put("height", 20.0);
        mockAggJSON.put("image", "slimeGa.png");
        mockAggJSON.put("behaviour", "aggressive");

        assertTrue(entityFactoryRegistry.createEntity(level, mockAggJSON) instanceof DynamicEntity);
        DynamicEntity ent = (DynamicEntity) entityFactoryRegistry.createEntity(level, mockAggJSON);
        Vector2D currentPos = ent.getPosition();
        for (int i = 0; i < 100; i++) {
            ent.update(frameDurationMilli, level.getGravity());
        }
        assertTrue(currentPos.isLeftOf(ent.getPosition().getX()));
    }

    @Test
    void floatingCloudBehaviourStrategyTest() {
        JSONObject mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "cloud");
        mockAggJSON.put("startX", 234.0);
        mockAggJSON.put("startY", 0.1);
        mockAggJSON.put("horizontalVelocity", -20.2);
        mockAggJSON.put("image", "cloud_1.png");

        assertTrue(entityFactoryRegistry.createEntity(level, mockAggJSON) instanceof DynamicEntity);
        DynamicEntity ent = (DynamicEntity) entityFactoryRegistry.createEntity(level, mockAggJSON);
        Vector2D currentPos = ent.getPosition();
        for (int i = 0; i < 100; i++) {
            ent.update(frameDurationMilli, level.getGravity());
        }
        assertTrue(currentPos.isRightOf(ent.getPosition().getX()));

        // Cloud 2
        JSONObject mockAggJSON2 = new JSONObject();
        mockAggJSON2.put("type", "cloud");
        mockAggJSON2.put("startX", 234.0);
        mockAggJSON2.put("startY", 0.1);
        mockAggJSON2.put("horizontalVelocity", 20.2);
        mockAggJSON2.put("image", "cloud_1.png");

        assertTrue(entityFactoryRegistry.createEntity(level, mockAggJSON2) instanceof DynamicEntity);
        ent = (DynamicEntity) entityFactoryRegistry.createEntity(level, mockAggJSON2);
        currentPos = ent.getPosition();
        for (int i = 0; i < 100; i++) {
            ent.update(frameDurationMilli, level.getGravity());
        }
        assertTrue(currentPos.isLeftOf(ent.getPosition().getX()));
    }

    @Test
    void passiveEntityBehaviourStrategyTest() {
        JSONObject mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "enemy");
        mockAggJSON.put("startX", 10.0);
        mockAggJSON.put("startY", 550.1);
        mockAggJSON.put("startVelocityX", 0);
        mockAggJSON.put("height", 20.0);
        mockAggJSON.put("image", "slimeGa.png");
        mockAggJSON.put("behaviour", "passive");

        assertTrue(entityFactoryRegistry.createEntity(level, mockAggJSON) instanceof DynamicEntity);
        DynamicEntity ent = (DynamicEntity) entityFactoryRegistry.createEntity(level, mockAggJSON);
        Vector2D currentPos = ent.getPosition();
        for (int i = 0; i < 100; i++) {
            ent.update(frameDurationMilli, 0);
        }
        assertEquals(currentPos.getX(), ent.getPosition().getX());
        assertEquals(currentPos.getY(), ent.getPosition().getY());
    }

    @Test
    void scaredEnemyBehaviourStrategyTest() {
        JSONObject mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "enemy");
        mockAggJSON.put("startX", 100.0);
        mockAggJSON.put("startY", 550.1);
        mockAggJSON.put("startVelocityX", 0);
        mockAggJSON.put("height", 20.0);
        mockAggJSON.put("image", "slimeGa.png");
        mockAggJSON.put("behaviour", "scared");

        assertTrue(entityFactoryRegistry.createEntity(level, mockAggJSON) instanceof DynamicEntity);
        DynamicEntity ent = (DynamicEntity) entityFactoryRegistry.createEntity(level, mockAggJSON);
        Vector2D oldPos = ent.getPosition();
        for (int i = 0; i < 50; i++) {
            ent.update(frameDurationMilli, level.getGravity());
        }
        assertTrue(oldPos.isRightOf(ent.getPosition().getX()));

    }

    @Test
    void squarecatBehaviourStrategyTest() {
        JSONObject mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "squarecat");
        mockAggJSON.put("heroX", 150.0);
        mockAggJSON.put("heroY", 300.0);
        mockAggJSON.put("height", 20.0);
        mockAggJSON.put("image", "squarecat.png");

        assertTrue(entityFactoryRegistry.createEntity(level, mockAggJSON) instanceof DynamicEntity);
        DynamicEntity ent = (DynamicEntity) entityFactoryRegistry.createEntity(level, mockAggJSON);
        Vector2D oldPos = ent.getPosition();

        // Move top left -> top right
        for (int i = 0; i < 50; i++) {
            ent.update(frameDurationMilli, level.getGravity());
        }
        assertTrue(oldPos.isLeftOf(ent.getPosition().getX()));

        // Move top right to bottom right
        for (int i = 0; i < 100; i++) {
            ent.update(frameDurationMilli, level.getGravity());
        }
        assertTrue(oldPos.isAbove(ent.getPosition().getY()));

        // Move bottom right -> bottom left
        for (int i = 0; i < 100; i++) {
            ent.update(frameDurationMilli, level.getGravity());
        }
        assertTrue(oldPos.isLeftOf(ent.getPosition().getX()));

        // Move bottom left -> top left
        for (int i = 0; i < 50; i++) {
            ent.update(frameDurationMilli, level.getGravity());
        }
        assertTrue(oldPos.isAbove(ent.getPosition().getY()));
    }

    @Test
    void badBehaviourConfig() {
        // BAD BEHAVIOUR
        JSONObject mockAggJSON = new JSONObject();
        mockAggJSON.put("type", "enemy");
        mockAggJSON.put("startX", 10.0);
        mockAggJSON.put("startY", 550.1);
        mockAggJSON.put("startVelocityX", -10.0);
        mockAggJSON.put("height", 20.0);
        mockAggJSON.put("image", "slimeGa.png");
        mockAggJSON.put("behaviour", "ASDF");

        assertThrows(ConfigurationParseException.class, () -> entityFactoryRegistry.createEntity(level, mockAggJSON));
    }

    @Test
    void squarecatKillEnemy() {
        int numOfEntities = level.getEntities().size();

        DynamicEntity squarecat = null;
        DynamicEntity enemy = null;
        for (Entity e : level.getEntities()) {
            if (level.isSquarecat(e)) {
                squarecat = (DynamicEntity) e;
            } else if (!level.isFinish(e) && !level.isHero(e) && e instanceof DynamicEntity) {
                enemy = (DynamicEntity) e;
            }

        }
        assertNotNull(squarecat);
        assertNotNull(enemy);
        assertEquals("RED  0\nBLUE  0\nGREEN  0\n", level.getScoreString());

        enemy.collideWith(squarecat);

        level.update();

        assertEquals(numOfEntities - 1, level.getEntities().size());

        // TEST SCORE UPDATE
        assertEquals("RED  100\nBLUE  0\nGREEN  0\n", level.getScoreString());
    }


    // Integration test: Level update()
    @Test
    void levelHeroGravity() {
        setup("configHeroBounce.json");

        // Hero gravity
        double heroYBeforeFalling = level.getHeroY();
        for (int i = 0; i < 10; i++) {
            level.update();
        }
        double heroYAfterFalling = level.getHeroY();
        assertTrue(heroYAfterFalling > heroYBeforeFalling);
    }

    // Integration test: Level update()
    @Test
    void levelHeroBounceOffRock() {
        setup("configHeroBounce.json");

        // Make hero fall and bounce off rock
        for (int i = 0; i < 1000; i++) {
            level.update();
        }
        // After bounce
        double heroYInitial = level.getHeroY();

        for (int i = 0; i < 10; i++) {
            level.update();
        }
        double heroYFinal = level.getHeroY();
        assertTrue(heroYFinal < heroYInitial);
    }


    /**
     * Test for physics engine
     */
    @Test
    void physicsEngineEnemyEnemyCollision() {
        setup("configEnemyEnemyCollide.json");

        // Hero falls into enemy below
        Entity a = level.getEntities().get(0);
        Entity b = level.getEntities().get(1);
        assertTrue(a instanceof DynamicEntity);
        assertTrue(b instanceof DynamicEntity);

        DynamicEntity enemyMovingLeft = (DynamicEntity) a;
        DynamicEntity enemyMovingRight = (DynamicEntity) b;

        assertTrue( enemyMovingLeft.getVelocity().getX() < 0 );
        assertTrue( enemyMovingRight.getVelocity().getX() > 0 );

        for (int i = 0; i < 300; i++) {
            level.update();
        }

        // After colliding: should be moving in reverse direction
        assertTrue( enemyMovingLeft.getVelocity().getX() > 0 );
        assertTrue( enemyMovingRight.getVelocity().getX() < 0 );


        // ========== VERTICAL COLLISION ============= //

        setup("configEnemyEnemyCollideVertical.json");

        // Hero falls into enemy below
        a = level.getEntities().get(0);
        b = level.getEntities().get(1);
        assertTrue(a instanceof DynamicEntity);
        assertTrue(b instanceof DynamicEntity);

        DynamicEntity enemyOnTop = (DynamicEntity) a;
        DynamicEntity enemyOnBottom = (DynamicEntity) b;

        level.update();
        level.update();
        level.update();

        // Both are moving down at the start
        assertTrue( enemyOnTop.getVelocity().getY() > 0 );
        assertTrue( enemyOnBottom.getVelocity().getY() > 0 );

        for (int i = 0; i < 35; i++) {
            level.update();


        }

        // After colliding: should be moving in reverse direction
        assertTrue( enemyOnTop.getVelocity().getY() < 0 );
        assertTrue( enemyOnBottom.getVelocity().getY() > 0 );
    }

    @Test
    void physicsEngineStaticCollision() {
        // ========== HORIZONTAL COLLISION FROM Left ============= //

        setup("configHorizontalCollideLeft.json");

        DynamicEntity enemy = null;
        StaticEntity rock = null;
        // Get the enemy and rock
        for (Entity e : level.getEntities()) {
            if (!level.isHero(e) && !level.isSquarecat(e) && !level.isFinish(e)) {
                if (e instanceof DynamicEntity) {
                    enemy = (DynamicEntity) e;
                } else if (e instanceof StaticEntity) {
                    rock = (StaticEntity) e;
                }
            }
        }
        assertNotNull(enemy);
        assertNotNull(rock);

        level.update();

        // Both are moving down at the start
        assertTrue( enemy.getVelocity().getX() > 0 );

        for (int i = 0; i < 100; i++) {
            level.update();
        }

        // After colliding: should be moving in reverse direction
        assertTrue( enemy.getVelocity().getX() < 0 );


        // ========== HORIZONTAL COLLISION FROM Right ============= //

        setup("configHorizontalCollideRight.json");

        enemy = null;
        rock = null;
        // Get the enemy and rock
        for (Entity e : level.getEntities()) {
            if (!level.isHero(e) && !level.isSquarecat(e) && !level.isFinish(e)) {
                if (e instanceof DynamicEntity) {
                    enemy = (DynamicEntity) e;
                } else if (e instanceof StaticEntity) {
                    rock = (StaticEntity) e;
                }
            }
        }
        assertNotNull(enemy);
        assertNotNull(rock);

        level.update();

        // Both are moving down at the start
        assertTrue( enemy.getVelocity().getX() < 0 );

        for (int i = 0; i < 200; i++) {
            level.update();

        }

        // After colliding: should be moving in reverse direction
        assertTrue( enemy.getVelocity().getX() > 0 );
    }


    // Features

    @Test
    void moveLeftRight() {

        // Save current velocity
        DynamicEntity hero = null;
        for (Entity e : level.getEntities()) {
            if (level.isHero(e)) {
                hero = (DynamicEntity) e;
            }
        }
        assertNotNull(hero);

        // Move left
        level.moveLeft();
        assertTrue(hero.getHorizontalAcceleration() < 0);
        // Move right
        level.moveRight();
        assertTrue(hero.getHorizontalAcceleration() > 0);
    }

    @Test
    void boostHeight() {

        // Save current velocity
        DynamicEntity hero = null;
        for (Entity e : level.getEntities()) {
            if (level.isHero(e)) {
                hero = (DynamicEntity) e;
            }
        }
        assertNotNull(hero);
        level.update();
        double oldYVelocity = hero.getVelocity().getY();

        // Move left
        assertTrue(level.boostHeight());
        assertTrue(hero.getVelocity().getY() > oldYVelocity);
    }


    /* ========== GAME ENGINE =========== */

    @Test
    void finishCurrentLevel() {

        assertEquals(levelList.get(0), ge.getCurrentLevel());
        ge.finishCurrentLevel();
        assertEquals(levelList.get(1), ge.getCurrentLevel());
    }

    /**
     * Test for functionality already well-tested in Level tests
     */
    @Test
    void trivialChecks() {

        assertTrue(ge.boostHeight());
        assertTrue(ge.moveLeft());
        assertTrue(ge.moveRight());

        ge.tick();
        assertNotEquals(300.0, ge.getCurrentLevel().getHeroY());
    }

    @Test
    void gameEngineFinishLevel() {
        assertEquals(levelList.get(0), ge.getCurrentLevel());
        ge.getCurrentLevel().finish();
        ge.tick();

        assertEquals(levelList.get(1), ge.getCurrentLevel());

    }

    @Test
    void totalScore() {
        assertEquals("Total Score: 0\n", ge.getTotalScoreAsString());

        DynamicEntity squarecat = null;
        DynamicEntity enemy = null;

        Level level = ge.getCurrentLevel();
        for (Entity e : level.getEntities()) {
            if (level.isSquarecat(e)) {
                squarecat = (DynamicEntity) e;
            } else if (!level.isFinish(e) && !level.isHero(e) && e instanceof DynamicEntity) {
                enemy = (DynamicEntity) e;
            }
        }
        enemy.collideWith(squarecat);
        ge.tick();

        ge.getCurrentLevel().finish();
        ge.tick();

        // Check total score after completing level
        assertEquals("Total Score: 100\n", ge.getTotalScoreAsString());
    }

    @Test
    @Order(1)
    void savingGame() {
        setup("configSimple.json");
        double heroY = ge.getCurrentLevel().getHeroY();

        Memento memento = ge.saveGameState();
        for (int i = 0; i < 100; i++) {
            ge.tick();
        }
        assertNotEquals(heroY, ge.getCurrentLevel().getHeroY());

        memento.restore();
        assertEquals(heroY, ge.getCurrentLevel().getHeroY());
    }


    /**
     * Check is GameWindow can be successfully opened and run
     */
    @Test
    void GameWindow() {
        GameCaretaker gct = new GameCaretaker(ge);
        GameWindow gw = new GameWindow(ge, gct, 400,400, frameDurationMilli);
        assertDoesNotThrow(()->gw.run());
    }
}
