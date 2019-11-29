package org.gradle.intellij.fs.notifier;

import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import org.jetbrains.annotations.NotNull;

class GradleVirtualFileListener implements VirtualFileListener {
    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        GradleNotifier.printMessage(event.getFile().getPath(), "Created");
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        GradleNotifier.printMessage(event.getFile().getPath(), "Deleted");
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        GradleNotifier.printMessage(event.getOldParent().getPath(), "Moved");
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        GradleNotifier.printMessage(event.getOriginalFile().getPath(), "Copied");
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        GradleNotifier.printMessage(event.getFile().getPath(), "Property changed");
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        GradleNotifier.printMessage(event.getFile().getPath(), "Contents changed");
    }

}
