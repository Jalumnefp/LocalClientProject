package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.data.FileItem;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;

public class DirectoryTreeItem extends TreeCell<FileItem> {

    @Override
    public void updateItem(FileItem item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item != null) {
            if (item.isDirectory()) {
                setText(item.getName());
            }
        }
    }
}
