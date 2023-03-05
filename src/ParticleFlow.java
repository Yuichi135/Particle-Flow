import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;

public class ParticleFlow extends Application {
    private FXGraphics2D graphics;

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(900, 600);
        stage.setScene(new Scene(new Group(canvas)));
        stage.setTitle("Particle Flow");
        stage.show();

        graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        graphics.setBackground(Color.BLACK);
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
    }

    private void draw() {

    }

    private void update(double deltaTime) {

    }
}