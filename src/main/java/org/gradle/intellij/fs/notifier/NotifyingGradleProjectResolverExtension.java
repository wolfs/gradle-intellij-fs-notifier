package org.gradle.intellij.fs.notifier;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.service.project.AbstractProjectResolverExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NotifyingGradleProjectResolverExtension extends AbstractProjectResolverExtension {
    private final static Logger LOG = Logger.getInstance(NotifyingGradleProjectResolverExtension.class);

    @Override
    public void enhanceTaskProcessing(@NotNull List<String> taskNames, @Nullable String jvmParametersSetup, @NotNull Consumer<String> initScriptConsumer) {
        try (InputStream stream = getClass().getResourceAsStream("/org/gradle/intellij/fs/notifier/notifyChanges.groovy")) {
            String notifyChangesScript = StreamUtil.readText(stream, StandardCharsets.UTF_8);
//            ApplicationManager.getApplication().getComponent()
            initScriptConsumer.consume(notifyChangesScript);
            LOG.warn("Init script appended");
        }
        catch (IOException e) {
            LOG.info(e);
        }
    }
}
