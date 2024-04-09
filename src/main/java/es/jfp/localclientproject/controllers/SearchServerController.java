package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.data.Server;
import es.jfp.localclientproject.models.ServerHistoryModel;
import es.jfp.localclientproject.repositorys.ServerRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.dialog.ExceptionDialog;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;

public class SearchServerController {

    @FXML
    private TableView<Server> serversTableView;
    @FXML
    private MenuItem editMenuItem;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private Button showDeleteAlertDialogButton;
    @FXML
    private Button showAddServerDialogButton;
    @FXML
    private Button connectButton;
    private List<Server> serverList;

    @FXML
    private void initialize() {
        serverList = ServerHistoryModel.getInstance().requestGetServers();
        ObservableList<Server> data = FXCollections.observableArrayList(serverList);
        serversTableView.setItems(data);

        serversTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        showAddServerDialogButton.setOnAction(actionEvent -> {
            Alert alert = getAddServerDialog();
            alert.showAndWait();
            Server tempServer = ServerHistoryModel.getInstance().getTempServer();
            reloadListElements();
            if (tempServer!=null) {
                serversTableView.getItems().add(tempServer);
            }
            serversTableView.refresh();
        });

        showDeleteAlertDialogButton.setOnAction(actionEvent -> deleteItem());

        deleteMenuItem.setOnAction(actionEvent -> deleteItem());

        serversTableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                connectServer();
            }
        });

        serversTableView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                connectServer();
            }
        });

        connectButton.setOnAction(actionEvent -> {
            connectServer();
        });


    }

    private void deleteItem() {
        Server selectedServer = serversTableView.getSelectionModel().getSelectedItem();
        if (selectedServer!=null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ojo!");
            alert.setContentText(String.format("Estás seguro/a de que quieres borrar el servidor: %s?", selectedServer));
            if (alert.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
                serverList.remove(selectedServer);
                ServerHistoryModel.getInstance().requestSaveServers(serverList);
                reloadListElements();
            }
        }
    }

    private Alert getAddServerDialog() {
        Alert alert = null;
        try {
            Pane pane = FXMLLoader.load(App.class.getResource("create-server-connection-view.fxml"));
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setGraphic(null);
            alert.getButtonTypes().removeAll(ButtonType.OK);
            Button cancellButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancellButton.setVisible(false);
            alert.getDialogPane().setContent(pane);
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionDialog exceptionDialog = new ExceptionDialog(e);
            exceptionDialog.showAndWait();
        }
        return alert;
    }

    private void reloadListElements() {
        serverList = ServerHistoryModel.getInstance().requestGetServers();
        serversTableView.getItems().clear();
        serversTableView.getItems().addAll(serverList);
    }

    private void connectServer() {
        Server selectedServer = serversTableView.getSelectionModel().getSelectedItem();
        InetAddress ipv4 = selectedServer.getIpv4();
        int port = selectedServer.getPort();
        try {
            ServerRepository.getInstance().setServerSocket(ipv4, port);
            Pane sceneContent = FXMLLoader.load(App.class.getResource("main-view.fxml"));
            Scene newScene = new Scene(sceneContent);
            newScene.getStylesheets().clear();
            newScene.getStylesheets().add(Objects.requireNonNull(App.class.getResource("styles/main-view-styles.css")).toExternalForm());
            Stage rootStage = App.getRootStage();
            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            rootStage.setX(screen.getMinX());
            rootStage.setY(screen.getMinY());
            rootStage.setWidth(screen.getWidth());
            rootStage.setHeight(screen.getHeight());
            rootStage.setMaximized(false);
            rootStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionDialog dialog = new ExceptionDialog(e);
            dialog.showAndWait();
        }
    }

}
