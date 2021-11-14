package ballboy.model.factories;

import ballboy.model.Entity;
import ballboy.model.Level;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 * Composite Entity factory that delegates to the appropriate registered concrete factory.
 */
public class EntityFactoryRegistry implements EntityFactory {

    private final Map<String, EntityFactory> factoryRegistry = new HashMap<>();

    /*
     * Registers a concrete factory delegate for the specified type.
     */
    public void registerFactory(
            String entityType,
            EntityFactory factory) {
        if (factoryRegistry.containsKey(entityType)) {
            throw new IllegalStateException(
                    String.format("duplicate registration of factory for entity type %s\n", entityType));
        }

        factoryRegistry.put(entityType, factory);
    }

    @Override
    public Entity createEntity(
            Level level,
            JSONObject config) {

        String type = (String) config.get("type");

        if (!factoryRegistry.containsKey(type)) {
            throw new IllegalStateException(String.format("no factory registered for entity type %s\n", type));
        }

        EntityFactory factory = factoryRegistry.get(type);
        return factory.createEntity(level, config);
    }
}



