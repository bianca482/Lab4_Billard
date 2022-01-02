package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.logic.listener.BallPocketedListener;
import at.fhv.sysarch.lab4.logic.listener.BallStrikeListener;
import at.fhv.sysarch.lab4.logic.listener.BallsCollisionListener;
import at.fhv.sysarch.lab4.logic.listener.ObjectsRestListener;
import org.dyn4j.dynamics.Body;

public interface BillardListener {
    void addBody(Body b);
    void performStrike(double startX, double startY, double endX, double endY);
    void resetBalls();
    void addBallsCollisionListener(BallsCollisionListener ballsCollisionListener);
    void addBallPocketedListener(BallPocketedListener ballPocketedListener);
    void addBallStrikeListener(BallStrikeListener ballStrikeListener);
    void addObjectRestListener(ObjectsRestListener objectsRestListener);
}
