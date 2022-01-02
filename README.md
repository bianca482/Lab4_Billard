# Systemarchitekturen Lab 4: Billard

## Allgemeines
Github Repository verfügbar unter: https://github.com/bianca482/Lab4_Billard

Zum Starten der Applikation wird Java Version 11 benötigt.
In der Klasse Main wurde die Größe der Applikation (SCENE_WIDTH, SCENE_HEIGHT) leicht abgeändert.

Das Jar-File *billard.jar* kann mittels Kommandozeile wie folgt gestartet werden:
*java -p  "<java_fx_path>" --add-modules javafx.controls,javafx.fxml -jar billard.jar* (wobei <java_fx_path> mit dem JAVA FX-Installationspfad des eigenen PCs angepasst werden muss.)

Bianca:
Player-Klasse, warum erstellt
Physics Klasse und Listeners (Observer Pattern), Billard Listener
Gamelogic ausgelagert die Interfaces implementiert
Grob erklären wie Physics und Gamelogic funktioniert

Ilona:
UI deaktivieren während Spiels
Außerhalb von Tisch spielen möglich
Bälle die außerhalb vom Table sind (z.B. durch zu festes schießen) werden als versenkt angesehen außer beim weißen Ball
Ball versenken setVisible --> unsichtbar machen
Ball Position --> aktuelle Position gespeichert
Rendering von Cue , Beschreiben wie Cue funktioniert (onMousedragged, onMouseReleased,...) Kraft usw.