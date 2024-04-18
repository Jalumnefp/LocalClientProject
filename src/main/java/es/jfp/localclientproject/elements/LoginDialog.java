package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.exceptions.PasswordComplexityException;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.dialog.ExceptionDialog;

public class LoginDialog extends org.controlsfx.dialog.LoginDialog {
    public LoginDialog() {
        super(null, credentials -> {
            String username = credentials.getKey();
            String password = credentials.getValue();
            if (password.matches("^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$")) {
                System.out.println("password true");
            } else {
                ExceptionDialog exceptionDialog = new ExceptionDialog(new PasswordComplexityException());
                exceptionDialog.showAndWait();
            }
            return null;
        });

        Label registerLabel = new Label("¿No tienes cuenta? Registrate aquí!");
        registerLabel.setTextFill(Color.RED);

        registerLabel.setOnMouseClicked(mouseEvent -> {
            System.out.println("register");
        });

        registerLabel.setOnMouseEntered(mouseEvent -> {
            registerLabel.setUnderline(true);
            registerLabel.setCursor(Cursor.HAND);
        });

        VBox vBox = (VBox) getDialogPane().getContent();
        vBox.getChildren().add(registerLabel);
    }
}
