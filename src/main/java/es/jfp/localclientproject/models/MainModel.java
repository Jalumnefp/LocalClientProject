package es.jfp.localclientproject.models;

import es.jfp.localclientproject.data.FileItem;
import es.jfp.localclientproject.repositorys.ServerRepository;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.*;
import java.net.Socket;
import java.util.*;

public final class MainModel {

    private static MainModel instance;
    private final ServerRepository serverRepo = ServerRepository.getInstance();

    private MainModel() {}

    public static MainModel getInstance() {
        synchronized (MainModel.class) {
            if (instance==null) {
                instance = new MainModel();
            }
            return instance;
        }
    }

    public TreeItem<FileItem> getTreeDirectory() {
        Map<String, List<String[]>> tree = serverRepo.getDirectoryMap();
        return createTreeItem(tree);
    }

    private TreeItem<FileItem> createTreeItem(Map<String, List<String[]>> directorios) {
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
                if (child[1].equals("d")) {
                    TreeItem<FileItem> item = new TreeItem<>(new FileItem(child[0], true));
                    item.getChildren().addAll(createTreeItem(directorios, child[0]));
                    items.add(item);
                } else {
                    items.add(new TreeItem<>(new FileItem(child[0], false)));
                }
            });
        }
        return items;
    }

}
