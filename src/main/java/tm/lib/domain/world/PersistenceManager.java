package tm.lib.domain.world;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import tm.lib.domain.world.dto.WorldDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PersistenceManager {

    private static final String ROOT_FOLDER = "season";
    private static final String FILE_NAME_WORLD_JSON = "world.json";

    public static World loadWorld() {
        ObjectMapper mapper = new ObjectMapper();
        WorldDto worldDto;
        try {
            worldDto = mapper.readValue(new File(makeFilename(FILE_NAME_WORLD_JSON)), WorldDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return World.fromDto(worldDto);
    }

    public static void saveWorld(World world) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(makeFilename(FILE_NAME_WORLD_JSON));
        try {
            mapper.writeValue(file, world.toDto());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String makeFilename(String baseName) {
        Path folderPath = Paths.get(ROOT_FOLDER);
        if (!Files.exists(folderPath)) {
            try {
                Files.createDirectory(folderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return folderPath.resolve(baseName).toAbsolutePath().toString();
    }
}
