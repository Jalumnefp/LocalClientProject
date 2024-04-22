package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class UserProfileController {

    @FXML
    private Button closeSessionButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private ImageView userfaceImageView;

    @FXML
    private void initialize() {
        usernameLabel.setText(App.getCurrentUser());

        closeSessionButton.setOnAction(actionEvent -> {
            App.setCurrentUser(null);
            Stage stage = (Stage) closeSessionButton.getParent().getScene().getWindow();
            stage.close();
        });
    }

}
