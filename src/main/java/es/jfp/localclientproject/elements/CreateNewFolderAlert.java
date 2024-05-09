package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.App;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;


public class CreateNewFolderAlert extends TextInputDialog {

    public CreateNewFolderAlert() {
        setTitle("Crear nueva carpeta");
        setHeaderText(null);
        setGraphic(null);
        setContentText("Nombre de la carpeta:");
        Button aceptarButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        aceptarButton.setDisable(true);
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> aceptarButton.setDisable(newValue.isEmpty()));

        setTheme();
    }

    private void setTheme() {
        boolean dark = Boolean.parseBoolean(App.preferences.get("DARK_THEME", "false"));
        if (dark) {
            String darkTheme = App.class.getResource("styles/dark/generic-dark-styles.css").toExternalForm();
            this.getDialogPane().getStylesheets().add(darkTheme);
        }
    }

}
