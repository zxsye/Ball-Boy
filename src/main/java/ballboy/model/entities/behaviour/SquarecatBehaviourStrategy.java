package ballboy.model.entities.behaviour;

import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.utilities.Vector2D;

/**
 * Strategy to move the squarecat in a square around the ballboy
 */
public class SquarecatBehaviourStrategy implements BehaviourStrategy {
    private static final double VELOCITY = 3;
    private final double radius;
    private double catX = 0;
    private double catY = 0;

    private final Level level;
    private SquarecatState state;

    public SquarecatBehaviourStrategy(Level level, double radius) {
        this.level = level;
        this.radius = radius;
        this.state = SquarecatState.A;
    }

    @Override
    public void behave(
            DynamicEntity entity,
            double frameDurationMilli) {

        // Set square cat position to be relative to top left corner
        entity.setPosition(new Vector2D(level.getHeroX(), level.getHeroY()));
        entity.setPosition(entity.getPosition().setX(
                entity.getPosition().getX() - radius - entity.getWidth()
        ));
        entity.setPosition(entity.getPosition().setY(
                entity.getPosition().getY() - radius - entity.getHeight()
        ));

        if (this.state == SquarecatState.A) {

            catX += VELOCITY;
            addPos(entity, catX, catY);

            if (entity.getPosition().isRightOf(
                    level.getHeroX() + level.getHeroWidth() + radius)) {
                this.state = state.next();

            }
        }

        else if (this.state == SquarecatState.B) {

            catY += VELOCITY;
            addPos(entity, catX, catY);

            if (entity.getPosition().isBelow(
                    level.getHeroY() + level.getHeroHeight() + radius)) {
                this.state = state.next();
            }
        }

        else if (this.state == SquarecatState.C) {

            catX -= VELOCITY;
            addPos(entity, catX, catY);

            if (entity.getPosition().isLeftOf(
                    level.getHeroX() - radius - entity.getWidth())) {
                this.state = state.next();

            }
        }

        else if (this.state == SquarecatState.D) {

            catY -= VELOCITY;
            addPos(entity, catX, catY);

            if (entity.getPosition().isAbove(
                    level.getHeroY() - radius - entity.getHeight()
            )) {
                this.state = state.next();

            }
        }
    }

    private void addPos(DynamicEntity entity, double x, double y) {
        entity.setPosition(new Vector2D(
                entity.getPosition().getX() + x,
                entity.getPosition().getY() + y));
    }

    @Override
    public SquarecatBehaviourStrategy copy() {
        return new SquarecatBehaviourStrategy(level, this.radius);
    }

}