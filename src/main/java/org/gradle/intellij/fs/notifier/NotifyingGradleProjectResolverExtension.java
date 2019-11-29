package org.gradle.intellij.fs.notifier;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.service.project.AbstractProjectResolverExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NotifyingGradleProjectResolverExtension extends AbstractProjectResolverExtension {
    private final static Logger LOG = Logger.getInstance(NotifyingGradleProjectResolverExtension.class);

    @Override
    public void enhanceTaskProcessing(@NotNull List<String> taskNames, @Nullable String jvmParametersSetup, @NotNull Consumer<String> initScriptConsumer) {
        Collection<String> collectedChanges = ApplicationManager.getApplication().getComponent(GradleNotifier.class).getFileChangeListener().getCollectedChanges();

        if (collectedChanges.isEmpty()) {
            return;
        }

        String changesAsArray = collectedChanges.stream()
                .collect(Collectors.joining("', '", "['", "']"));

        try (InputStream stream = getClass().getResourceAsStream("/org/gradle/intellij/fs/notifier/notifyChanges.gradle")) {
            String notifyChangesScript = StreamUtil.readText(stream, StandardCharsets.UTF_8).replaceFirst(Pattern.quote("${CHANGED_LOCATIONS}"), Matcher.quoteReplacement(changesAsArray));
            initScriptConsumer.consume(notifyChangesScript);
            LOG.warn("Init script appended");
        }
        catch (IOException e) {
            LOG.info(e);
        }
    }
}
