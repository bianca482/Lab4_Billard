package at.fhv.sysarch.lab4.logic;

import at.fhv.sysarch.lab4.game.Ball;
import at.fhv.sysarch.lab4.game.Game;
import at.fhv.sysarch.lab4.logic.listener.BallStrikeListener;
import at.fhv.sysarch.lab4.logic.listener.BallPocketedListener;
import at.fhv.sysarch.lab4.logic.listener.BallsCollisionListener;
import at.fhv.sysarch.lab4.logic.listener.ObjectsRestListener;
import at.fhv.sysarch.lab4.rendering.Renderer;
import org.dyn4j.geometry.Vector2;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static at.fhv.sysarch.lab4.game.Ball.WHITE;

public class GameLogic implements BallStrikeListener, BallPocketedListener, BallsCollisionListener, ObjectsRestListener {

    private final Game game;
    private Ball ballTouchedByCue;
    private List<Ball> contactedBalls = new LinkedList<>();
    private Set<Ball> pocketBalls = new HashSet<>();
    private List<String> fouls = new LinkedList<>();
    private boolean deactivateUi = false;
    private final Renderer renderer;
    private Vector2 whiteBallOldPosition;

    public GameLogic(Game game, Renderer renderer) {
        this.game = game;
        this.renderer = renderer;
    }

    public boolean isDeactivateUi() {
        return deactivateUi;
    }

    public void setDeactivateUi(boolean deactivateUi) {
        this.deactivateUi = deactivateUi;
    }

    @Override
    public void onBallStrike(Ball b, Vector2 oldPosition) {
        this.renderer.setFoulMessage("");
        // Foul: It is a foul if any other ball than the white one is stroke by the cue.
        if (!b.equals(WHITE)) {
            fouls.add("Player did not hit the white ball.");
        }
        whiteBallOldPosition = oldPosition;
        ballTouchedByCue = b;
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        pocketBalls.add(b);
        // Foul: It is a foul if the white ball is pocketed.
        if (b.isWhite()) {
            fouls.add("White Ball is in pocket.");
        }
        b.setVisible(false);

        return false;
    }

    @Override
    public void onBallsCollide(Ball b1, Ball b2) {
        if (b1.equals(ballTouchedByCue)) {
            contactedBalls.add(b2);
        } else {
            contactedBalls.add(b1);
        }
    }

    @Override
    public void onEndAllObjectsRest() {
        // Kein Ball bewegt sich mehr. Punkte zählen und nächsten Spieler auswählen
        if (contactedBalls.size() == 0) {
            // Foul: It is a foul if the white ball does not touch any object ball.
            fouls.add("White ball did not touch any object ball.");
        }

        if (fouls.size() != 0) {
            game.getActivePlayer().addScore(-1);
            game.switchPlayer();
            StringBuilder allFouls = new StringBuilder();

            for (String foul : fouls) {
                allFouls.append(foul).append("\n");
            }

            renderer.setFoulMessage(allFouls.toString());
            renderer.setActionMessage(game.getActivePlayer().getName() + " committed a foul, switching players.");
        } else {
            int score = pocketBalls.size();
            game.getActivePlayer().addScore(score);
            if (score == 0) {
                renderer.setActionMessage(game.getActivePlayer().getName() + " did not pocket any balls.");
                game.switchPlayer();
            } else {
                StringBuilder allPocketedBalls = new StringBuilder();

                for (Ball ball : pocketBalls) {
                    allPocketedBalls.append(ball.getBody().getUserData()).append(", ");
                }
                renderer.setActionMessage(game.getActivePlayer().getName() + " pocketed the following balls: " + allPocketedBalls);
            }
        }

        if(pocketBalls.contains(this.game.getWhiteBall())){
            this.game.getWhiteBall().setPosition(whiteBallOldPosition.x,whiteBallOldPosition.y);
            this.game.getWhiteBall().setVisible(true);
        }

        deactivateUi = false;


        // ToDo:
        /*
        After the 14th ball is pocketed, the 14 balls are put back on the table with the top spot left
        free. The current player can then continue either by playing the 15th ball or any other balls
        in the rack. This continues forever.
         */
    }

    @Override
    public void onStartAllObjectsRest() {
        contactedBalls = new LinkedList<>();
        pocketBalls = new HashSet<>();
        fouls = new LinkedList<>();
        deactivateUi = true;
    }
}
