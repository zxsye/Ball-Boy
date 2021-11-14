package ballboy.view;

import ballboy.model.GameEngine;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class BlockedBackground implements BackgroundDrawer {
    private Rectangle sky;
    private Rectangle floor;
    private Pane pane;
    private GameEngine model;

    @Override
    public void draw(
            GameEngine model,
            Pane pane) {
        this.model = model;
        this.pane = pane;

        double width = pane.getWidth();
        double height = pane.getHeight();
        double floorHeight = model.getCurrentLevel().getFloorHeight();
        Color floorColor = model.getCurrentLevel().getFloorColor();

        sky = new Rectangle(0, 0, width, height);
        sky.setFill(Paint.valueOf("LIGHTBLUE"));
        sky.setViewOrder(1000.0);

        double levelheight = model.getCurrentLevel().getLevelHeight();
        floor = new Rectangle(0, floorHeight, width, levelheight - floorHeight);
        floor.setFill(floorColor);
        floor.setViewOrder(1000.0);

        pane.getChildren().addAll(sky, floor);
    }

    @Override
    public void update(
            double xViewportOffset,
            double yViewportOffset) {
        if (floor != null) {
            floor.setTranslateY(-yViewportOffset);
        }
    }
}
