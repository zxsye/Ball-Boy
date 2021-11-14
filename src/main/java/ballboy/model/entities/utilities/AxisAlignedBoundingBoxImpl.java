package ballboy.model.entities.utilities;

import ballboy.model.Prototype;

/**
 * Standard implementation of an AxisAlignedBoundingBox, with O(1) time
 * collidesWith and containsPoint implementations.
 */
public class AxisAlignedBoundingBoxImpl implements AxisAlignedBoundingBox, Prototype {
    private final double width;
    private final double height;
    private Vector2D topLeft;

    public AxisAlignedBoundingBoxImpl(
            Vector2D topLeft,
            double height,
            double width) {
        this.width = width;
        this.height = height;
        this.topLeft = topLeft;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getLeftX() {
        return topLeft.getX();
    }

    @Override
    public double getRightX() {
        return topLeft.getX() + width;
    }

    @Override
    public double getTopY() {
        return topLeft.getY();
    }

    @Override
    public double getBottomY() {
        return topLeft.getY() + height;
    }

    @Override
    public void setTopLeft(Vector2D topLeft) {
        this.topLeft = topLeft;
    }

    @Override
    public boolean collidesWith(AxisAlignedBoundingBox box) {
        return getLeftX() < box.getRightX() &&
                getRightX() > box.getLeftX() &&
                getBottomY() > box.getTopY() &&
                getTopY() < box.getBottomY();
    }

    @Override
    public boolean containsPoint(Vector2D point) {
        return point.isRightOf(getLeftX()) &&
                point.isLeftOf(getRightX()) &&
                point.isAbove(getBottomY()) &&
                point.isBelow(getTopY());
    }

    @Override
    public AxisAlignedBoundingBoxImpl copy() {
        return new AxisAlignedBoundingBoxImpl(
                topLeft.copy(),
                height,
                width
        );
    }
}