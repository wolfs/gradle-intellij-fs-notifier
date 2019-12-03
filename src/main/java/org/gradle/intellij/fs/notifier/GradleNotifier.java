package org.gradle.intellij.fs.notifier;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;

public class GradleNotifier implements ProjectComponent {

    private final Project project;

    public GradleNotifier(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        MessageBusConnection connection = ApplicationManager.getApplication().getMessageBus().connect();
        connection.subscribe(VirtualFileManager.VFS_CHANGES, new NotifyGradleAboutFileChangeListener(project));
    }

    public static void printMessage(String path, String type) {
        System.out.println(path + " " + type);
    }
}
