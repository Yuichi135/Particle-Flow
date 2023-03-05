import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ParticleFlow extends Application {
    private FXGraphics2D graphics;
    private final int GRID_WIDTH = 50;
    private final int GRID_HEIGHT = 25;
    private final int GRID_SIZE = 25;
    private Obstacle[] obstacleGrid;

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(GRID_WIDTH * GRID_SIZE, GRID_HEIGHT * GRID_SIZE);
        stage.setScene(new Scene(new Group(canvas)));
        stage.setTitle("Particle Flow");
        stage.show();

        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        graphics.setBackground(Color.BLACK);
        graphics.setColor(Color.WHITE);
        graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());

        new AnimationTimer() {
            long last = -1;

            @Override
            public void handle(long now) {
                if (last == -1)
                    last = now;
                double deltaTime = (now - last) / 1000000000.0;
                update(deltaTime);
                last = now;
                draw();
            }
        }.start();

        initGrid();
    }

    private void initGrid() {
        obstacleGrid = new Obstacle[GRID_WIDTH * GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                // Create borders
                if (x == 0 || x == GRID_WIDTH - 1 || y == 0 || y == GRID_HEIGHT - 1)
                    obstacleGrid[x * GRID_HEIGHT + y] = new Obstacle(new Vector2D(x, y), GRID_SIZE);
            }
        }
    }

    private void draw() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (obstacleGrid[x * GRID_HEIGHT + y] != null)
                    obstacleGrid[x * GRID_HEIGHT + y].draw(graphics);
            }
        }
    }

    private void update(double deltaTime) {

    }
}