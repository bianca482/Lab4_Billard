package at.fhv.sysarch.lab4.physics;

import at.fhv.sysarch.lab4.game.Ball;
import at.fhv.sysarch.lab4.game.BallStrikeListener;

// Irgendwie sollen wir noch die Interfaces verwenden --> aber wie?
// Im Sinne von Spiellogik getrennt in einer Klasse halten/ Physic Klasse entlasten? (Prüfen ob Regeln verletzt etc.)
// Oder soll Physic auch diese Methoden implementieren?
public class GameLogic implements BallStrikeListener, BallPocketedListener, BallsCollisionListener, ObjectsRestListener {

    @Override
    public void onBallStrike(Ball b) {

    }

    @Override
    public boolean onBallPocketed(Ball b) {
        return false;
    }

    @Override
    public void onBallsCollide(Ball b1, Ball b2) {

    }

    @Override
    public void onEndAllObjectsRest() {

    }

    @Override
    public void onStartAllObjectsRest() {

    }
}
