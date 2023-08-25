package org.rm20.enderpearlfixer.utils;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.rm20.enderpearlfixer.EnderPearlFixer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;

public class IOUtils {
    public final static String PLUGIN_ROOT = "plugins/" + EnderPearlFixer.class.getSimpleName() + "/";
    public final static String LOGS_ROOT = PLUGIN_ROOT + "logs/";



    public static void csvAppend(String file, String message) {
        write(LOGS_ROOT + file + ".csv", List.of(StandardOpenOption.APPEND), writer ->
        {
            try {
                writer.append(String.format("%n%s", message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Overwrites existing content
    public static void fileWrite(String file, BiConsumer<BufferedWriter, List<String>> consumer) {
        write(file, List.of(), writer -> {
            @NotNull final List<String> outputs = new ArrayList<>();
            consumer.accept(writer, outputs);
            try {
                writer.write(String.join(System.lineSeparator(), outputs));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void write(String fileName, List<StandardOpenOption> openOptions, Consumer<BufferedWriter> consumer) {
                try {
                    final Path path = Paths.get(fileName);
                    final File file = path.toFile();
                    if (!file.exists())
                        file.createNewFile();

                    final StandardOpenOption[] options = openOptions.toArray(StandardOpenOption[]::new);
                    try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8, options)) {
                        consumer.accept(writer);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

    }

    @SneakyThrows
    public static File getPluginFile(String path) {
        return getFile(PLUGIN_ROOT + path);
    }

    @SneakyThrows
    public static File getPluginFolder(String path) {
        return getFolder(PLUGIN_ROOT + path);
    }

    @SneakyThrows
    public static File getLogsFile(String path) {
        return getFile(LOGS_ROOT + path);
    }

    @SneakyThrows
    public static File getLogsFolder(String path) {
        return getFolder(LOGS_ROOT + path);
    }

    @SneakyThrows
    public static File getFile(String path) {
        File file = Paths.get(path).toFile();
        if (!file.exists()){
            file.createNewFile();
        }
        return file;
    }

    @SneakyThrows
    public static File getFolder(String path) {
        File file = Paths.get(path).toFile();
        if (!file.exists()) file.mkdir();
        return file;
    }

    @SneakyThrows
    public static YamlConfiguration getNexusConfig(String path) {
        return YamlConfiguration.loadConfiguration(getPluginFile(path));
    }

    @SneakyThrows
    public static YamlConfiguration getConfig(String path) {
        return YamlConfiguration.loadConfiguration(getFile(path));
    }

}
