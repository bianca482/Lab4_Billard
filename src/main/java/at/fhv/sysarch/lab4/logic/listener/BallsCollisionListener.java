package at.fhv.sysarch.lab4.logic.listener;

import at.fhv.sysarch.lab4.game.Ball;

public interface BallsCollisionListener {
    void onBallsCollide(Ball b1, Ball b2);
}