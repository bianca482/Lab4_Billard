package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.rendering.FrameListener;
import at.fhv.sysarch.lab4.rendering.Renderer;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Physic implements ContactListener, StepListener, FrameListener {

    private World world;
    private Renderer renderer;

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

    public void performStrike(double x, double y) {
        Vector2 origin = new Vector2(x, y); //Anhand der Koordinaten bestimmen, wo der Stoß stattgefunden hat
        Vector2 direction = new Vector2(-1, 0); //Stoßrichtung nach links

        Ray ray = new Ray(origin, direction);
        List<RaycastResult> results = new ArrayList<>();

        boolean hit = this.world.raycast(ray, 0, true, false, results); // prüfen ob was getroffen wurde und wenn ja, was getroffen wurde (=results)

        if (hit) {
            System.out.println(results.get(0).getBody().getUserData());

            direction.multiply(500); //Kraft vorgeben

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

    }

    @Override
    public void sensed(ContactPoint point) {

    }

    @Override
    public boolean begin(ContactPoint point) {
        //Welche Bodies Kontakt hatten
        //point.getBody1();
        //point.getBody2();
        return false;
    }

    @Override
    public void end(ContactPoint point) {
        this.renderer.setActionMessage(point.getBody1().getUserData() + " touched " + point.getBody2().getUserData());
    }

    @Override
    public boolean persist(PersistedContactPoint point) {
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
