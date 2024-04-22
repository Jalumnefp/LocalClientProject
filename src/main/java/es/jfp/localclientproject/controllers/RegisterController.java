package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.models.LoginModel;
import es.jfp.localclientproject.models.RegisterModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.IOException;

public class RegisterController {

    @FXML
    private Button registerButton;
    @FXML
    private CustomTextField usernameCustomTextField;
    @FXML
    private CustomPasswordField passwordCustomTextField;
    @FXML
    private CustomPasswordField repeatPasswordCustomTextField;
    @FXML
    private ProgressBar registerProgressBar;
    @FXML
    private Button goBackButton;

    @FXML
    private void initialize() {

        registerButton.setOnAction(actionEvent -> {
            registerProgressBar.setVisible(true);
            String username = usernameCustomTextField.getText();
            String password = passwordCustomTextField.getText();
            if (RegisterModel.getInstance().requestRegister(username, password)) {
                App.setCurrentUser(username);
                Stage stage = (Stage) registerButton.getParent().getScene().getWindow();
                stage.close();
            } else {
                registerProgressBar.setVisible(false);
            }
        });


        goBackButton.setOnAction(actionEvent -> {
            Stage stage = (Stage) goBackButton.getParent().getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
            try {
                stage.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
