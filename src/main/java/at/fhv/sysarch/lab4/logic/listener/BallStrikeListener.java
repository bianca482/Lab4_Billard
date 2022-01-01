package at.fhv.sysarch.lab4.logic.listener;

import at.fhv.sysarch.lab4.game.Ball;
import org.dyn4j.geometry.Vector2;

public interface BallStrikeListener {
    void onBallStrike(Ball b, Vector2 oldPosition);
}