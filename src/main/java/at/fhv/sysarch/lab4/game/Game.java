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
    private final GameLogic gameLogic;
    private final Player player1;
    private final Player player2;
    private Player activePlayer;
    private Cue cue;
    private List<Ball> balls;
    private Ball whiteBall;
    private GameState gameState;


    public Game(Renderer renderer, Physic physic) {
        this.renderer = renderer;
        this.physic = physic;

        this.gameLogic = new GameLogic(this, renderer, physic);
        this.physic.addObjectRestListener(gameLogic);
        this.physic.addBallPocketedListener(gameLogic);
        this.physic.addBallsCollisionListener(gameLogic);
        this.physic.addBallStrikeListener(gameLogic);

        this.player1 = new Player("Player 1");
        this.player2 = new Player("Player 2");
        this.renderer.setPlayer1(player1);
        this.renderer.setPlayer2(player2);
        activePlayer = player1;
        player1.setActivePlayer(true);

        this.gameState = GameState.GAME_RUNNING;

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

    public Ball getWhiteBall() {
        return whiteBall;
    }

    public void switchPlayer() {
        if (activePlayer == null || activePlayer.equals(player2)) {
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

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void placeBalls(List<Ball> balls) {
        Collections.shuffle(balls);

        // positioning the billard balls IN WORLD COORDINATES: meters
        int row = 0;
        int col = 0;
        int colSize = 5;

        double y0 = -4 * Ball.Constants.RADIUS;
        double x0 = -Table.Constants.WIDTH * 0.25 - Ball.Constants.RADIUS;

        for (Ball b : balls) {
            b.setVisible(true);
            // für Testzwecke
//            if (balls.size() == 15 && row > 1) {
//                b.setVisible(false);
//                b.setPosition(Table.Constants.WIDTH, 0);
//                continue;
//            }
            double y = y0 + (2 * Ball.Constants.RADIUS * row) + (col * Ball.Constants.RADIUS);
            double x = x0 + (2 * Ball.Constants.RADIUS * col);

            b.setPosition(x, y);
            b.getBody().setLinearVelocity(0, 0);
            row++;

            if (row == colSize) {
                row = 0;
                col++;
                colSize--;
            }
        }
    }

    private void initWorld() {
        balls = new ArrayList<>();

        for (Ball b : Ball.values()) {
            if (b == Ball.WHITE) {
                whiteBall = b;
                continue;
            }
            balls.add(b);
        }

        this.placeBalls(balls);
        for (Ball ball : balls) {
            renderer.addBall(ball);
            physic.addBody(ball.getBody());
        }

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
        if (gameLogic.isDeactivateUi()) {
            return;
        }
        double x = e.getX();
        double y = e.getY();

        double pX = this.getRenderer().screenToPhysicsX(x);
        double pY = this.getRenderer().screenToPhysicsY(y);

        if (gameState.equals(GameState.GAME_RUNNING)) {
            this.getCue().setStartPosition(pX, pY);
        } else if (gameState.equals(GameState.SET_WHITE_BALL)) {
            // Prüfen, ob die Position nicht außerhalb des Tisches liegt
            if (!(Math.abs(pX) > Table.Constants.WIDTH / 2 || Math.abs(pY) > Table.Constants.HEIGHT / 2)) {

                // ToDo: Prüfen, ob kein Ball auf der gewünschten Position ist
//                boolean canPlaceBall = true;
//                for (Ball b : balls) {
//                    if (b.getPosition().distance(new Vector2(pX, pY)) <= 0.05) {
//                        System.out.println(b.getPosition().distance(new Vector2(pX, pY)));
//                        canPlaceBall = false;
//                    }
//                }
//
//                if (canPlaceBall) {
//                    this.whiteBall.setPosition(pX, pY);
//                    gameState = GameState.GAME_RUNNING;
//                }

                this.whiteBall.setPosition(pX, pY);
                gameState = GameState.GAME_RUNNING;
            }
        }
    }

    public void onMouseReleased(MouseEvent e) {
        if (gameLogic.isDeactivateUi()) {
            return;
        }
        double x = e.getX();
        double y = e.getY();

        double pX = this.getRenderer().screenToPhysicsX(x);
        double pY = this.getRenderer().screenToPhysicsY(y);

        if (gameState.equals(GameState.GAME_RUNNING)) {
            this.getPhysic().performStrike(this.getCue().getStartX(), this.getCue().getStartY(), pX, pY);
        }
    }

    public void setOnMouseDragged(MouseEvent e) {
        if (gameLogic.isDeactivateUi()) {
            return;
        }
        double x = e.getX();
        double y = e.getY();

        double pX = this.getRenderer().screenToPhysicsX(x);
        double pY = this.getRenderer().screenToPhysicsY(y);

        if (gameState.equals(GameState.GAME_RUNNING)) {
            this.getCue().setEndPosition(pX, pY);
        }
    }

    public List<Ball> getBalls() {
        return balls;
    }
}