import com.sun.xml.internal.ws.addressing.WsaTubeHelperImpl;
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
    private final int GRID_WIDTH = 100;
    private final int GRID_HEIGHT = 50;
    private final int GRID_SIZE = 12;
    private Tile[] grid;
    private WaveFrontAlgorithm waveFrontAlgorithm = new WaveFrontAlgorithm(GRID_WIDTH, GRID_HEIGHT);
    private ArrayList<Particle> particles = new ArrayList<>();
    private Point goalPoint = new Point();
    private int strokeWidth = 1;
    private int collisionChecks;
    private long drawTime;
    private long updateTime;
    private int debugMode = 0;

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
                draw(deltaTime);
            }
        }.start();

        initGrid();

        canvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case C:
                    particles.clear();
                    break;
                case R:
                    initGrid();
                    break;
                case SPACE:
                    createParticles(100);
                    break;
                case UP:
                    if (strokeWidth < 5)
                        strokeWidth++;
                    break;
                case DOWN:
                    if (strokeWidth > 1)
                        strokeWidth--;
                    break;
                case Q:
                    debugMode ^= 0b001;
                    break;
                case W:
                    debugMode ^= 0b010;
                    break;
                case E:
                    debugMode ^= 0b100;
                    break;
            }
        });

        canvas.setOnMousePressed(this::mouseEvent);
        canvas.setOnMouseDragged(this::mouseEvent);
    }

    private void createParticles(int amount) {
        while (amount > 0) {
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
//                        || Math.random() > .80
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
        for (int x = 0; x < strokeWidth; x++) {
            for (int y = 0; y < strokeWidth; y++) {
                if (!waveFrontAlgorithm.isOutOfBounds(location.x + x, location.y + y))
                    grid[(location.x + x) * GRID_HEIGHT + location.y + y] = new NonTraversableTile(new Point(location.x + x, location.y + y), GRID_SIZE);
            }
        }
        waveFrontAlgorithm.updateGrid(grid, goalPoint);
    }

    private void removeTile(Point location) {
        for (int x = 0; x < strokeWidth; x++) {
            for (int y = 0; y < strokeWidth; y++) {
                if (!waveFrontAlgorithm.isOutOfBounds(location.x + x, location.y + y))
                    grid[(location.x + x) * GRID_HEIGHT + location.y + y] = new TraversableTile(new Point(location.x + x, location.y + y), GRID_SIZE);
            }
        }
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

    private void drawOverlay(double deltaTime) {
        int fontSize = graphics.getFont().getSize();
        graphics.setColor(Color.GRAY);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        graphics.fill(new Rectangle2D.Double(0, 0, 175, fontSize * 10));

        StringBuilder sb = new StringBuilder();
        sb.append("FPS:\t\t\t\t" + Math.round(1 / deltaTime * 10) / 10.0 + "\n");
        sb.append("DrawTime:\t\t" + Math.round(drawTime * 10) / 10.0 + "ms\n");
        sb.append("UpdateTime:\t\t" + Math.round(updateTime * 10) / 10.0 + "ms\n");
        sb.append("Particles:\t\t\t" + particles.size() + "\n");
        sb.append("CollisionChecks:\t" + collisionChecks + "\n");
        sb.append("StrokeWidth:\t\t" + strokeWidth + "\n");
        sb.append("Debug mode:\t\t" + Integer.toBinaryString(debugMode) + "\n");

        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        graphics.setColor(Color.BLACK);
        graphics.drawString(sb.toString(), 0, fontSize);

        graphics.setColor(Color.WHITE);
    }

    private void draw(double deltaTime) {
        long startTime = System.currentTimeMillis();
        graphics.clearRect(0, 0, GRID_WIDTH * GRID_SIZE, GRID_HEIGHT * GRID_SIZE);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (grid[x * GRID_HEIGHT + y] != null) grid[x * GRID_HEIGHT + y].draw(graphics, debugMode);
            }
        }
        for (Iterator<Particle> iterator = particles.iterator(); iterator.hasNext(); ) {
            Particle particle = iterator.next();
            particle.draw(graphics);
        }

        drawTime = System.currentTimeMillis() - startTime;
        drawOverlay(deltaTime);
    }

    private void update(double deltaTime) {
        long startTime = System.currentTimeMillis();
        applyForce();
        for (Particle particle : particles) {
            particle.update(deltaTime);
        }
        solveCollisions();
        updateTime = System.currentTimeMillis() - startTime;
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
        collisionChecks = 0;
        for (Particle particle : particles) {
            for (Tile tile : grid) {
                if (tile instanceof NonTraversableTile) {
                    Rectangle2D tileRec = tile.getShape().getBounds2D();
                    Rectangle2D particleRec = particle.getShape().getBounds2D();

                    collisionChecks += 4;

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