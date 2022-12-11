package org.ecn;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameSaver {
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
