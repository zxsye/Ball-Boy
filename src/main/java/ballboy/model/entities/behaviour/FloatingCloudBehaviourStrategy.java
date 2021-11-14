package ballboy.model.entities.behaviour;

import ballboy.model.Prototype;
import ballboy.model.entities.DynamicEntity;

/**
 * Applies an upwards acceleration to undo the effect of gravity, giving a floating effect.
 */
public class FloatingCloudBehaviourStrategy implements BehaviourStrategy {

    @Override
    public void behave(
            DynamicEntity cloud,
            double frameDurationMilli) {
        cloud.setPosition(cloud.getPosition().setY(cloud.getPositionBeforeLastUpdate().getY()));
        cloud.setVelocity(cloud.getVelocity().setY(0));
    }

    @Override
    public FloatingCloudBehaviourStrategy copy() {
        return new FloatingCloudBehaviourStrategy();
    }
}
