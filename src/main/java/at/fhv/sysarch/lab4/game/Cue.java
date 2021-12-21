package at.fhv.sysarch.lab4.game;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;

public class Cue {
    private final Body body;

    public Cue() {
        this.body = new Body();
        this.body.translate(0, 0);
        this.body.setMass(MassType.NORMAL);
    }

    public Body getBody() { return this.body; }

    public void setPosition(double x, double y) {
        this.body.translateToOrigin();
        this.body.translate(x, y);
    }
}
