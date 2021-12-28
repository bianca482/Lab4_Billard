package at.fhv.sysarch.lab4.logic;

import at.fhv.sysarch.lab4.game.Ball;
import at.fhv.sysarch.lab4.game.Game;
import at.fhv.sysarch.lab4.logic.listener.BallStrikeListener;
import at.fhv.sysarch.lab4.logic.listener.BallPocketedListener;
import at.fhv.sysarch.lab4.logic.listener.BallsCollisionListener;
import at.fhv.sysarch.lab4.logic.listener.ObjectsRestListener;
import at.fhv.sysarch.lab4.rendering.Renderer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static at.fhv.sysarch.lab4.game.Ball.WHITE;

public class GameLogic implements BallStrikeListener, BallPocketedListener, BallsCollisionListener, ObjectsRestListener {

    private Game game;
    private Ball ballTouchedByCue;
    private List<Ball> contactedBalls;
    private Set<Ball> pocketBalls;
    private List<String> fouls;
    private boolean deactivateUi = false;
    private final Renderer renderer;

    public GameLogic(Game game, Renderer renderer) {
        this.game = game;
        this.renderer = renderer;
        fouls = new LinkedList<>();
        pocketBalls = new HashSet<>();
        contactedBalls = new LinkedList<>();
    }

    public boolean isDeactivateUi() {
        return deactivateUi;
    }

    public void setDeactivateUi(boolean deactivateUi) {
        this.deactivateUi = deactivateUi;
    }

    @Override
    public void onBallStrike(Ball b) {
        this.renderer.setFoulMessage("");
        // Foul: It is a foul if any other ball than the white one is stroke by the cue.
        if (!b.equals(WHITE)) {
            fouls.add("Player did not hit the white ball.");
        }
        ballTouchedByCue = b;
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        pocketBalls.add(b);
        // Foul: It is a foul if the white ball is pocketed.
        if (b.isWhite()) {
            fouls.add("White Ball is in pocket.");
        }
        //TODO return
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
            renderer.setActionMessage(game.getActivePlayer().getName() + " commited a foul, switching players.");
        } else {
            int score = pocketBalls.size();
            game.getActivePlayer().addScore(score);
            if (score == 0) {
                renderer.setActionMessage(game.getActivePlayer().getName() + " did not pocket any balls.");
            } else {
                StringBuilder allPocketedBalls = new StringBuilder();

                for (Ball ball : pocketBalls) {
                    allPocketedBalls.append(ball.getBody().getUserData()).append("\n");
                }
                renderer.setActionMessage(game.getActivePlayer().getName() + " pocketed the following balls: " + allPocketedBalls);
            }
        }
        deactivateUi = false;

    }

    @Override
    public void onStartAllObjectsRest() {
        contactedBalls = new LinkedList<>();
        pocketBalls = new HashSet<>();
        fouls = new LinkedList<>();
        deactivateUi = true;
    }
}
