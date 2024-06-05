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
import java.nio.file.Path;
import java.util.*;


public final class MainController {

    private final MainModel model = MainModel.getInstance();
    private final ResourceBundle resourceBundle = App.getResourceBundle();
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
        model.setProcessToolbar(processToolBar);
        try {
            model.setControllerUpdateDirectory(this.getClass().getMethod("setUpDirectoryElements"));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

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

    public void setUpDirectoryElements() {
        directoryTreeView.setRoot(model.getTreeDirectory());
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
            currentDirectoryList.getItems().add(new FileListItem(
                    resourceBundle.getString("empty_folder"),
                    null, true, null, null));
        }
        currentDirectoryList.refresh();
    }

    private void downloadFile(String filePath) {
        String fileName = getPath(filePath);

        if (fileName != null && Path.of(fileName).toFile().isFile()) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(fileName);
            File selectedDirectory = fileChooser.showSaveDialog(App.getRootStage());
            if (selectedDirectory.isFile()) {
                model.downloadFile(selectedDirectory.getPath(), filePath.replace(rootPath, ""));
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("¡Ojo!");
            alert.setHeaderText(null);
            alert.setContentText(resourceBundle.getString("to_download_folder_message"));
            alert.showAndWait();
        }

        System.out.println("end download");
    }

    @Deprecated
    public void showResultAlert(String action, String ctx, boolean success) {
        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(String.format(success ?
                "La operación %s en %s ha sido realizada con éxito" :
                "Ha habido un error con la operación %s en %s",
                action, ctx
        ));
        alert.showAndWait();
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
        alert.setContentText(String.format("¿%s: %s?",
                resourceBundle.getString("delete_confirmation_message"), getPath(filePath)));
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.setTitle("¡Ojo!");
        if (alert.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            if (isDirectory) {
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
                alert2.setContentText(resourceBundle.getString("folder_recursive_delete_advise"));
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
