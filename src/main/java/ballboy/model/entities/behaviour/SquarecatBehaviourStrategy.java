package ballboy.model.entities.behaviour;

import ballboy.model.Level;
import ballboy.model.Prototype;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.utilities.Vector2D;

/**
 * An aggressive strategy that makes the entity follow the ballboy.
 * Acceleration is applied horizontally in the direction of the ballboy
 * until a maximum velocity is reached.
 */
public class SquarecatBehaviourStrategy implements BehaviourStrategy {
    private static final double VELOCITY = 3;
    private double radius;
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

        // Undo gravity
//        entity.setPosition(entity.getPosition().setY(
//                entity.getPositionBeforeLastUpdate().getY()));
        // Remove any other automatic movement
//        entity.setVelocity(entity.getVelocity().setY(0));
//        entity.setVelocity(entity.getVelocity().setX(0));

        // Set square cat position to be relative to top left corner
        entity.setPosition(new Vector2D(level.getHeroX(), level.getHeroY()));
        entity.setPosition(entity.getPosition().setX(
                entity.getPosition().getX() - radius - entity.getWidth()
        ));
        entity.setPosition(entity.getPosition().setY(
                entity.getPosition().getY() - radius - entity.getHeight()
        ));
//        System.out.println("MY STATE IS");

//        switch (this.state) {
//            case A:
        if (this.state == SquarecatState.A) {
//            System.out.println("A");
            catX += VELOCITY;
            addPos(entity, catX, catY);
//            entity.setPosition(entity.getPosition().setX(
//                    entity.getPosition().getX() + catX
//            ));
//            System.out.printf("== REL x %f\n", entity.getPosition().getX() - level.getHeroX());

            if (entity.getPosition().isRightOf(
                    level.getHeroX() + level.getHeroWidth() + radius)) {
//                System.out.printf("SC_x: %f = Hero_x: %f", entity.getPosition().getX(), level.getHeroX());
                this.state = state.next();
//                System.out.println("going to B");
            }
        }
//            case B:
        else if (this.state == SquarecatState.B) {
//            System.out.println("B");
            catY += VELOCITY;
            addPos(entity, catX, catY);
//            entity.setPosition(entity.getPosition().setY(
//                    entity.getPosition().getY() + catY
//            ));

            if (entity.getPosition().isBelow(
                    level.getHeroY() + level.getHeroHeight() + radius)) {
                this.state = state.next();
//                System.out.println("going to C");
            }
        }
//            case C:
        else if (this.state == SquarecatState.C) {
//            System.out.println("C");
            catX -= VELOCITY;
            addPos(entity, catX, catY);
//            entity.setPosition(entity.getPosition().setX(
//                    entity.getPosition().getX() + catX;
//            ));

            if (entity.getPosition().isLeftOf(
                    level.getHeroX() - radius - entity.getWidth())) {
                this.state = state.next();
//                System.out.println("going to D");
            }
        }
//            case D:
        else if (this.state == SquarecatState.D) {
//            System.out.println("Dee");
            catY -= VELOCITY;
            addPos(entity, catX, catY);
//            entity.setPosition(entity.getPosition().setY(
//                    entity.getPosition().getY() - VELOCITY
//            ));

            if (entity.getPosition().isAbove(
                    level.getHeroY() - radius - entity.getHeight()
            )) {
                this.state = state.next();
//                System.out.println("going to A");
            }
        }

//        System.out.printf("relative x %f", entity.getPosition().getX() - level.getHeroX());
//        System.out.printf("relative y %f", entity.getPosition().getY() - level.getHeroY());


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