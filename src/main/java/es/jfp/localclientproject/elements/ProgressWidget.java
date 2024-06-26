package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.App;
import es.jfp.localclientproject.models.MainModel;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.util.ResourceBundle;

public class ProgressWidget extends HBox {

    private final ResourceBundle resourceBundle = App.getResourceBundle();
    private final String name;
    private final ProgressBar progressBar;
    private final Label progressLabel;
    private Thread progressThread;

    public ProgressWidget(String name) {
        this.name = name;

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(100);

        progressLabel = new Label("0%");
        progressLabel.setStyle("-fx-background-color: transparent;");

        Button cancellButton = new Button();
        Glyph glyph = new Glyph("FontAwesome", FontAwesome.Glyph.MINUS);
        glyph.setColor(Color.GREY);
        glyph.setGraphicTextGap(0);
        glyph.setFontSize(10);
        cancellButton.setGraphic(glyph);
        cancellButton.setStyle("""
            -fx-background-radius: 10em;
            -fx-min-width: 20px;
            -fx-min-height: 20px;
            -fx-max-width: 20px;
            -fx-max-height: 20px;
        """);

        cancellButton.setOnMouseClicked(mouseEvent -> {
            if (progressBar.getProgress() < 1.0) {
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("¡Ojo!");
                confirmDialog.setHeaderText(null);
                confirmDialog.setGraphic(null);
                confirmDialog.setContentText(resourceBundle.getString("interrupt_process_question") + ' ' + name);
                if (confirmDialog.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
                    progressThread.interrupt();
                    selfDestruction();
                }
            } else {
                selfDestruction();
            }
        });

        this.setSpacing(5);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(progressBar, progressLabel, cancellButton);

    }

    private void selfDestruction() {
        MainModel.getInstance().removeProgressWidget(this);
    }

    public void setBarProgress(double progress) {
        this.progressLabel.setText(String.valueOf((int)progress) + '%');
        this.progressBar.setProgress(progress / 100);
        if ((int)progress == 100) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(String.format(resourceBundle.getString("process_end_advise"), this.name));
            alert.show();
        }
    }

    public void setProcessThread(Thread thread) {
        this.progressThread = thread;
        this.progressThread.start();
    }

}
