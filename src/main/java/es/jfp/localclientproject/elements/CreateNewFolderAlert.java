package es.jfp.localclientproject.elements;

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
    }

}
