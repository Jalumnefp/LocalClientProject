package es.jfp.localclientproject.models;

import es.jfp.SerialFile;
import es.jfp.SerialMap;
import es.jfp.localclientproject.data.FileItem;
import es.jfp.localclientproject.elements.ProgressWidget;
import es.jfp.localclientproject.repositorys.ServerRepository;
import javafx.application.Platform;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.*;

public final class MainModel {

    private static MainModel instance;
    private final ServerRepository serverRepo = ServerRepository.getInstance();

    private File selectedFile;
    private ToolBar procesToolbar;

    private MainModel() {}

    public static MainModel getInstance() {
        synchronized (MainModel.class) {
            if (instance==null) {
                instance = new MainModel();
            }
            return instance;
        }
    }

    public void setProcessToolbar(ToolBar toolbar) {
        this.procesToolbar = toolbar;
    }

    public void insertOnProcessToolbar(ProgressWidget progressWidget) {
        Platform.runLater(() -> this.procesToolbar.getItems().add(progressWidget));
    }

    public void removeProgressWidget(ProgressWidget progressWidget) {
        Platform.runLater(() -> procesToolbar.getItems().remove(progressWidget));
    }

    public TreeItem<FileItem> getTreeDirectory() {

        SerialMap serialMap = serverRepo.getDirectoryMap();
        return createTreeItem(serialMap.getRootFile());
    }

    private TreeItem<FileItem> createTreeItem(SerialFile rootFile) {
        FileItem rootItem = new FileItem(rootFile.getFileName(), true);
        TreeItem<FileItem> root = new TreeItem<>(rootItem);
        List<TreeItem<FileItem>> children = createTreeItem(rootFile.getChildren());
        root.getChildren().addAll(children);
        return root;
    }

    private List<TreeItem<FileItem>> createTreeItem(List<SerialFile> files) {
        List<TreeItem<FileItem>> treeItems = new LinkedList<>();
        files.forEach(child -> {
            FileItem fileItem = new FileItem(child.getFileName(), child.isFolder());
            TreeItem<FileItem> treeItem = new TreeItem<>(fileItem);
            if (child.isFolder()) {
                List<TreeItem<FileItem>> children = createTreeItem(child.getChildren());
                treeItem.getChildren().addAll(children);
            }
            treeItems.add(treeItem);
        });
        return treeItems;
    }

    public void deleteFile(String path) {
        serverRepo.deleteFile(path);
    }

    public void deleteFolder(String path) {
        serverRepo.deleteFolder(path);
    }

    public void createNewFolder(String path) {
        serverRepo.createNewFolder(path);
    }

    public void uploadFile(File file, String relativePath) {
        serverRepo.uploadFile(file, relativePath);
    }

    public void downloadFile(String destination, String filePath) {
        serverRepo.downloadFile(destination, filePath);
    }

    public void sendFileToModel(File file) {
        this.selectedFile = file;
    }

    public File getFileToModel() {
        return selectedFile;
    }

    public void updateProgressBar() {

    }

    /*private TreeItem<FileItem> createTreeItem(Map<String, List<String[]>> directorios) {
        TreeItem<FileItem> root;
        if (directorios != null) {
            String rootName = directorios.get("ROOT").get(0)[0];
            FileItem rootItem = new FileItem(rootName, true);
            root = new TreeItem<>(rootItem);
            root.getChildren().addAll(createTreeItem(directorios, root.getValue().getName()));
        } else {
            root = new TreeItem<>(new FileItem("No se ha encontrado nada!", false));
        }
        return root;
    }

    private List<TreeItem<FileItem>> createTreeItem(Map<String, List<String[]>> directorios, String parentKey) {
        List<TreeItem<FileItem>> items = new LinkedList<>();
        if (directorios.get(parentKey) != null) {
            directorios.get(parentKey).forEach(child -> {
                String fileName = child[0].substring(0, child[0].indexOf('?'));
                if (child[1].equals("d")) {
                    TreeItem<FileItem> item = new TreeItem<>(new FileItem(fileName, true));
                    item.getChildren().addAll(createTreeItem(directorios, child[0]));
                    items.add(item);
                } else {
                    items.add(new TreeItem<>(new FileItem(fileName, false)));
                }
            });
        }
        return items;
    }*/

}
