package es.jfp.localclientproject.models;

import es.jfp.SerialFile;
import es.jfp.SerialMap;
import es.jfp.localclientproject.controllers.MainController;
import es.jfp.localclientproject.data.FileItem;
import es.jfp.localclientproject.elements.ProgressWidget;
import es.jfp.localclientproject.repositorys.ServerRepository;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public final class MainModel {

    private static MainModel instance;
    private final ServerRepository serverRepo = ServerRepository.getInstance();

    private File selectedFile;
    private ToolBar procesToolbar;
    private Method controllerUpdateDirectory;
    private SerialMap currentSerialMap;

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
        FileItem rootItem = new FileItem(rootFile.getFileName(), true, Path.of(rootFile.getDirectory()), rootFile.getSize());
        TreeItem<FileItem> root = new TreeItem<>(rootItem);
        List<TreeItem<FileItem>> children = createTreeItem(rootFile.getChildren(), rootFile);
        root.getChildren().addAll(children);
        return root;
    }

    private List<TreeItem<FileItem>> createTreeItem(List<SerialFile> files, SerialFile rootFile) {
        List<TreeItem<FileItem>> treeItems = new LinkedList<>();
        files.forEach(child -> {
            Path path = Path.of(rootFile.getDirectory(), child.getDirectory());
            FileItem fileItem = new FileItem(child.getFileName(), child.isFolder(), path, child.getSize());
            TreeItem<FileItem> treeItem = new TreeItem<>(fileItem);
            if (child.isFolder()) {
                List<TreeItem<FileItem>> children = createTreeItem(child.getChildren(), rootFile);
                treeItem.getChildren().addAll(children);
            }
            treeItems.add(treeItem);
        });
        return treeItems;
    }

    public boolean deleteFile(String path) {
        return serverRepo.deleteFile(path);
    }

    public boolean requestPing() {
        return serverRepo.ping();
    }

    public boolean deleteFolder(String path) {
        return serverRepo.deleteFolder(path);
    }

    public boolean createNewFolder(String path) {
        return serverRepo.createNewFolder(path);
    }

    public void uploadFile(File file, String relativePath) {
        serverRepo.uploadFile(file, relativePath);
    }



    public void downloadFile(String destination, String filePath, long size) {
        serverRepo.downloadFile(destination, filePath, size);
    }

    public void sendFileToModel(File file) {
        this.selectedFile = file;
    }

    public File getFileToModel() {
        return selectedFile;
    }

    public void requestControllerUpdateDirectory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            MainController controller = loader.getController();
            this.controllerUpdateDirectory.invoke(controller);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
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
