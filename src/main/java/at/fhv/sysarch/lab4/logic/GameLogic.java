package at.fhv.sysarch.lab4.logic;

import at.fhv.sysarch.lab4.game.Ball;
import at.fhv.sysarch.lab4.game.Game;
import at.fhv.sysarch.lab4.game.GameState;
import at.fhv.sysarch.lab4.game.Table;
import at.fhv.sysarch.lab4.logic.listener.BallStrikeListener;
import at.fhv.sysarch.lab4.logic.listener.BallPocketedListener;
import at.fhv.sysarch.lab4.logic.listener.BallsCollisionListener;
import at.fhv.sysarch.lab4.logic.listener.ObjectsRestListener;
import at.fhv.sysarch.lab4.physics.Physic;
import at.fhv.sysarch.lab4.rendering.Renderer;
import org.dyn4j.geometry.Vector2;

import java.util.*;

import static at.fhv.sysarch.lab4.game.Ball.WHITE;

public class GameLogic implements BallStrikeListener, BallPocketedListener, BallsCollisionListener, ObjectsRestListener {

    private final Game game;
    private final Renderer renderer;
    private final Physic physic;
    private Ball ballTouchedByCue;
    private List<Ball> contactedBalls = new LinkedList<>();
    private Set<Ball> pocketBalls = new HashSet<>();
    private List<Foul> fouls = new LinkedList<>();
    private boolean deactivateUi = false;
    private Vector2 whiteBallOldPosition;

    public GameLogic(Game game, Renderer renderer, Physic physic) {
        this.game = game;
        this.renderer = renderer;
        this.physic = physic;
    }

    private enum Foul {
        WHITE_BALL_IN_POCKET ("White Ball is in pocket."),
        WHITE_BALL_NOT_HIT ("Player did not hit the white ball."),
        WHITE_BALL_HIT_NO_OBJECT_BALL ("White ball did not touch any object ball.");

        private final String name;

