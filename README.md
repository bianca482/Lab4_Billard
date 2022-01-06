# Systemarchitekturen Lab 4: Billard

## Allgemeines
Github Repository verfügbar unter: https://github.com/bianca482/Lab4_Billard

Zum Starten der Applikation wird Java Version 11 benötigt.
In der Klasse Main wurde die Größe der Applikation (SCENE_WIDTH, SCENE_HEIGHT) leicht abgeändert.

Die Applikation kann über die Main-Klasse gestartet werden.

### Wichtige Klasse und deren Verwendung
#### Player
Die beiden SpielerInnen werden über die Klasse Player abgebildet, damit der Score, Name (Player 1 und Player 2),
sowie das Festhalten des aktiven Players an derselben Stelle gespeichert werden können.

#### Cue
Für die Verwendung des Cues werden die Methoden onMousePressed, onMouseReleased und setOnMouseDragged der Game-Klasse eingesetzt.
Mit Drücken der Maus wird die onMousePressed-Methode aufgerufen und der Cue positioniert.
Mit des setOnMouseDragged-Methode kann der Cue kann in alle Richtungen bewegt werden und somit die Stoßrichtung bestimmt werden.
Weiters wird dabei die Kraft des Stoßes bestimmt.
Je weiter z.B. der Cue nach hinten gezogen wird, desto größer wird die Differenz der Start- und Endposition des Cues.
Somit kann z.B mehr Kraft auf den Cue ausgesetzt werden um die Stoßkraft zu verstärken.
Sobald die Maus losgelassen wird, wird die onMouseReleased-Methode aufgerufen und der Stoß wird ausgeführt.

#### Game
Die Klasse **Game** wurde um ein Enum **GameState** erweitert, damit das beliebige Platzieren der weißen Kugel implementiert werden kann.
Es gibt zwei verschiedene States, die das Game haben kann: 
- Der eine GameState ist *GAME_RUNNING*, welcher per Default eingestellt ist. Ist dieser State aktiv, kann eine Kugel normal angestoßen werden und die Positionen des Cue
werden angepasst.
- *SET_WHITE_BALL* hingegen ist der State, in dem sich das Spiel befindet, wenn das Foul *"It is a foul if the white ball does not touch any object ball."* begangen wurde. 
Dann darf der Spieler per Mouse-Click bestimmen, wohin er die weiße Kugel setzen möchte. Ist dieser State aktiv, reagiert Game ansonsten auf keine weiteren MouseClicked/ -Pressed oder -Released Events mehr.
Beim Setzen der Positon muss beachtet werden, dass auch eine gültige neue Position für die weiße Kugel bestimmt wird. Eine ungültige Position ist es, wenn 
sich die Kugel außerhalb des Tisches befinden würde oder sich auf einer anderen Kugel befindet bzw. mit der Position einer anderen Kugel überschneidet.

### Verwendung der Physics Engine dyn4j
Die Klasse **Physic** verwendet eine Physics Engine um prüfen zu können, welche Objekte des Spieles
miteinander interagieren. Neben den Interfaces RaycastListener, ContactListener und StepListener, implementiert
Physic auch das Interfaces BillardListener. In diesem zusätzlichen Interface werden alle weiteren benötigen
Methoden definiert, die zum Billard spielen notwendig sind. Die performStrike-Methode ist z.B. 
der Einstiegspunkt, wenn ein/e SpielerIn eine Kugel mit dem Cue angestoßen hat. Darin wird z.B. die gewirkte Kraft
und Richtung auf den Ball angewandt.

Außerdem hängt Physic eng mit der Klasse GameLogic zusammen. Die Klasse Physic erkennt, welche Ereignisse aufgetreten sind und teilt diese
dann der GameLogic mit, welche sich um die eigentliche Spielogik kümmert. Um dies zu realisieren, wurde das Observer Pattern verwendet.
Die Klasse Physic hat dafür verschiedene Listeners, die jeweils beim Initialisieren der Klasse Game
hinzugefügt werden. Die privaten Methoden notifyXXXListeners werden dann verwendet, um allen registrierten
Listeners mitzuteilen, dass etwas für sie Relevantes passiert ist. Bei uns ist die GameLogic die einzige Klasse, welche
eines dieser Interfaces implementiert, daher ist sie auch die einzige, die über ein derartiges Ereignis benachrichtigt wird. Potentiell könnten aber
mehrere verschiedene Klassen dafür verwendet werden.


### Spielelogik und Zusammenhang mit Physic
Um die Spielelogik getrennt von der Physic halten zu können, wurde die Klasse **GameLogic** erstellt.
Hierin wird alles abgehandelt, was mit der Spielelogik und Befolgung der Regeln zusammenhängt.
Die Klasse GameLogic implementiert die Interfaces BallStrikeListener, BallPocketedListener, BallsCollisionListener 
sowie ObjectsRestListener, damit auf die jeweiligen Ereignisse, die es von Physic bekommt, reagiert werden kann. 
Die Methode onStartAllObjectsRest wird dann aufgerufen, sobald ein Ball mit dem Cue angeschlagen wurde.
Dies initialisiert auch die für die GameLogic notwendigen Listen und deaktiviert das UI, sodass während des Spielzugs kein erneutes Anstoßen mit dem Cue möglich ist. Das UI bleibt solange deaktiviert, bis sich keine Kugeln mehr bewegen.

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

Beim Foul *"It is a foul if the white ball does not touch any object ball."* darf der Player, der das Foul nicht begangen hat, 
die weiße Kugel frei Hand setzen. Um dies implementieren zu können, wird der GameState verwendet, was in dem Kapitel über Game erklärt wird.

Damit versenkte Kugeln nicht mehr angezeigt werden, werden diese Kugeln mit der setVisible-Methode unsichtbar gemacht.
Wird eine Kugel so fest angestoßen, sodass sie über den Tisch gespielt wird und sich somit außerhalb des Tisches befindet, wird dies, außer es handelt sich um die weiße Kugel, nicht als Foul gewertet.
Da sich diese Kugel aber nicht mehr auf dem Tisch befindet wird sie ebenfalls unsichtbar gemacht.

Die Methode allow ermöglicht es, den Cue auch außerhalb des Tisches zu verwenden.
Dies wurde deshalb implementiert, damit Kugeln, die sich am Rande des Tisches befinden, direkt an dieser Kante von außen angespielt werden können.

Die Methode handleEndState kümmert sich zudem um das Zurücksetzen der Bälle in die Dreiecksform, wobei hier
die oberste Kugel (von der Startposition des Cue aus gesehen) freigelassen wird. Wenn also
14 Bälle (oder 15, falls der letzte Stoß die letzten zwei Kugeln versenkt hat) versenkt wurden, werden alle bisher versenkten
Bälle wieder in diesem unvollständigem Dreieck positioniert.


