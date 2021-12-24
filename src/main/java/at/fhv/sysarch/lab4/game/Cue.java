package at.fhv.sysarch.lab4.game;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;

public class Cue {
    private final Body body;
    private double startX;
    private double startY;
    private double endX;
    private double endY;

    public Cue() {
        this.body = new Body();
        this.body.translate(0, 0);
        this.body.setMass(MassType.NORMAL);
    }

    public Body getBody() { return this.body; }

    public void setStartPosition(double x, double y) {
        this.body.translateToOrigin();
        this.body.translate(x, y);
        this.startX = x;
        this.startY = y;
    }

    public void setEndPosition (double x, double y){
        this.endX = x;
        this.endY = y;
    }

    public double getStartX(){
        return this.startX;
    }

    public double getStartY(){
        return this.startY;
    }

    public double getEndX(){
        return this.endX;
    }

    public double getEndY(){
        return this.endY;
    }
}
