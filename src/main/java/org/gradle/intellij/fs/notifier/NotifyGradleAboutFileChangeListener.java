package org.gradle.intellij.fs.notifier;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class NotifyGradleAboutFileChangeListener implements BulkFileListener {
    private static final Logger LOGGER = Logger.getInstance(NotifyGradleAboutFileChangeListener.class);

    private final String basePath;
    private final Path changesLocation;
    private final String ignoredLocation;
    private final Set<String> writtenChanges = new HashSet<>();

    public NotifyGradleAboutFileChangeListener(String basePath) {
        LOGGER.info("Base path for changed detection file: " + basePath);
        this.basePath = basePath;
        this.changesLocation = Paths.get(basePath, ".gradle/idea-change-tracking");
        this.ignoredLocation = changesLocation.toString();
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        events.forEach(
                event -> {
                    GradleNotifier.printMessage(event.getPath(), "BULK after");
                }
        );
        if (events.size() > 1) {
            LOGGER.info("More than one event");
        } else {
            LOGGER.info("Single event - passing to Gradle");
            writeChanges(events);
        }
    }

    private void writeChanges(@NotNull List<? extends VFileEvent> events) {
        if (!Files.exists(changesLocation)) {
            writtenChanges.clear();
        }
        List<String> allChanges = events.stream()
                .map(VFileEvent::getPath)
                .filter(path -> !writtenChanges.contains(path) && path.startsWith(basePath) && !path.equals(ignoredLocation))
                .collect(Collectors.toList());
        try {
            writtenChanges.addAll(allChanges);
            Files.write(changesLocation, allChanges, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
