package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.data.FileItem;
import es.jfp.localclientproject.elements.FileListItem;
import es.jfp.localclientproject.models.MainModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.Optional;


public final class MainController {

    private final MainModel model = MainModel.getInstance();

    @FXML
    private TreeView<FileItem> directoryTreeView;
    @FXML
    private Label directoryTreeTitleLabel;
    @FXML
    private ImageView createFolderActionIcon;
    @FXML
    private ListView<AnchorPane> currentDirectoryList;


    @FXML
    public void initialize() {
        directoryTreeView.setRoot(model.getTreeDirectory());

        directoryTreeView.setOnMouseClicked(mouseEvent -> {
            TreeItem<FileItem> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem!=null) {
                directoryTreeTitleLabel.setText(getPath(selectedItem));
                if (selectedItem.getValue().isDirectory()) {
                    updateCurrentDirectoryList(selectedItem.getChildren());
                }
            }
        });

        createFolderActionIcon.setOnMouseEntered(mouseDragEvent -> {
            createFolderActionIcon.setCursor(Cursor.HAND);
        });

        createFolderActionIcon.setOnMouseClicked(mouseEvent -> showCreateNewFolderAlert());

    }

    private void updateCurrentDirectoryList(ObservableList<TreeItem<FileItem>> children) {
        currentDirectoryList.getItems().clear();
        for (TreeItem<FileItem> child: children) {
            FileListItem item = new FileListItem(child.getValue().getName(), child.getValue().isDirectory());
            currentDirectoryList.getItems().add(item);
        }
        currentDirectoryList.refresh();
    }

    private void showCreateNewFolderAlert() {
        TextInputDialog createFolderAlert = new TextInputDialog();
        createFolderAlert.setTitle("Crear nueva carpeta");
        createFolderAlert.setHeaderText(null);
        createFolderAlert.setContentText("Nombre de la carpeta:");
        Button aceptarButton = (Button) createFolderAlert.getDialogPane().lookupButton(ButtonType.OK);
        aceptarButton.setDisable(true);
        createFolderAlert.getEditor().textProperty().addListener((observable, oldValue, newValue) -> aceptarButton.setDisable(newValue.isEmpty()));
        Optional<String> folderName = createFolderAlert.showAndWait();
        if (folderName.isPresent()) {
            System.out.println(folderName.get());
        }
    }

    private String getPath(TreeItem<FileItem> item) {
        StringBuilder pathBuilder = new StringBuilder(item.getValue().getName());
        TreeItem<FileItem> parent = item.getParent();
        while (parent != null) {
            pathBuilder.insert(0, parent.getValue() + "/");
            parent = parent.getParent();
        }
        return pathBuilder.toString();
    }
}
