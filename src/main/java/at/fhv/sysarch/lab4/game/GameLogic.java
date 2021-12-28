package at.fhv.sysarch.lab4.game;

import at.fhv.sysarch.lab4.physics.listener.BallStrikeListener;
import at.fhv.sysarch.lab4.physics.listener.BallPocketedListener;
import at.fhv.sysarch.lab4.physics.listener.BallsCollisionListener;
import at.fhv.sysarch.lab4.physics.listener.ObjectsRestListener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

// Irgendwie sollen wir noch die Interfaces verwenden --> aber wie?
// Im Sinne von Spiellogik getrennt in einer Klasse halten/ Physic Klasse entlasten? (Prüfen ob Regeln verletzt etc.)
// Oder soll Physic auch diese Methoden implementieren?
public class GameLogic implements BallStrikeListener, BallPocketedListener, BallsCollisionListener, ObjectsRestListener {

    private Game game;
    private Ball ballTouchedByCue;
    private List<Ball> contactedBalls = new LinkedList<>();
    private Set<Ball> pocketBalls = new HashSet<>();
    List<String> fouls = new LinkedList<>();
    private boolean deactivateUi = false;

    public boolean isDeactivateUi() {
        return deactivateUi;
    }

    public void setDeactivateUi(boolean deactivateUi) {
        this.deactivateUi = deactivateUi;
    }


    public GameLogic(Game game) {
        this.game = game;
    }

    @Override
    public void onBallStrike(Ball b) {
        ballTouchedByCue = b;
    }

    @Override
    public boolean onBallPocketed(Ball b) {
        pocketBalls.add(b);
        if(b.isWhite()){
            fouls.add("White Ball is in pocket");
        }
        //TODO return
        return false;
    }

    @Override
    public void onBallsCollide(Ball b1, Ball b2) {
        if(b1 == ballTouchedByCue){
            contactedBalls.add(b2);
        }else{
            contactedBalls.add(b1);
        }
    }

    @Override
    public void onEndAllObjectsRest() {
        // kein Ball bewegt sich mehr. Punkte zählen und nächsten Spieler auswählen
        if(contactedBalls.size() == 0){
            // Foul: It is a foul if the white ball does not touch any object ball.
            fouls.add("No other balls was touched");
        }

        if(fouls.size() != 0){
            game.getActivePlayer().addScore(-1);
            game.switchPlayer();
            for (String foul : fouls) {
                System.out.println("Foul: "+foul);
            }
        }else{
            int score = pocketBalls.size();
            game.getActivePlayer().addScore(score);
        }
        deactivateUi = false;

    }

    @Override
    public void onStartAllObjectsRest() {
        contactedBalls = new LinkedList<>();
        pocketBalls = new HashSet<>();
        fouls = new LinkedList<>();
        deactivateUi = true;
    }

}
