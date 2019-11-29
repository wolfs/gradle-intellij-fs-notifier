package org.gradle.intellij.fs.notifier;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class NotifyGradleAboutFileChangeListener implements BulkFileListener {
    private static final Logger LOGGER = Logger.getInstance(NotifyGradleAboutFileChangeListener.class);

    private final Set<String> collectedChanges = new HashSet<>();

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
            collectChanges(events);
        }
    }

    public Collection<String> getCollectedChanges() {
        ImmutableList<String> copiedChanges = ImmutableList.copyOf(collectedChanges);
        collectedChanges.clear();
        return copiedChanges;
    }

    private void collectChanges(@NotNull List<? extends VFileEvent> events) {
        List<String> allChanges = events.stream()
                .map(VFileEvent::getPath)
                .filter(path -> !collectedChanges.contains(path))
                .collect(Collectors.toList());
        collectedChanges.addAll(allChanges);
    }
}
