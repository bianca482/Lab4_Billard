package at.fhv.sysarch.lab4.game;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;

public class Cue {
    private final Body body;
    private double startx;
    private double starty;
    private double endx;
    private double endy;

    public Cue() {
        this.body = new Body();
        this.body.translate(0, 0);
        this.body.setMass(MassType.NORMAL);
    }

    public Body getBody() { return this.body; }

    public void setStartPosition(double x, double y) {
        this.body.translateToOrigin();
        this.body.translate(x, y);
        this.startx = x;
        this.starty = y;
    }

    public void setEndPosition (double x, double y){
        this.endx = x;
        this.endy = y;
    }

    public double getStartX(){
        return this.startx;
    }

    public double getStartY(){
        return this.starty;
    }

    public double getEndX(){
        return this.endx;
    }

    public double getEndY(){
        return this.endy;
    }
}
