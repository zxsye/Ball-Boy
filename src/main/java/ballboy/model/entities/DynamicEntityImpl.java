package ballboy.model.entities;

import ballboy.model.Entity;
import ballboy.model.entities.behaviour.BehaviourStrategy;
import ballboy.model.entities.collision.CollisionStrategy;
import ballboy.model.entities.utilities.*;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class DynamicEntityImpl extends DynamicEntity {
    private final CollisionStrategy collisionStrategy;
    private final BehaviourStrategy behaviourStrategy;
    private final AxisAlignedBoundingBox volume;
    private final Layer layer;
    private final Image image;
    private final KinematicState kinematicState;

    private List<EntityListener> listenerList;

    public DynamicEntityImpl(
            KinematicState kinematicState,
            AxisAlignedBoundingBox volume,
            Layer layer,
            Image image,
            CollisionStrategy collisionStrategy,
            BehaviourStrategy behaviourStrategy
    ) {
        this.kinematicState = kinematicState;
        this.volume = volume;
        this.layer = layer;
        this.image = image;
        this.collisionStrategy = collisionStrategy;
        this.behaviourStrategy = behaviourStrategy;

        this.listenerList = new ArrayList<>();
    }

    @Override
    public Image getImage() {
        return this.image;
    }

    @Override
    public Vector2D getPosition() {
        return kinematicState.getPosition();
    }

    @Override
    public void setPosition(Vector2D pos) {
        this.kinematicState.setPosition(pos);
    }

    @Override
    public Vector2D getPositionBeforeLastUpdate() {
        return this.kinematicState.getPreviousPosition();
    }

    @Override
    public Vector2D getVelocity() {
        return this.kinematicState.getVelocity();
    }

    @Override
    public void setVelocity(Vector2D vel) {
        this.kinematicState.setVelocity(vel);
    }

    @Override
    public double getHorizontalAcceleration() {
        return this.kinematicState.getHorizontalAcceleration();
    }

    @Override
    public void setHorizontalAcceleration(double horizontalAcceleration) {
        this.kinematicState.setHorizontalAcceleration(horizontalAcceleration);
    }

    @Override
    public double getHeight() {
        return volume.getHeight();
    }

    @Override
    public double getWidth() {
        return volume.getWidth();
    }

    @Override
    public Layer getLayer() {
        return this.layer;
    }

    @Override
    public boolean collidesWith(Entity entity) {
        return volume.collidesWith(entity.getVolume());
    }

    @Override
    public AxisAlignedBoundingBox getVolume() {
        return this.volume;
    }

    @Override
    public void collideWith(Entity entity) {
        collisionStrategy.collideWith(this, entity);
    }

    @Override
    public void update(
            double milliSeconds,
            double levelGravity) {
        kinematicState.update(milliSeconds, levelGravity);
//        System.out.println("behaving");
        behaviourStrategy.behave(this, milliSeconds);
        this.volume.setTopLeft(this.kinematicState.getPosition());
    }

    /**
     * Updates all listeners, informing them of colour and pointupdate
     * @param colour
     * @param pointUpdate
     */
    @Override
    public void notifyAllListeners(String colour, Integer pointUpdate) {
        for (EntityListener listener: this.listenerList) {
            listener.updateFromNotifier(colour, pointUpdate);
        }
    }

    /**
     * @return Version of this Entity as a EntityNotifier
     */
    @Override
    public EntityNotifier getNotifier() {
        return this;
    }

    @Override
    public void attach(EntityListener listener) {
        this.listenerList.add(listener);
    }

    @Override
    public void detach(EntityListener listener) {
        this.listenerList.remove(listener);
    }


    public DynamicEntity copy() {

        DynamicEntityImpl copy = new DynamicEntityImpl(
                this.kinematicState.copy(),
                this.volume.copy(),
                layer,
                new Image(image.getUrl()), // @TODO: not sure if this works
                this.collisionStrategy.copy(),
                this.behaviourStrategy.copy()
        );

        for (EntityListener listener : this.listenerList) {
            copy.attach(listener);
        }
        return copy;
    }
}
