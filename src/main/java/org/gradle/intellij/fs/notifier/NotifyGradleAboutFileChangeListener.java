package org.gradle.intellij.fs.notifier;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.service.execution.GradleExecutionHelper;
import org.jetbrains.plugins.gradle.settings.GradleExecutionSettings;
import org.jetbrains.plugins.gradle.settings.GradleSettings;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class NotifyGradleAboutFileChangeListener implements BulkFileListener {
    private static final Logger LOGGER = Logger.getInstance(NotifyGradleAboutFileChangeListener.class);

    private final Set<String> collectedChanges = new HashSet<>();
    private final Project project;
    private final GradleExecutionHelper myExecutionHelper = new GradleExecutionHelper();

    public NotifyGradleAboutFileChangeListener(Project project) {
        this.project = project;
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

        String gradleVmOptions = GradleSettings.getInstance(project).getGradleVmOptions();
        ExternalSystemTaskExecutionSettings settings = new ExternalSystemTaskExecutionSettings();
        settings.setExecutionName("Notify Gradle about changes");
        settings.setExternalProjectPath(project.getBasePath());
        settings.setVmOptions(gradleVmOptions);
        settings.setExternalSystemIdString(GradleConstants.SYSTEM_ID.getId());

        GradleExecutionSettings executionSettings = ExternalSystemApiUtil.getExecutionSettings(project, settings.getExternalProjectPath(), GradleConstants.SYSTEM_ID);

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            myExecutionHelper.execute(project.getBasePath(), executionSettings, projectConnection -> {
                System.out.println("Doing something with project connection");
                projectConnection.action(new TellGradleToInvalidateLocations(new ArrayList<>(collectedChanges)))
                        .setStandardOutput(System.out)
                        .run();
                return null;
            });
        });
    }
}
