package at.fhv.sysarch.lab4.game;

public class Player {

    private final String name;
    private int score = 0;
    private boolean activePlayer = false;

    public Player(String name) {
        this.name = name;
    }

    public boolean isActivePlayer() {
        return this.activePlayer;
    }

    public void setActivePlayer(boolean activePlayer) {
        this.activePlayer = activePlayer;
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(int points){
        this.score = score + points;
    }

    public String getName(){
        return this.name;
    }
}
