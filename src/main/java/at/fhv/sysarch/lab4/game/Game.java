package at.fhv.sysarch.lab4.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.fhv.sysarch.lab4.logic.GameLogic;
import at.fhv.sysarch.lab4.physics.Physic;
import at.fhv.sysarch.lab4.rendering.Renderer;
import javafx.scene.input.MouseEvent;
import org.dyn4j.geometry.Vector2;

public class Game {

    private final Renderer renderer;
    private final Physic physic;
    private Cue cue;
    private final Player player1 = new Player("Player 1");
    private final Player player2 = new Player("Player 2");
    private Player activePlayer = player1;
    private Ball whiteBall;
    private Vector2 whiteBallLastPosition;

    public Game(Renderer renderer, Physic physic) {
        this.renderer = renderer;
        this.physic = physic;

        GameLogic gameLogic = new GameLogic(this);
        this.physic.addObjectRestListener(gameLogic);
        this.physic.addBallPocketedListener(gameLogic);
        this.physic.addBallsCollisionListener(gameLogic);
        this.physic.addBallStrikeListener(gameLogic);

        this.renderer.setPlayer1(player1);
        this.renderer.setPlayer2(player2);
        player1.setActivePlayer(true);

        this.initWorld();
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void switchPlayer() {
        if (activePlayer == null || activePlayer == player2) {
            activePlayer = player1;
            activePlayer.setActivePlayer(true);
            player2.setActivePlayer(false);
        } else {
            activePlayer = player2;
            activePlayer.setActivePlayer(true);
            player1.setActivePlayer(false);
        }
    }

    protected Renderer getRenderer() {
        return renderer;
    }

    protected Physic getPhysic() {
        return physic;
    }

    protected Cue getCue() {
        return cue;
    }

    private void placeBalls(List<Ball> balls) {
        Collections.shuffle(balls);

        // positioning the billard balls IN WORLD COORDINATES: meters
        int row = 0;
        int col = 0;
        int colSize = 5;

        double y0 = -2 * Ball.Constants.RADIUS * 2;
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
            if (b == Ball.WHITE) {
                whiteBall = b;
                continue;
            }
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

    public void onMousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.getRenderer().screenToPhysicsX(x);
        double pY = this.getRenderer().screenToPhysicsY(y);

        this.getCue().setStartPosition(pX, pY);
    }

    public void onMouseReleased(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.getRenderer().screenToPhysicsX(x);
        double pY = this.getRenderer().screenToPhysicsY(y);

        this.getPhysic().performStrike(this.getCue().getStartX(), this.getCue().getStartY(), pX, pY);
    }

    public void setOnMouseDragged(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        double pX = this.getRenderer().screenToPhysicsX(x);
        double pY = this.getRenderer().screenToPhysicsY(y);

        this.getCue().setEndPosition(pX, pY);
    }
}