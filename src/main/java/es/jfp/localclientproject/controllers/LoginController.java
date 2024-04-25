package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.models.LoginModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.IOException;
import java.util.ResourceBundle;

public class LoginController {

    @FXML
    private Label registerUserLabel;
    @FXML
    private ProgressBar loginProgressBar;
    @FXML
    private Button loginButton;
    @FXML
    private CustomPasswordField passwordCustomTextField;
    @FXML
    private CustomTextField usernameCustomTextField;

    @FXML
    private void initialize() {

        loginButton.setOnAction(actionEvent -> {
            //loginProgressBar.setProgress(-1);
            loginProgressBar.setVisible(true);
            String username = usernameCustomTextField.getText();
            String password = passwordCustomTextField.getText();
            if (LoginModel.getInstance().requestLogin(username, password)) {
                App.setCurrentUser(username);
                Stage stage = (Stage) loginButton.getParent().getScene().getWindow();
                stage.close();
            } else {
                loginProgressBar.setVisible(false);
            }
        });

        registerUserLabel.setOnMouseClicked(mouseEvent -> {
            Stage stage = (Stage) loginButton.getParent().getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("register-view.fxml"));
            fxmlLoader.setResources(ResourceBundle.getBundle("es/jfp/localclientproject/bundle/strings_es"));
            try {
                stage.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

}
