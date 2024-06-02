package es.jfp.localclientproject.elements;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class UserAlert extends Alert {
    public UserAlert(AlertType alertType) {
        super(alertType);

        setTitle("Profile");
        setContentText("asdfasdf");

    }


}
