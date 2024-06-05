package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.App;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.ResourceBundle;


public class CreateNewFolderAlert extends TextInputDialog {

    private final ResourceBundle resourceBundle = App.getResourceBundle();

    public CreateNewFolderAlert() {
        setTitle(resourceBundle.getString("create_new_folder_text"));
        setHeaderText(null);
        setGraphic(null);
        setContentText(resourceBundle.getString("input_name_file_text"));
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
