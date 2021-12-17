package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.rendering.FrameListener;
import org.dyn4j.dynamics.*;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Physic implements ContactListener, StepListener, FrameListener {

    private World world;

    public Physic() {
        this.world = new World();
        this.world.setGravity(World.ZERO_GRAVITY);
        //this.world.getSettings().setStepFrequency(); //Wiederholfrequenz evtl notwendig bei Problemen
        this.world.addListener(this); //Physics Klasse soll notifiziert werden wenn in der Welt was passiert
    }

    //Eventuell neues Interface welche diese zusätzlichen Methoden definiert
    public void addBody(Body b){
        this.world.addBody(b);
    }

    public void performStrike() {
        Vector2 origin = new Vector2(1, 0);
        Vector2 direction = new Vector2(-1, 0); //Stoßrichtung nach links

        Ray ray = new Ray(origin, direction);
        List<RaycastResult> results = new ArrayList<>();

        boolean hit = this.world.raycast(ray, 0, true, false, results); // prüfen ob was getroffen wurde und wenn ja, was getroffen wurde (=results)

        if (hit) {
            System.out.println(results.get(0).getBody().getUserData());

            direction.multiply(500); //Kraft vorgeben

            results.get(0).getBody().applyForce(direction);
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

    }

    @Override
    public boolean persist(PersistedContactPoint point) {
        return false;
    }

    @Override
    public boolean preSolve(ContactPoint point) {
        return false;
    }

    @Override
    public void postSolve(SolvedContactPoint point) {

    }
}