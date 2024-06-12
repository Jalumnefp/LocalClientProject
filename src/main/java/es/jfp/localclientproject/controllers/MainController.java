package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.data.FileItem;
import es.jfp.localclientproject.data.Server;
import es.jfp.localclientproject.elements.CreateNewFolderAlert;
import es.jfp.localclientproject.elements.FileListItem;
import es.jfp.localclientproject.models.MainModel;
import es.jfp.localclientproject.repositorys.ServerRepository;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.dialog.ExceptionDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;


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
            boolean oldState = App.getCurrentUser() == null;
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

            boolean newState = App.getCurrentUser() == null;
            if (oldState != newState) {
                setUpDirectoryElements();
            }
        });

        reloadDirectorys.setOnMouseClicked(mouseEvent -> Platform.runLater(this::setUpDirectoryElements));

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
                    Platform.runLater(this::setUpDirectoryElements);
                }
            });
            directoryListener.start();
        }
    }

    public void setUpDirectoryElements() {
        TreeItem<FileItem> treeItem = model.getTreeDirectory();
        directoryTreeView.setRoot(treeItem);
        updateCurrentDirectoryList(directoryTreeView.getRoot().getChildren());
        rootPath = directoryTreeView.getRoot().getValue().getName();
        directoryTreeTitleLabel.setText(rootPath);
    }

    /*private void delete(Path path) {
        TreeItem<FileItem> treeItem = getElementByDir(path, directoryTreeView.getRoot());
        System.out.println(treeItem.getValue());
        if (treeItem != null) {
            directoryTreeView.getRoot().getChildren().remove(treeItem);
        }
    }

    private void create(TreeItem<FileItem> fileItem) {
        Path parentDir = fileItem.getValue().getPath().getParent();
        TreeItem<FileItem> treeItem = getElementByDir(parentDir, directoryTreeView.getRoot());
        if (treeItem != null) {
            treeItem.getChildren().add(fileItem);
        }
    }

    private TreeItem<FileItem> getElementByDir(Path elementDir, TreeItem<FileItem> parent) {
        if (parent.getValue().getPath() == null ||parent.getValue().getPath().equals(elementDir)) {
            return parent;
        }
        for (TreeItem<FileItem> child: parent.getChildren()) {
            TreeItem<FileItem> element = getElementByDir(elementDir, child);
            if (element != null) {
                return element;
            }
        }
        return null;
    }*/

    private void showCreateNewFolderDialog() {
        CreateNewFolderAlert createNewFolderAlert = new CreateNewFolderAlert();
        Optional<String> folderName = createNewFolderAlert.showAndWait();
        if (folderName.isPresent()) {
            String path = directoryTreeTitleLabel.getText() + '/' + folderName.get();

            boolean successful = model.createNewFolder(path.replace(rootPath, ""));

            if (successful) {
                Path parentPath = Path.of(directoryTreeTitleLabel.getText());
                Optional<TreeItem<FileItem>> parent = findItemByPath(directoryTreeView.getRoot(), parentPath.toString());
                if (parent.isPresent()) {
                    Path folderPath = Path.of(path);
                    FileItem fileItem = new FileItem(folderPath.getFileName().toString(), true, folderPath, 0);
                    TreeItem<FileItem> newTreeItem = new TreeItem<>(fileItem);
                    addOptimisticFile(parent.get(), newTreeItem);
                }
            }
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
            Path path = Path.of(directoryTreeTitleLabel.getText() + '/' + selectedFile.getName());
            model.uploadFile(selectedFile, path.toString().replace(rootPath, ""));

            new Thread(() -> {
                Path parentPath = Path.of(directoryTreeTitleLabel.getText());
                Optional<TreeItem<FileItem>> parent = findItemByPath(directoryTreeView.getRoot(), parentPath.toString());
                if (parent.isPresent()) {
                    FileItem fileItem = new FileItem(
                            selectedFile.getName(),
                            selectedFile.isDirectory(),
                            Path.of(directoryTreeTitleLabel.getText(), selectedFile.getName()),
                            selectedFile.length()
                    );
                    TreeItem<FileItem> newTreeItem = new TreeItem<>(fileItem);
                    Platform.runLater(() -> addOptimisticFile(parent.get(), newTreeItem));
                }
            }).start();

        }
    }

    private void addOptimisticFile(TreeItem<FileItem> parent, TreeItem<FileItem> file) {
        parent.getChildren().add(file);
        directoryTreeView.refresh();
        updateCurrentDirectoryList(parent.getChildren());
    }

    private void removeOptimisticFile(TreeItem<FileItem> file) {
        TreeItem<FileItem> parent = file.getParent();
        parent.getChildren().remove(file);
        directoryTreeView.refresh();
        updateCurrentDirectoryList(parent.getChildren());
    }

    private void deleteFile(String filePath, boolean isDirectory) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(String.format("¿%s: %s?",
                resourceBundle.getString("delete_confirmation_message"), getPath(filePath)));
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.setTitle("¡Ojo!");
        if (alert.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            boolean successful = true;
            if (isDirectory) {
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
                alert2.setContentText(resourceBundle.getString("folder_recursive_delete_advise"));
                alert2.setGraphic(null);
                alert2.setHeaderText(null);
                alert2.setTitle("¡Ojo!");
                if (alert2.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
                    successful = model.deleteFolder(filePath.replace(rootPath, ""));
                }
            } else {
                successful = model.deleteFile(filePath.replace(rootPath, ""));
            }
            if (successful) {
                new Thread(() -> {
                    Path nativePath = Path.of(filePath);
                    Optional<TreeItem<FileItem>> parent = findItemByPath(directoryTreeView.getRoot(), nativePath.toString());
                    System.out.println(parent);
                    System.out.println(nativePath);
                    Platform.runLater(() -> parent.ifPresent(this::removeOptimisticFile));
                }).start();
            }
        }
    }

    private void downloadFile(String filePath, boolean isFolder) {
        String fileName = getPath(filePath);
        if (!isFolder) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(fileName);
            File selectedDirectory = fileChooser.showSaveDialog(App.getRootStage());
            Optional<TreeItem<FileItem>> optionalTreeItem = findItemByPath(directoryTreeView.getRoot(), Path.of(filePath).toString());
            long size = 0;
            if (optionalTreeItem.isPresent()) {
                size = optionalTreeItem.get().getValue().getSize();
            }
            model.downloadFile(
                    selectedDirectory.getPath(), filePath.replace(rootPath, ""), size);

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("¡Ojo!");
            alert.setHeaderText(null);
            alert.setContentText(resourceBundle.getString("to_download_folder_message"));
            alert.showAndWait();
        }

        System.out.println("end download");
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

    private Optional<TreeItem<FileItem>> findItemByPath(TreeItem<FileItem> root, String path) {
        System.out.println("root " + root.getValue().getPath().toString());
        System.out.println("path " + path);
        System.out.println("ASDFASDFA "+root.getValue().getPath().toString().equals(path));
        if (root.getValue().getPath().toString().equals(path)) {
            return Optional.of(root);
        }
        for (TreeItem<FileItem> child: root.getChildren()) {
            Optional<TreeItem<FileItem>> childFileItem = findItemByPath(child, path);
            if (childFileItem.isPresent()) {
                return childFileItem;
            }
        }
        return Optional.empty();
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
