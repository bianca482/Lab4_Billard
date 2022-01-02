# Systemarchitekturen Lab 4: Billard

## Allgemeines
Github Repository verfügbar unter: https://github.com/bianca482/Lab4_Billard

Zum Starten der Applikation wird Java Version 11 benötigt.
In der Klasse Main wurde die Größe der Applikation (SCENE_WIDTH, SCENE_HEIGHT) leicht abgeändert.

Das Jar-File *billard.jar* kann mittels Kommandozeile wie folgt gestartet werden:
*java -p  "<java_fx_path>" --add-modules javafx.controls,javafx.fxml -jar billard.jar* (wobei <java_fx_path> mit dem JAVA FX-Installationspfad des eigenen PCs angepasst werden muss.)

Bianca:
- Player-Klasse, warum erstellt
Die beiden SpielerInnen werden über die Klasse Player abgebildet, damit der Score, Name (Player 1 und Player 2), 
sowie das Festhalten des aktiven Players an derselben Stelle gespeichert werden können.

- Physics Klasse und Listeners (Observer Pattern), Billard Listener


- Gamelogic ausgelagert die Interfaces implementiert
- Grob erklären wie Physics und Gamelogic funktioniert

Um die Spielelogik getrennt von der Physic halten zu können, wurde die Klasse **GameLogic** erstellt.
Hierin wird alles abgehandelt, was mit der Spielelogik und Befolgung der Regeln zusammenhängt. 
Die Klasse GameLogic implementiert die Interfaces BallStrikeListener, BallPocketedListener, BallsCollisionListener 
sowie ObjectsRestListener, damit auf die jeweiligen Ereignisse reagiert werden kann. Die Klasse Physic erkennt
daher mithilfe der Physics-Engine dyn4j, welche Ereignisse aufgetreten sind und teilt dies dann
der GameLogic mit. Die Methode onStartAllObjectsRest wird dann aufgerufen, sobald ein Ball mit dem Cue angeschlagen wurde.
Dies initialisiert auch die für die GameLogic notwendigen Listen.

Wird z.B. ein Ball getroffen, ruft Physic die Methode onBallStrike auf. In dieser Methode
wird zunächst geprüft ob das Foul *"It is a foul if any other ball than the white one is stroke by the cue."*
zutrifft und gegebenenfalls die entsprechende Message zu den Fouls hinzugefügt. Außerdem 
wird hierin auch die alte Position des weißen Balles gespeichert (bevor der Stoß durchgeführt wird), damit im Falle, dass
der weiße Ball versenkt wird, auf diese Position zugriffen werden kann. 
Ähnlich funktioniert dies auch bei der onBallPocketed Methode, welche aufgerufen wird wenn
ein Ball versenkt wird. Hierin wird das Foul *"It is a foul if the white ball is pocketed."* abgeprüft.
Die onBallsCollide-Methode wird aufgerufen, wenn ein Ball einen anderen Ball berührt. Alle Bälle,
die getroffen wurden, werden ebenfalls in einer Liste gespeichert. 

Sobald die Klasse Physic festgestellt hat, dass alle Objekte sich im Ruhezustand befinden, wird 
die Methode onEndAllObjectsRest aufgerufen. Hierin wird das dritte und letzte Foul abgefragt, *"It is a foul if the white ball does not touch any object ball."* 
Wenn keine Objekte sich mehr bewegen, kann außerdem das Zählen der Punkte erfolgen. Wenn ein oder mehrere Fouls
begangen wurden, wird dem aktiven Player einen Punkt abgezogen, die entsprechenden Messages (Foul- und Action-Message) dem Renderer mitgeteilt
sowie der Spieler gewechselt. 
Falls kein Foul begangen wurde, wird anhand der Anzahl der versenkten Kugeln die neue Punkteanzahl des aktiven Spielers
berechnet. Außerdem wird der Spieler gleich gewechselt, wenn gar kein Ball versenkt wurde. Ansonsten werden wieder die
jeweiligen Messages gesetzt und das Spiel kann weitergehen.

Die Methode handleEndState kümmert sich zudem um das Zurücksetzen der Bälle in die Dreiecksform, wobei hier
die oberste Kugel (von der Startposition des Cue aus gesehen) freigelassen wird. Wenn also 
14 Bälle (oder 15, falls der letzte Stoß die letzten zwei Kugeln versenkt hat) versenkt wurden, werden alle bisher versenkten
Bälle wieder in diesem unvollständigem Dreieck positioniert.






Ilona:
UI deaktivieren während Spiels
Außerhalb von Tisch spielen möglich
Bälle die außerhalb vom Table sind (z.B. durch zu festes schießen) werden als versenkt angesehen außer beim weißen Ball
Ball versenken setVisible --> unsichtbar machen
Ball Position --> aktuelle Position gespeichert
Rendering von Cue , Beschreiben wie Cue funktioniert (onMousedragged, onMouseReleased,...) Kraft usw.