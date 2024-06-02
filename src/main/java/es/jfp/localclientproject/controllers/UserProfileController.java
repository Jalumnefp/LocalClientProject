package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.repositorys.ServerRepository;
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
    private Button changePassword;

    @FXML
    private void initialize() {
        usernameLabel.setText(App.getCurrentUser());

        closeSessionButton.setOnAction(actionEvent -> {
            ServerRepository.getInstance().closeSession();
            App.setCurrentUser(null);
            Stage stage = (Stage) closeSessionButton.getParent().getScene().getWindow();
            stage.close();
        });
    }

}
