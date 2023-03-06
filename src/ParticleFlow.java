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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

public class ParticleFlow extends Application {
    private FXGraphics2D graphics;
    private final int GRID_WIDTH = 25;
    private final int GRID_HEIGHT = 12;
    private final int GRID_SIZE = 50;
    private Tile[] grid;
    private WaveFrontAlgorithm waveFrontAlgorithm = new WaveFrontAlgorithm(GRID_WIDTH, GRID_HEIGHT);
    private ArrayList<Particle> particles = new ArrayList<>();
    private Point goalPoint = new Point();

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
            if (event.getCode() == KeyCode.R) {
                initGrid();
                if (event.isShiftDown())
                    particles.clear();
            } else if (event.getCode() == KeyCode.SPACE)
                createParticles(100);
        });

        canvas.setOnMousePressed(this::mouseEvent);
        canvas.setOnMouseDragged(this::mouseEvent);
    }

    private void createParticles(int amount) {
        while (amount >= 0) {
            double x = Math.random() * (GRID_WIDTH - 3) + 1.5;
            double y = Math.random() * (GRID_HEIGHT - 3) + 1.5;
            if (grid[(int) (x * GRID_HEIGHT + y)] instanceof TraversableTile) {
                particles.add(new Particle(new Vector2D(x * GRID_SIZE, y * GRID_SIZE), 10, Color.YELLOW, AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f)));
                amount--;
            }
        }
    }

    private void initGrid() {
        grid = new Tile[GRID_WIDTH * GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                // Create borders
                if (x == 0 || x == GRID_WIDTH - 1 || y == 0 || y == GRID_HEIGHT - 1
                        || Math.random() > .80
                )
                    grid[x * GRID_HEIGHT + y] = new NonTraversableTile(new Point(x, y), GRID_SIZE);
                else
                    grid[x * GRID_HEIGHT + y] = new TraversableTile(new Point(x, y), GRID_SIZE);
            }
        }
    }

    private void mouseEvent(MouseEvent e) {
        if (e.isShiftDown()) {
            Point mouseLocation = new Point((int) (e.getX() / GRID_SIZE), (int) (e.getY() / GRID_SIZE));
            if (e.isPrimaryButtonDown())
                placeTile(mouseLocation);
            else if (e.isSecondaryButtonDown())
                removeTile(mouseLocation);

        } else {
            changeGoalPosition(e);
        }
    }

    private void placeTile(Point location) {
        grid[location.x * GRID_HEIGHT + location.y] = new NonTraversableTile(location, GRID_SIZE);
        waveFrontAlgorithm.updateGrid(grid, goalPoint);
    }

    private void removeTile(Point location) {
        grid[location.x * GRID_HEIGHT + location.y] = new TraversableTile(location, GRID_SIZE);
        waveFrontAlgorithm.updateGrid(grid, goalPoint);
    }

    private void changeGoalPosition(MouseEvent mouseEvent) {
        goalPoint = new Point((int) (mouseEvent.getX() / GRID_SIZE), (int) (mouseEvent.getY() / GRID_SIZE));
        changeGoalPosition(goalPoint);
    }

    private void changeGoalPosition(Point goalPoint) {
        if (waveFrontAlgorithm.isOutOfBounds(goalPoint.x, goalPoint.y))
            return;
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

        graphics.setColor(Color.BLACK);
        graphics.drawString("Particles:\t" + particles.size(), 0, graphics.getFont().getSize() * 2);
        graphics.setColor(Color.WHITE);
        for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext(); ) {
            Particle particle = iterator.next();
            particle.draw(graphics);
        }
    }

    private void update(double deltaTime) {
        applyForce();
        for (Particle particle : particles) {
            particle.update(deltaTime);
        }
        solveCollisions();
    }

    private void applyForce() {
        for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext(); ) {
            Particle particle = iterator.next();
            int x = (int) Math.floor(particle.getPosition().getX() / GRID_SIZE);
            int y = (int) Math.floor(particle.getPosition().getY() / GRID_SIZE);

            if (waveFrontAlgorithm.isOutOfBounds(x, y)) {
                iterator.remove();
                continue;
            }

            particle.applyForce(grid[x * GRID_HEIGHT + y].getDirectionVector());
            particle.applyForce(new Vector2D(Math.random() * 2 - 1, Math.random() * 2 - 1));
        }
    }

    private void solveCollisions() {
        for (Particle particle : particles) {
            for (Tile tile : grid) {
                if (tile instanceof NonTraversableTile) {
                    Rectangle2D tileRec = tile.getShape().getBounds2D();
                    Rectangle2D particleRec = particle.getShape().getBounds2D();

                    // Collision LEFT SIDE
                    if (tileRec.contains(particleRec.getX(), particleRec.getCenterY())) {
//                        particle.movePosition(new Vector2D(tileRec.getX() + tileRec.getWidth() + particleRec.getWidth() / 2, particleRec.getCenterY()));
                        particle.setPositionX(tileRec.getX() + tileRec.getWidth() + particleRec.getWidth() / 2);
                    }
                    // Collision RIGHT SIDE
                    if (tileRec.contains(particleRec.getX() + particleRec.getWidth(), particleRec.getCenterY())) {
//                        particle.movePosition(new Vector2D(tileRec.getX() - particleRec.getWidth() / 2, particleRec.getCenterY()));
                        particle.setPositionX(tileRec.getX() - particleRec.getWidth() / 2);
                    }
                    // Collision TOP
                    if (tileRec.contains(particleRec.getCenterX(), particleRec.getY())) {
//                        particle.movePosition(new Vector2D(particleRec.getCenterX(), tileRec.getY() + tileRec.getHeight() + particleRec.getHeight()/2));
                        particle.setPositionY(tileRec.getY() + tileRec.getHeight() + particleRec.getHeight() / 2);
                    }
                    // Collision BOTTOM
                    if (tileRec.contains(particleRec.getCenterX(), particleRec.getY() + particleRec.getHeight())) {
//                        particle.movePosition(new Vector2D(particleRec.getCenterX(), tileRec.getY() - particleRec.getHeight()/2));
                        particle.setPositionY(tileRec.getY() - particleRec.getHeight() / 2);
                    }
                }
            }
        }
    }
}