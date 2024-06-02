package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.data.FileItem;
import es.jfp.localclientproject.elements.CreateNewFolderAlert;
import es.jfp.localclientproject.elements.FileListItem;
import es.jfp.localclientproject.models.MainModel;
import es.jfp.localclientproject.repositorys.ServerRepository;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.dialog.ExceptionDialog;

import java.io.File;
import java.io.IOException;
import java.util.*;


public final class MainController {

    private final MainModel model = MainModel.getInstance();
    @FXML
    private Button reloadDirectorys;
    @FXML
    private ToolBar processToolBar;
    @FXML
    private  MenuItem menuItemReturnServerChooser;
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
    private Thread directoryListener;


    @FXML
    public void initialize() {
        MainModel.getInstance().setProcessToolbar(processToolBar);

        // guardar directorios en el modelo
        directoryTreeView.setRoot(MainModel.getInstance().getTreeDirectory());
        directoryTreeView.setShowRoot(true);
        setUpDirectoryElements();
        //startDirectoryListenerThread();


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

        reloadDirectorys.setOnMouseClicked(mouseEvent -> {
            TreeItem<FileItem> directoryTreeItem = MainModel.getInstance().getTreeDirectory();
            Platform.runLater(() -> {
                directoryTreeView.setRoot(directoryTreeItem);
                setUpDirectoryElements();
            });
        });

        createFolderActionIcon.setOnMouseClicked(mouseEvent -> showCreateNewFolderDialog());

        uploadActionIcon.setOnMouseClicked(mouseEvent -> showUploadFileDialog());

        menuItemUploadFile.setOnAction(actionEvent -> showUploadFileDialog());

        menuItemCreateNewFolder.setOnAction(actionEvent -> showCreateNewFolderDialog());

        menuItemReturnServerChooser.setOnAction(actionEvent -> {
            try {
                ServerRepository.getInstance().closeConnection();
                if (directoryListener!=null) {
                    directoryListener.interrupt();
                }
                App.loadSceneInRootStage("search-server-view", stage -> {
                    stage.setWidth(400);
                    stage.setHeight(300);
                    stage.centerOnScreen();
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        menuItemClose.setOnAction(actionEvent -> {
            if (directoryListener!=null) {
                directoryListener.interrupt();
            }Stage stage = (Stage) uploadActionIcon.getScene().getWindow();
            stage.close();
        });

    }

    private void startDirectoryListenerThread() {
        if (directoryListener == null) {
            directoryListener = new Thread(() -> {
                while (ServerRepository.getInstance().socketIsRunning()) {
                    TreeItem<FileItem> directoryTreeItem = MainModel.getInstance().getTreeDirectory();
                    Platform.runLater(() -> {
                        directoryTreeView.setRoot(directoryTreeItem);
                        setUpDirectoryElements();
                    });
                }
            });
            directoryListener.start();
        }
    }

    private void setUpDirectoryElements() {
        updateCurrentDirectoryList(directoryTreeView.getRoot().getChildren());
        rootPath = directoryTreeView.getRoot().getValue().getName();
        directoryTreeTitleLabel.setText(rootPath);
    }

    private void showCreateNewFolderDialog() {
        CreateNewFolderAlert createNewFolderAlert = new CreateNewFolderAlert();
        Optional<String> folderName = createNewFolderAlert.showAndWait();
        if (folderName.isPresent()) {
            String path = directoryTreeTitleLabel.getText() + '/' + folderName.get();
            model.createNewFolder(path.replace(rootPath, ""));
        }
    }

    private void updateCurrentDirectoryList(ObservableList<TreeItem<FileItem>> children) {
        currentDirectoryList.getItems().clear();
        if (!children.isEmpty()) {
            for (TreeItem<FileItem> child: children) {
                FileListItem item = new FileListItem(
                        child.getValue().getName(), getPath(child), child.getValue().isDirectory(), this::downloadFile, this::deleteFile);
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
        System.out.println("end download");
    }

    private void showUploadFileDialog() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(App.getRootStage());

        if (selectedFile != null) {
            String path = directoryTreeTitleLabel.getText() + '/' + selectedFile.getName();
            model.uploadFile(selectedFile, path.replace(rootPath, ""));
        }

    }

    private void deleteFile(String filePath, boolean isDirectory) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Estas seguro/a de que quieres eliminar: " + getPath(filePath) + '?');
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.setTitle("¡Ojo!");
        if (alert.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            if (isDirectory) {
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
                alert2.setContentText("Al eliminar una carpeta se eliminará su contenido. ¿Quieres proseguir?");
                alert2.setGraphic(null);
                alert2.setHeaderText(null);
                alert2.setTitle("¡Ojo!");
                if (alert2.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
                    model.deleteFolder(filePath.replace(rootPath, ""));
                }
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

    public void createProgressBar() {
        Platform.runLater(() -> {
            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefHeight(100);
            processToolBar.getItems().add(progressBar);
        });
    }

}
