package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.data.FileItem;
import es.jfp.localclientproject.elements.CreateNewFolderAlert;
import es.jfp.localclientproject.elements.FileListItem;
import es.jfp.localclientproject.models.MainModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.dialog.ExceptionDialog;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.ResourceBundle;


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
    private ListView<FileListItem> currentDirectoryList;

    private String rootPath;


    @FXML
    public void initialize() {
        directoryTreeView.setRoot(model.getTreeDirectory());
        rootPath = directoryTreeView.getRoot().getValue().getName();
        directoryTreeTitleLabel.setText(rootPath);

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
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlUrl));
                fxmlLoader.setResources(ResourceBundle.getBundle("es/jfp/localclientproject/bundle/strings_es"));
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setGraphic(null);
                alert.getButtonTypes().removeAll(ButtonType.OK);
                Button cancellButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
                cancellButton.setVisible(false);
                alert.getDialogPane().setContent(fxmlLoader.load());
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
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("upload-file-view.fxml"));
            fxmlLoader.setResources(ResourceBundle.getBundle("es/jfp/localclientproject/bundle/strings_es"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());
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
                FileListItem item = new FileListItem(child.getValue().getName(), getPath(child), child.getValue().isDirectory(), this::downloadFile, this::deleteFile);
                currentDirectoryList.getItems().add(item);
            }
        } else {
            currentDirectoryList.getItems().add(new FileListItem("EMPTY FOLDER", null, true, null, null));
        }
        currentDirectoryList.refresh();
    }

    private void downloadFile(String filePath) {
        String fileName = getPath(filePath);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(fileName);
        File selectedDirectory = fileChooser.showSaveDialog(App.getRootStage());

        if (selectedDirectory != null) {
            model.downloadFile(selectedDirectory.getPath(), filePath.replace(rootPath, ""));
        }
        System.out.println("wwwwwwwwwwwwwwww");
    }

    private void deleteFile(String filePath, boolean isDirectory) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Estas seguro/a de que quieres eliminar: " + getPath(filePath));
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.setTitle("¡Ojo!");
        if (alert.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            if (isDirectory) {
                // drct
            } else {
                model.deleteFile(filePath.replace(rootPath, ""));
            }
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

    private String getPath(String path) {
        String[] pathParts = path.split("/");
        return pathParts[pathParts.length - 1];
    }

}
