package ballboy.model.entities.utilities;

import java.util.Map;

/**
 * An Entity class should implement this interface. Notifies listeners of
 * any updates by calling the EntityListener methods.
 */
public interface EntityNotifier {

    void notifyAllListeners(String colour, Integer pointUpdate);

    void attach(EntityListener listener);

    void detach(EntityListener listener);

}
