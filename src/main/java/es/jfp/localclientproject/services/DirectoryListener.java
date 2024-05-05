package es.jfp.localclientproject.services;

import es.jfp.localclientproject.data.FileItem;
import es.jfp.localclientproject.models.MainModel;
import javafx.scene.control.TreeView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

public class DirectoryListener implements Runnable {

    private TreeView<FileItem> directoryTreeView;

    public DirectoryListener(TreeView<FileItem> socketInputStream) {
        this.directoryTreeView = socketInputStream;
    }

    @Override
    public void run() {


    }
}
