package org.ecn;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GameSaverTest {

    @Test
    void saveGame() {
        BoardController boardController = new BoardController(new Board(10), "Joueur 1", "Joueur 2");
        File targetFile = new File("saveGame.json");
        try {
            GameSaver.saveGame(boardController, targetFile);

            assertEquals(boardController, GameSaver.loadGame(targetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // automatically delete file on exit
         targetFile.deleteOnExit();
    }
}