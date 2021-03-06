package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.*;
import at.fhv.sysarch.lab4.logic.listener.*;
import at.fhv.sysarch.lab4.rendering.FrameListener;
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.contact.*;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

import java.util.*;

import static at.fhv.sysarch.lab4.game.Ball.WHITE;

public class Physic implements RaycastListener, ContactListener, StepListener, FrameListener, BillardListener {

    private final static int FORCE = 500; //Kraft vorgeben
    private final World world;
    private boolean ballWasMovingInLastStep = false;
    private List<Ball> allPocketedBallsOfGame = new LinkedList<>();
    private Map<Ball, Boolean> countedBallsOfStrike = new HashMap<>();

    private final List<BallsCollisionListener> ballsCollisionListeners = new LinkedList<>();
    private final List<BallPocketedListener> ballPocketedListeners = new LinkedList<>();
    private final List<BallStrikeListener> ballStrikeListeners = new LinkedList<>();
    private final List<ObjectsRestListener> objectsRestListeners = new LinkedList<>();

    public Physic() {
        this.world = new World();
        this.world.setGravity(World.ZERO_GRAVITY);
        this.world.addListener(this);
    }

    @Override
    public void addBody(Body b) {
        this.world.addBody(b);
    }

    @Override
    public void performStrike(double startX, double startY, double endX, double endY) {
        countedBallsOfStrike.clear();

        Vector2 origin = new Vector2(startX, startY); //Anhand der Koordinaten bestimmen, wo der Stoß stattgefunden hat
        Vector2 direction = origin.difference(endX, endY); //Stoßrichtung berechnen

        Ray ray = new Ray(origin, direction);
        List<RaycastResult> results = new ArrayList<>();

        boolean hit = this.world.raycast(ray, 0, true, false, results); // prüfen ob was getroffen wurde und wenn ja, was getroffen wurde (=results)

        if (hit) {
            notifyObjectRestStartListeners();

            // Angestoßenes Objekt
            Body hitObjectData = results.get(0).getBody();
            Ball ball = (Ball) hitObjectData.getUserData();

            notifyBallCueListeners(ball, ball.getPosition());

            //Weiße Kugel stoßen
            direction.multiply(FORCE); //Da mit der Direction multipliziert, wird gewirkte Kraft bei größerem Abstand größer
            hitObjectData.applyForce(direction);
        }
    }

    @Override
    public void resetBalls() {
        allPocketedBallsOfGame.clear();
    }

    @Override
    public void addBallsCollisionListener(BallsCollisionListener ballsCollisionListener) {
        ballsCollisionListeners.add(ballsCollisionListener);
    }

    @Override
    public void addBallPocketedListener(BallPocketedListener ballPocketedListener) {
        ballPocketedListeners.add(ballPocketedListener);
    }

    @Override
    public void addBallStrikeListener(BallStrikeListener ballStrikeListener) {
        ballStrikeListeners.add(ballStrikeListener);
    }

    @Override
    public void addObjectRestListener(ObjectsRestListener objectsRestListener) {
        objectsRestListeners.add(objectsRestListener);
    }

    @Override
    public void onFrame(double dt) {
        this.world.update(dt);
    }

    @Override
    public void begin(Step step, World world) {}

    @Override
    public void updatePerformed(Step step, World world) {}

    @Override
    public void postSolve(Step step, World world) {}

    @Override
    public void end(Step step, World world) {
        LinkedList<Body> movingObjects = new LinkedList<>();

        // Überprüfen ob Kugeln sich noch bewegen
        for (Body body : world.getBodies()) {
            // Magnitude = Größenordnung (wenn Wert 0 --> nichts bewegt sich)
            double magnitude = body.getLinearVelocity().getMagnitude();
            if (magnitude != 0) {
                movingObjects.add(body);
            }
        }
        boolean ballIsMoving = (movingObjects.size() != 0);
        if (ballWasMovingInLastStep != ballIsMoving) {
            // Bälle bewegen sich nun, oder bewegen sich nun nicht mehr
            ballWasMovingInLastStep = ballIsMoving;
            if (!ballIsMoving) {
                notifyObjectRestEndListeners();
            }
        }
    }

    @Override
    public void sensed(ContactPoint point) {}

    @Override
    public boolean begin(ContactPoint point) {
        return false;
    }

    @Override
    public void end(ContactPoint point) {
        if (point.getBody1().getUserData() instanceof Ball && point.getBody2().getUserData() instanceof Ball) {
            Ball ball1 = (Ball) point.getBody1().getUserData();
            Ball ball2 = (Ball) point.getBody2().getUserData();
            notifyBallsCollisionListeners(ball1, ball2);
        }
    }

    @Override
    public boolean persist(PersistedContactPoint point) {
        if (point.isSensor()) {

            //Prüfen, um wie viel sich Ball und Pocket überschneiden
            if (point.getDepth() >= 0.08) {
                Body ball;

                //Prüfen, welcher von den beiden Bodies der Ball ist
                if (point.getBody1().getUserData() instanceof Ball) {
                    ball = point.getBody1();
                } else {
                    ball = point.getBody2();
                }

                Ball pocketedBall = (Ball) ball.getUserData();

                // Damit das Versenken mehrere Bälle während eines Stoßes möglich ist:
                // Ball nur hinzufügen, wenn er noch nicht in der Map ist
                if (!countedBallsOfStrike.containsKey(pocketedBall) && (!allPocketedBallsOfGame.contains(pocketedBall) || pocketedBall.equals(WHITE))) {
                    countedBallsOfStrike.put(pocketedBall, false);
                    if (!pocketedBall.equals(WHITE)) {
                        allPocketedBallsOfGame.add(pocketedBall);
                    }
                    if (!countedBallsOfStrike.get(pocketedBall)) {
                        notifyBallPocketedListeners(pocketedBall);
                        // true setzen, damit man weiß, die Punkte für das Versenken dieses Balles wurden bereits gezählt
                        countedBallsOfStrike.replace(pocketedBall, true);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean preSolve(ContactPoint point) {
        return true;
    }

    @Override
    public void postSolve(SolvedContactPoint point) {}

    @Override
    public boolean allow(Ray ray, Body body, BodyFixture fixture) {
        Object userData = body.getUserData();
        if (userData instanceof Table) {
            return false;
        }
        return true;
    }

    @Override
    public boolean allow(Ray ray, Body body, BodyFixture fixture, Raycast raycast) {
        return true;
    }

    private void notifyBallPocketedListeners(Ball ball) {
        for (BallPocketedListener ballPocketedListener : ballPocketedListeners) {
            ballPocketedListener.onBallPocketed(ball);
        }
    }

    private void notifyBallsCollisionListeners(Ball ball1, Ball ball2) {
        for (BallsCollisionListener ballsCollisionListener : ballsCollisionListeners) {
            ballsCollisionListener.onBallsCollide(ball1, ball2);
        }
    }

    private void notifyBallCueListeners(Ball ball, Vector2 oldPosition) {
        for (BallStrikeListener ballStrikeListener : ballStrikeListeners) {
            ballStrikeListener.onBallStrike(ball, oldPosition);
        }
    }

    private void notifyObjectRestEndListeners() {
        for (ObjectsRestListener objectsRestListener : objectsRestListeners) {
            objectsRestListener.onEndAllObjectsRest();
        }
    }

    private void notifyObjectRestStartListeners() {
        for (ObjectsRestListener objectsRestListener : objectsRestListeners) {
            objectsRestListener.onStartAllObjectsRest();
        }
    }
}
