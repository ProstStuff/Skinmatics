package dev.proststuff.skinmatics.api.client.impl.gui;

import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.List;

public interface FileEventListener {
    default void onFilesDropped(@NonNull List<Path> files) {}
}
