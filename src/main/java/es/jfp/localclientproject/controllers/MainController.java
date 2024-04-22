package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.data.FileItem;
import es.jfp.localclientproject.elements.*;
import es.jfp.localclientproject.models.MainModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.dialog.ExceptionDialog;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public final class MainController {

    private final MainModel model = MainModel.getInstance();
    @FXML
    private SplitPane splitPane;
    @FXML
    private MenuItem menuItemClose;
    @FXML
    private MenuItem menuItemUploadFile;
    @FXML
    private MenuItem menuItemCreateNewFolder;
    @FXML
    private Button userProfileButton;

    @FXML
    private BreadCrumbBar<String> directoryBreadCrumbBar;
    @FXML
    private TreeView<FileItem> directoryTreeView;
    @FXML
    private Label directoryTreeTitleLabel;
    @FXML
    private Button createFolderActionIcon;
    @FXML
    private Button uploadActionIcon;
    @FXML
    private ListView currentDirectoryList;

    private String rootPath;


    @FXML
    public void initialize() {
        directoryTreeView.setRoot(model.getTreeDirectory());
        rootPath = directoryTreeView.getRoot().getValue().getName();
        directoryTreeTitleLabel.setText(rootPath);

        //directoryTreeView.setCellFactory(fileItemTreeView -> new DirectoryTreeItem());
        //FontAwesome.Glyph.USER

        updateCurrentDirectoryList(directoryTreeView.getRoot().getChildren());

        directoryTreeView.setOnMouseClicked(mouseEvent -> {
            TreeItem<FileItem> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem!=null) {
                directoryTreeTitleLabel.setText(getPath(selectedItem));
                if (selectedItem.getValue().isDirectory()) {
                    updateCurrentDirectoryList(selectedItem.getChildren());
                }
            }
        });

        userProfileButton.setOnMouseClicked(mouseEvent -> {
            Alert alert = null;
            try {
                String fxmlUrl = App.getCurrentUser() != null ? "user-profile-view.fxml" : "login-view.fxml";
                Pane pane = FXMLLoader.load(Objects.requireNonNull(App.class.getResource(fxmlUrl)));
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.getButtonTypes().removeAll(ButtonType.OK);
                Button cancellButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
                cancellButton.setVisible(false);
                alert.getDialogPane().setContent(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
            alert.showAndWait();
        });

        createFolderActionIcon.setOnMouseClicked(mouseEvent -> showCreateNewFolderDialog());

        uploadActionIcon.setOnMouseClicked(mouseEvent -> showUploadFileDialog());

        menuItemUploadFile.setOnAction(actionEvent -> showUploadFileDialog());

        menuItemCreateNewFolder.setOnAction(actionEvent -> showCreateNewFolderDialog());

        menuItemClose.setOnAction(actionEvent -> {
            Stage stage = (Stage) uploadActionIcon.getScene().getWindow();
            stage.close();
        });

    }

    private void showCreateNewFolderDialog() {
        CreateNewFolderAlert createNewFolderAlert = new CreateNewFolderAlert();
        Optional<String> folderName = createNewFolderAlert.showAndWait();
        if (folderName.isPresent()) {
            String path = directoryTreeTitleLabel.getText() + '/' + folderName.get();
            model.createNewFolder(path.replace(rootPath, ""));
        }
    }

    private void showUploadFileDialog() {
        try {
            Pane uploadDialogView = FXMLLoader.load(App.class.getResource("upload-file-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(uploadDialogView);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Upload file");
            stage.showAndWait();
            File selectedFile = model.getFileToModel();
            if (selectedFile != null) {
                String path = directoryTreeTitleLabel.getText() + '/' + selectedFile.getName();
                model.uploadFile(selectedFile, path.replace(rootPath, ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionDialog exceptionDialog = new ExceptionDialog(e);
            exceptionDialog.showAndWait();
        }
    }

    private void updateCurrentDirectoryList(ObservableList<TreeItem<FileItem>> children) {
        currentDirectoryList.getItems().clear();
        if (!children.isEmpty()) {
            for (TreeItem<FileItem> child: children) {
                FileListItem item = new FileListItem(child.getValue().getName(), child.getValue().isDirectory());
                currentDirectoryList.getItems().add(item);
            }
        } else {
            currentDirectoryList.getItems().add(new FileListItem("EMPTY FOLDER", true));
        }
        currentDirectoryList.refresh();
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
