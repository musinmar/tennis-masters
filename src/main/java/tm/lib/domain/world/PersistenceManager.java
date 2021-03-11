package tm.lib.domain.world;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import tm.lib.domain.core.Knight;
import tm.lib.domain.core.dto.KnightDto;
import tm.lib.domain.world.dto.WorldDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PersistenceManager {

    private static final String ROOT_FOLDER = "season";
    private static final String FILE_NAME_WORLD_JSON = "world.json";
    private static final String FILE_NAME_PLAYERS_JSON = "default_players.json";

    public static boolean canLoadWorld() {
        return new File(makeFilename(FILE_NAME_WORLD_JSON)).exists();
    }

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

    public static List<Knight> loadDefaultPlayers() {
        ObjectMapper mapper = new ObjectMapper();
        List<KnightDto> knightDtos;
        try {
            knightDtos = mapper.readValue(new File(FILE_NAME_PLAYERS_JSON), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return knightDtos.stream().map(Knight::fromDto).collect(toList());
    }

    public static void saveDefaultPlayers(List<Knight> knights) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(FILE_NAME_PLAYERS_JSON);
        try {
            List<KnightDto> dtos = knights.stream().map(Knight::toDto).collect(toList());
            mapper.writeValue(file, dtos);
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
