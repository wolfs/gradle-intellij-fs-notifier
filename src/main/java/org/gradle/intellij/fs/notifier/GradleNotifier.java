package org.gradle.intellij.fs.notifier;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;

public class GradleNotifier {

    private final NotifyGradleAboutFileChangeListener fileChangeListener;

    public GradleNotifier() {
        MessageBusConnection connection = ApplicationManager.getApplication().getMessageBus().connect();
        fileChangeListener = new NotifyGradleAboutFileChangeListener();
        connection.subscribe(VirtualFileManager.VFS_CHANGES, fileChangeListener);
    }

    public NotifyGradleAboutFileChangeListener getFileChangeListener() {
        return fileChangeListener;
    }

    public static void printMessage(String path, String type) {
        System.out.println(path + " " + type);
    }

}
