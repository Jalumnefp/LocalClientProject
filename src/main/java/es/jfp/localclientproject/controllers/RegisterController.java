package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.models.LoginModel;
import es.jfp.localclientproject.models.RegisterModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.IOException;
import java.util.ResourceBundle;

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
    private Button goBackButton;

    @FXML
    private void initialize() {

        registerButton.setOnAction(actionEvent -> {
            String username = usernameCustomTextField.getText();
            String password = passwordCustomTextField.getText();
            if (RegisterModel.getInstance().requestRegister(username, password)) {
                App.setCurrentUser(username);
                Stage stage = (Stage) registerButton.getParent().getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("El usuario no ha podido registrarse");
                alert.showAndWait();
            }
        });


        goBackButton.setOnAction(actionEvent -> {
            Stage stage = (Stage) goBackButton.getParent().getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
            fxmlLoader.setResources(ResourceBundle.getBundle("es/jfp/localclientproject/bundle/strings_es"));
            try {
                stage.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
