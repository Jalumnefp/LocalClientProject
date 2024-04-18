package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.models.MainModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public final class UploadFileController {

    @FXML
    private Button cancelUploadButton;
    @FXML
    private TextField pathTextField;
    @FXML
    private Button selectFileButton;
    @FXML
    private Button uploadFileButton;

    private final FileChooser fileChooser = new FileChooser();
    private File selectedFile;


    @FXML
    private void initialize() {

        uploadFileButton.setDisable(true);
        pathTextField.setEditable(false);

        pathTextField.textProperty().addListener((observableValue, s, t1) -> uploadFileButton.setDisable(t1.isEmpty()));

        selectFileButton.setOnMouseClicked(mouseEvent -> {
            selectedFile = fileChooser.showOpenDialog(selectFileButton.getScene().getWindow());
            if (selectedFile!=null) {
                pathTextField.setText(selectedFile.getAbsolutePath());
            }
        });

        uploadFileButton.setOnMouseClicked(mouseEvent -> {
            MainModel.getInstance().sendFileToModel(selectedFile);
            closeWindow();
        });

        cancelUploadButton.setOnMouseClicked(mouseEvent -> closeWindow());

    }

    private void closeWindow() {
        Stage stage = (Stage) uploadFileButton.getScene().getWindow();
        stage.close();
    }

}
