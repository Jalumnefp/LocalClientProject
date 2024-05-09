package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.App;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;

public class UploadFileDialog extends Dialog {

    public UploadFileDialog() {
        setTitle("Diálogo Personalizado");

        // Configurar el contenido del diálogo
        FileChooser fileChooser = new FileChooser();
        Button selectFileButton = new Button("Seleccionar archivo");
        TextField textField = new TextField();
        textField.setEditable(false);
        ProgressBar progressBar = new ProgressBar();
        Button uploadButton = new Button("Subir archivo");

        // Crear el diseño del diálogo
        GridPane dialogContent = new GridPane();
        dialogContent.setPadding(new Insets(20));
        dialogContent.setVgap(10);
        dialogContent.setHgap(10);
        dialogContent.addRow(0, new Label("Selecciona un archivo:"), selectFileButton);
        dialogContent.addRow(1, new Label("Contenido del archivo:"), textField);
        dialogContent.addRow(2, uploadButton);
        dialogContent.addRow(3, new Label("Progreso:"), progressBar);

        // Configurar el botón de selección de archivo
        selectFileButton.setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(getOwner());
            if (selectedFile != null) {
                textField.setText(selectedFile.getAbsolutePath());
            }
        });

        // Configurar el botón de subida
        uploadButton.setOnAction(event -> {
            // Simular la subida con una tarea
            progressBar.setProgress(0.5); // Ejemplo de progreso
        });

        // Configurar el botón de cierre del diálogo
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        setOnCloseRequest(event -> close());

        // Configurar el contenido del diálogo
        getDialogPane().setContent(dialogContent);

        setTheme();
    }

    private void setTheme() {
        boolean dark = Boolean.parseBoolean(App.preferences.get("DARK_THEME", "false"));
        if (dark) {
            String darkTheme = App.class.getResource("styles/dark/generic-dark-styles.css").toExternalForm();
            this.getDialogPane().getStylesheets().clear();
            this.getDialogPane().getStylesheets().add(darkTheme);
        }
    }

}
