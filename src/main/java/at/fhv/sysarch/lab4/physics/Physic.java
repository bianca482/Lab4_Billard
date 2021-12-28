package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.*;
import at.fhv.sysarch.lab4.physics.listener.*;
import at.fhv.sysarch.lab4.rendering.FrameListener;
import at.fhv.sysarch.lab4.rendering.Renderer;
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.contact.*;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static at.fhv.sysarch.lab4.game.Ball.WHITE;

public class Physic implements RaycastListener, ContactListener, StepListener, FrameListener {

    private final static int FORCE = 500; //Kraft vorgeben
    private final World world;
    private final Renderer renderer;
    private boolean alreadySetPoint = false;

    private List<BallsCollisionListener> ballsCollisionListeners = new LinkedList<>();
    private List<BallPocketedListener> ballPocketedListeners = new LinkedList<>();
    private List<BallStrikeListener> ballStrikeListeners = new LinkedList<>();
    private List<ObjectsRestListener> objectsRestListeners = new LinkedList<>();

    public void addBallsCollisionListener(BallsCollisionListener ballsCollisionListener) {
        ballsCollisionListeners.add(ballsCollisionListener);
    }

    public void addBallPocketedListener(BallPocketedListener ballPocketedListener) {
        ballPocketedListeners.add(ballPocketedListener);
    }

    public void addBallStrikeListener(BallStrikeListener ballStrikeListener) {
        ballStrikeListeners.add(ballStrikeListener);
    }

    public void addObjectRestListener(ObjectsRestListener objectsRestListener) {
        objectsRestListeners.add(objectsRestListener);
    }

    public Physic(Renderer renderer) {
        this.world = new World();
        this.world.setGravity(World.ZERO_GRAVITY);
        //this.world.getSettings().setStepFrequency(); //Wiederholfrequenz evtl notwendig bei Problemen
        this.world.addListener(this); //Physics Klasse soll notifiziert werden wenn in der Welt was passiert
        this.renderer = renderer;
    }

    //Eventuell neues Interface welche diese zusätzlichen Methoden definiert
    public void addBody(Body b) {
        this.world.addBody(b);
    }

    public void performStrike(double startX, double startY, double endX, double endY) {
        this.renderer.setFoulMessage("");
        this.alreadySetPoint = false;

        Vector2 origin = new Vector2(startX, startY); //Anhand der Koordinaten bestimmen, wo der Stoß stattgefunden hat
        Vector2 direction = origin.difference(endX, endY); //Stoßrichtung berechnen

        Ray ray = new Ray(origin, direction);
        List<RaycastResult> results = new ArrayList<>();

        boolean hit = this.world.raycast(ray, 0, true, false, results); // prüfen ob was getroffen wurde und wenn ja, was getroffen wurde (=results)

        if (hit) {
            // Angestoßenes Objekt
            Body hitObjectData = results.get(0).getBody();

            // Foul: Nothing hit.
            // Keine UserData? Dann kann es kein Ball sein
//            if (hitObjectData.getUserData() == null) {
//                this.renderer.setFoulMessage("Foul: Nothing hit.");
//                this.renderer.changeCurrentPlayerScore(-1);
//                this.renderer.changeCurrentPlayer();
//                return;
//            }
            Ball ball = (Ball) hitObjectData.getUserData();
            notifyBallCueListener(ball);
            // Foul: It is a foul if any other ball than the white one is stroke by the cue.
            if (ball != null && !ball.equals(WHITE)) {
                this.renderer.setFoulMessage("Foul: Player did not hit the white ball.");
                this.renderer.changeCurrentPlayerScore(-1);
                this.renderer.changeCurrentPlayer();
            }
            notifyObjectRestListenerStart();
            //Weiße Kugel stoßen
            direction.multiply(FORCE); //Da mit der Direction multipliziert, wird gewirkte Kraft bei größerem Abstand größer
            hitObjectData.applyForce(direction);
        }
    }

    @Override
    public void onFrame(double dt) {
        this.world.update(dt);
    }

    @Override
    public void begin(Step step, World world) {

    }

    @Override
    public void updatePerformed(Step step, World world) {

    }

    @Override
    public void postSolve(Step step, World world) {

    }

    private boolean ballWasMovingInLastStep = false;

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
                notifyObjectRestListenerEnd();
            }
        }
    }

    private void notifyballPocketedListeners(Ball ball) {
        for (BallPocketedListener ballPocketedListener : ballPocketedListeners) {
            ballPocketedListener.onBallPocketed(ball);
        }
    }

    private void notifyBallsCollisionListeners(Ball ball1, Ball ball2) {
        for (BallsCollisionListener ballsCollisionListener : ballsCollisionListeners) {
            ballsCollisionListener.onBallsCollide(ball1, ball2);
        }
    }

    private void notifyBallCueListener(Ball ball) {
        for (BallStrikeListener ballStrikeListener : ballStrikeListeners) {
            ballStrikeListener.onBallStrike(ball);
        }
    }

    private void notifyObjectRestListenerEnd() {
        for (ObjectsRestListener objectsRestListener : objectsRestListeners) {
            objectsRestListener.onEndAllObjectsRest();
        }
    }

    private void notifyObjectRestListenerStart() {
        for (ObjectsRestListener objectsRestListener : objectsRestListeners) {
            objectsRestListener.onStartAllObjectsRest();
        }
    }


    @Override
    public void sensed(ContactPoint point) {

    }

    @Override
    public boolean begin(ContactPoint point) {
        return false;
    }

    @Override
    public void end(ContactPoint point) {
        this.renderer.setActionMessage(point.getBody1().getUserData() + " touched " + point.getBody2().getUserData());

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
            if (point.getDepth() >= 0.09) {
                Body ball;

                //Prüfen, welcher von den beiden Bodies der Ball ist
                if (point.getBody1().getUserData() instanceof Ball) {
                    ball = point.getBody1();
                } else {
                    ball = point.getBody2();
                }

                Ball pockedBall = (Ball) ball.getUserData();
                notifyballPocketedListeners(pockedBall);

                this.renderer.removeBall(pockedBall);

                // TODO: Foul: It is a foul if the white ball is pocketed.
                if (!alreadySetPoint && ball.getUserData().equals(WHITE)) {
                    alreadySetPoint = true;

                    // Foul registrieren
                    this.renderer.setFoulMessage("Foul: Player pocketed white ball.");
                    this.renderer.changeCurrentPlayerScore(-1);
                    this.renderer.changeCurrentPlayer();

                    // TODO: Ball neu zeichnen
                    (pockedBall).setPosition(point.getOldPoint().x, point.getOldPoint().y);
                    this.renderer.addBall(pockedBall);
                    this.renderer.drawWhiteBall(pockedBall);
                } else if (!alreadySetPoint && !ball.getUserData().equals(WHITE)) {
                    alreadySetPoint = true;
                    this.renderer.changeCurrentPlayerScore(1);
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
    public void postSolve(SolvedContactPoint point) {

    }

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
}
