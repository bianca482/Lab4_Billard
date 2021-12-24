package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.Ball;
import at.fhv.sysarch.lab4.rendering.FrameListener;
import at.fhv.sysarch.lab4.rendering.Renderer;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.contact.*;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Physic implements ContactListener, StepListener, FrameListener {

    private final static int FORCE = 500; //Kraft vorgeben
    private final World world;
    private final Renderer renderer;

    public Physic(Renderer renderer) {
        this.world = new World();
        this.world.setGravity(World.ZERO_GRAVITY);
        //this.world.getSettings().setStepFrequency(); //Wiederholfrequenz evtl notwendig bei Problemen
        this.world.addListener(this); //Physics Klasse soll notifiziert werden wenn in der Welt was passiert
        this.renderer = renderer;
    }

    //Eventuell neues Interface welche diese zusätzlichen Methoden definiert
    public void addBody(Body b){
        this.world.addBody(b);
    }

    public void performStrike(double startX, double startY, double endX, double endY) {
        Vector2 origin = new Vector2(startX, startY); //Anhand der Koordinaten bestimmen, wo der Stoß stattgefunden hat
        Vector2 direction = origin.difference(endX, endY); //Stoßrichtung berechnen

        Ray ray = new Ray(origin, direction);
        List<RaycastResult> results = new ArrayList<>();

        boolean hit = this.world.raycast(ray, 0, true, false, results); // prüfen ob was getroffen wurde und wenn ja, was getroffen wurde (=results)

        if (hit) {
            direction.multiply(FORCE); //Da mit der Direction multipliziert, wird gewirkte Kraft bei größerem Abstand größer

            results.get(0).getBody().applyForce(direction);

//            results.forEach(result -> {
//                this.renderer.setActionMessage("White ball did touch \n" + result.getBody().getUserData().toString());
//            });
        } else {
            //this.renderer.setActionMessage("White ball did not touch any other ball");
            if (this.renderer.getCurrentPlayer() == 1) {
                this.renderer.setCurrentPlayer(2);
            } else {
                this.renderer.setCurrentPlayer(1);
            }
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

    @Override
    public void end(Step step, World world) {
        // überprüfen ob Kugeln sich noch bewegen
//        List<Double> movingObjects = new LinkedList<>();
//
//        for (Body body : world.getBodies()) {
//            double magnitude = body.getLinearVelocity().getMagnitude(); // Magnitude = Größenordnung (wenn Wert 0 --> nichts bewegt sich)
//            if (magnitude != 0) {
//                movingObjects.add(magnitude);
//            }
//        }
//        if (movingObjects.size() == 0) {
//            if (this.renderer.getCurrentPlayer() == 1) {
//                this.renderer.setCurrentPlayer(2);
//            } else {
//                this.renderer.setCurrentPlayer(1);
//            }
//        }
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
    }

    @Override
    public boolean persist(PersistedContactPoint point) {
        if (point.isSensor()) {
            Body ball;

            if (point.getBody1().getUserData() instanceof Ball) {
                ball = point.getBody1();
            } else {
                ball = point.getBody2();
            }

            if (point.getDepth() >= 0.09) {
                this.renderer.removeBall((Ball) ball.getUserData());
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
}
