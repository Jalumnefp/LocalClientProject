package es.jfp.localclientproject.data;

import java.nio.file.Path;

public class FileItem {

    private long id;
    private String name;
    private Path path;
    private boolean isDirectory;

    public FileItem(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public FileItem(String name, boolean isDirectory, Path path) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }
}
