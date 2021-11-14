package ballboy.model.factories;

import ballboy.model.Entity;
import ballboy.model.Level;
import org.json.simple.JSONObject;

/*
 * Generic factory for Entities.
 */
public interface EntityFactory {

    /*
     * Instantiates an entity configured by the provided JSON for the provided Level.
     */
    Entity createEntity(
            Level level,
            JSONObject entityConfig);
}
