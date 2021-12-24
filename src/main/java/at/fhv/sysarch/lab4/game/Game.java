package at.fhv.sysarch.lab4.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.fhv.sysarch.lab4.physics.Physic;
import at.fhv.sysarch.lab4.rendering.Renderer;
import javafx.scene.input.MouseEvent;

public class Game {
    private final Renderer renderer;
    private final Physic physic;
    private Cue cue;

    public Game(Renderer renderer, Physic physic) {
        this.renderer = renderer;
        this.physic = physic;
        this.initWorld();
    }

    public void onMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.renderer.screenToPhysicsX(x);
        double pY = this.renderer.screenToPhysicsY(y);

        cue.setStartPosition(pX, pY);
    }

    public void onMouseReleased(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.renderer.screenToPhysicsX(x);
        double pY = this.renderer.screenToPhysicsY(y);

        this.physic.performStrike(cue.getStartX(), cue.getStartY(), pX, pY);
    }

    public void setOnMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = renderer.screenToPhysicsX(x);
        double pY = renderer.screenToPhysicsY(y);

        cue.setEndPosition(pX, pY);
    }

    private void placeBalls(List<Ball> balls) {
        Collections.shuffle(balls);

        // positioning the billard balls IN WORLD COORDINATES: meters
        int row = 0;
        int col = 0;
        int colSize = 5;

        double y0 = -2*Ball.Constants.RADIUS*2;
        double x0 = -Table.Constants.WIDTH * 0.25 - Ball.Constants.RADIUS;

        for (Ball b : balls) {
            double y = y0 + (2 * Ball.Constants.RADIUS * row) + (col * Ball.Constants.RADIUS);
            double x = x0 + (2 * Ball.Constants.RADIUS * col);

            b.setPosition(x, y);
            b.getBody().setLinearVelocity(0, 0);
            renderer.addBall(b);
            physic.addBody(b.getBody());

            row++;

            if (row == colSize) {
                row = 0;
                col++;
                colSize--;
            }
        }
    }

    private void initWorld() {
        List<Ball> balls = new ArrayList<>();
        
        for (Ball b : Ball.values()) {
            if (b == Ball.WHITE)
                continue;

            balls.add(b);
        }
       
        this.placeBalls(balls);

        Ball.WHITE.setPosition(Table.Constants.WIDTH * 0.25, 0);
        
        renderer.addBall(Ball.WHITE);
        physic.addBody(Ball.WHITE.getBody());
        
        Table table = new Table();
        renderer.setTable(table);
        physic.addBody(table.getBody());

        cue = new Cue();
        renderer.setCue(cue);
        physic.addBody(cue.getBody());
    }
}