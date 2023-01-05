package org.ecn;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class GameSaver {

    private GameSaver() {
        throw new IllegalStateException("Utility class: " + getClass());
    }

    public static void saveGame(BoardController boardController, File targetFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Serialize Java object info JSON file.
        mapper.writeValue(targetFile, boardController);
    }

    public static BoardController loadGame(File targetFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Deserialize JSON file into Java object.
        return mapper.readValue(targetFile, BoardController.class);
    }
}