        Foul(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public boolean isDeactivateUi() {
        return deactivateUi;
    }

    @Override
    public void onBallStrike(Ball b, Vector2 oldPosition) {
        this.renderer.setFoulMessage("");
        // Foul: It is a foul if any other ball than the white one is stroke by the cue.
        if (!b.equals(WHITE)) {
            fouls.add(Foul.WHITE_BALL_NOT_HIT);
        }
        whiteBallOldPosition = oldPosition;
        ballTouchedByCue = b;
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        pocketBalls.add(b);
        // Foul: It is a foul if the white ball is pocketed.
        if (b.isWhite()) {
            fouls.add(Foul.WHITE_BALL_IN_POCKET);
        }
        b.setVisible(false);
        b.getBody().setLinearVelocity(0, 0); // Wartezeit verringern von B??llen die versenkt worden sind
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
        // Kein Ball bewegt sich mehr. Punkte z??hlen und n??chsten Spieler ausw??hlen
        if (contactedBalls.size() == 0 && !fouls.contains(Foul.WHITE_BALL_NOT_HIT) && !fouls.contains(Foul.WHITE_BALL_IN_POCKET)) {
            // Foul: It is a foul if the white ball does not touch any object ball.
            fouls.add(Foul.WHITE_BALL_HIT_NO_OBJECT_BALL);
        }

        if (fouls.size() != 0) {
            game.getActivePlayer().addScore(-1);
            StringBuilder allFouls = new StringBuilder();

            for (Foul foul : fouls) {
                allFouls.append(foul.name).append("\n");
            }

            StringBuilder actionMessage = new StringBuilder();
            actionMessage.append(game.getActivePlayer().getName()).append(" committed a foul, switching players.");

            renderer.setFoulMessage(allFouls.toString());
            game.switchPlayer();

            // Wenn die wei??e Kugel keine andere Kugel getroffen hat, kann der Spieler die Position der Kugel frei hand bestimmen
            if (fouls.contains(Foul.WHITE_BALL_HIT_NO_OBJECT_BALL)) {
                actionMessage.append(" ").append(game.getActivePlayer().getName()).append(" can place the white ball free hand.");
                game.setGameState(GameState.SET_WHITE_BALL);
            }

            renderer.setActionMessage(actionMessage.toString());
        } else {
            int score = pocketBalls.size();
            game.getActivePlayer().addScore(score);
            if (score == 0) {
                renderer.setActionMessage(game.getActivePlayer().getName() + " did not pocket any balls.");
                game.switchPlayer();
            } else {
                StringBuilder allPocketedBalls = new StringBuilder();

                List<Ball> ballList = new ArrayList<>(pocketBalls);
                for (int i = 0; i < ballList.size(); i++) {
                    Ball ball = ballList.get(i);
                    if (i == (ballList.size() - 1)) {
                        allPocketedBalls.append(ball.getBody().getUserData());
                    } else {
                        allPocketedBalls.append(ball.getBody().getUserData()).append(", ");
                    }
                }
                renderer.setActionMessage(game.getActivePlayer().getName() + " pocketed the following balls: " + allPocketedBalls);
            }
        }

        // B??lle die au??erhalb vom Table sind (z.B. durch zu festes schie??en) werden als versenkt angesehen
        for (Ball ball : this.game.getBalls()) {
            Vector2 position = ball.getPosition();
            if (Math.abs(position.x) > Table.Constants.WIDTH / 2 || Math.abs(position.y) > Table.Constants.HEIGHT / 2) {
                ball.setVisible(false);
            }
        }

        boolean whiteBallOutsideOfTable = Math.abs(game.getWhiteBall().getPosition().x) > Table.Constants.WIDTH / 2 || Math.abs(game.getWhiteBall().getPosition().y) > Table.Constants.HEIGHT / 2;
        if (pocketBalls.contains(this.game.getWhiteBall()) || whiteBallOutsideOfTable) {
            this.game.getWhiteBall().setPosition(whiteBallOldPosition.x, whiteBallOldPosition.y);
            this.game.getWhiteBall().setVisible(true);
        }

        deactivateUi = false;

        handleEndState();
    }

    @Override
    public void onStartAllObjectsRest() {
        contactedBalls = new LinkedList<>();
        pocketBalls = new HashSet<>();
        fouls = new LinkedList<>();
        deactivateUi = true;
    }

    private void handleEndState() {
        // ??berpr??fen, ob 14 oder mehr B??lle versenkt sind
        List<Ball> balls = this.game.getBalls();
        List<Ball> hiddenBalls = new LinkedList<>();
        int hiddenBallCount = 0;
        Ball lastNotHiddenBall = null;
        for (Ball ball : balls) {
            if (!ball.isVisible()) {
                hiddenBallCount++;
                hiddenBalls.add(ball);
            } else {
                lastNotHiddenBall = ball;
            }
        }

        if (hiddenBallCount >= 14) {
            if (hiddenBallCount == 15) {
                // Die letzten beiden B??lle wurden gleichzeitig versenkt, somit werden alle B??lle wieder als Dreieck angeordnet
                this.game.placeBalls(balls);
            } else if (hiddenBallCount == 14) {
                // Die versenkten B??lle werden als unvollst??ndiges Dreieck angeordnet - ??brig gebliebener Ball bleibt an seiner Position, falls die Position nicht im Dreiecksbereich liegt
                // Falls der Ball im Dreiecksbereich liegt, wird er neu positioniert.
                this.game.placeBalls(hiddenBalls);
                for (Ball hiddenBall : hiddenBalls) {
                    double distance = hiddenBall.getPosition().distance(lastNotHiddenBall.getPosition());
                    if (distance < 2 * Ball.Constants.RADIUS) {
                        // letzter Ball ber??hrt Dreieck und muss deshalb umgesetzt werden.
                        double x0 = Table.Constants.WIDTH * 0.33;
                        lastNotHiddenBall.setPosition(x0, 0);
                        break;
                    }
                }
            }

            // Falls der wei??e Ball im Dreiecksbereich liegt, oder auf dem neuen Platz des letzten Balls, wir der wei??e Ball neu platziert
            for (Ball hiddenBall : hiddenBalls) {
                double distanceToWhiteBall = this.game.getWhiteBall().getPosition().distance(hiddenBall.getPosition());
                if (distanceToWhiteBall < 2 * Ball.Constants.RADIUS) {
                    // wei??er Ball ber??hrt Dreieck und muss deshalb umgesetzt werden.
                    Vector2 newWhitePosition = new Vector2(Table.Constants.WIDTH * 0.25, 0);
                    if (lastNotHiddenBall != null && lastNotHiddenBall.getPosition().distance(newWhitePosition) <= 2 * Ball.Constants.RADIUS) {
                        newWhitePosition.add(-5 * Ball.Constants.RADIUS, 0);
                    }
                    this.game.getWhiteBall().setPosition(newWhitePosition.x, newWhitePosition.y);
                    break;
                }
            }
            physic.resetBalls();
        }
    }
}
