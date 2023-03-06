import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;

public class ParticleFlow extends Application {
    private FXGraphics2D graphics;
    private final int GRID_WIDTH = 25;
    private final int GRID_HEIGHT = 12;
    private final int GRID_SIZE = 50;
    private Tile[] grid;
    private WaveFrontAlgorithm waveFrontAlgorithm = new WaveFrontAlgorithm(GRID_WIDTH, GRID_HEIGHT);

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(GRID_WIDTH * GRID_SIZE, GRID_HEIGHT * GRID_SIZE);
        stage.setScene(new Scene(new Group(canvas)));
        stage.setTitle("Particle Flow");
        stage.show();
        canvas.requestFocus();

        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        graphics.setBackground(Color.BLACK);
        graphics.setColor(Color.WHITE);
        graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());

        new AnimationTimer() {
            long last = -1;

            @Override
            public void handle(long now) {
                if (last == -1) last = now;
                double deltaTime = (now - last) / 1000000000.0;
                update(deltaTime);
                last = now;
                draw();
            }
        }.start();

        initGrid();

        canvas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R)
                initGrid();
        });

        canvas.setOnMouseClicked(this::changeGoalPosition);
        canvas.setOnMouseDragged(this::changeGoalPosition);
    }

    private void initGrid() {
        grid = new Tile[GRID_WIDTH * GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                // Create borders
                if (x == 0 || x == GRID_WIDTH - 1 || y == 0 || y == GRID_HEIGHT - 1
//                        || Math.random() > .6
                )
                    grid[x * GRID_HEIGHT + y] = new NonTraversableTile(new Point(x, y), GRID_SIZE);
                else
                    grid[x * GRID_HEIGHT + y] = new TraversableTile(new Point(x, y), GRID_SIZE);
            }
        }
    }

    private  void changeGoalPosition(MouseEvent mouseEvent) {
        Point goalPoint = new Point((int) (mouseEvent.getX() / GRID_SIZE), (int) (mouseEvent.getY() / GRID_SIZE));
        changeGoalPosition(goalPoint);
    }

    private void changeGoalPosition(Point goalPoint) {
        if (grid[goalPoint.x * GRID_HEIGHT + goalPoint.y].getDistance() == -1)
            return;

        waveFrontAlgorithm.updateGrid(grid, goalPoint);
    }

    private void draw() {
        graphics.clearRect(0, 0, GRID_WIDTH * GRID_SIZE, GRID_HEIGHT * GRID_SIZE);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (grid[x * GRID_HEIGHT + y] != null) grid[x * GRID_HEIGHT + y].draw(graphics);
            }
        }
    }

    private void update(double deltaTime) {

    }
}