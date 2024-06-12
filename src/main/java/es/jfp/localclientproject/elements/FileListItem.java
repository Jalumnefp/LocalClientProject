package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.io.InputStream;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FileListItem extends HBox {

    private final ResourceBundle resourceBundle = App.getResourceBundle();
    private final boolean isDirectory;
    private final String path;

    public FileListItem(String fileName, String path, boolean isDirectory, BiConsumer<String, Boolean> download, BiConsumer<String, Boolean> delete) {
        Label fileNameLabel = setUpFileName(fileName);
        this.path = path;
        this.isDirectory = isDirectory;
        ImageView fileImageView = setUpFileImage();
        MenuButton menuButton = setUpButton(download, delete);

        getChildren().addAll(fileImageView, createSpacer(), fileNameLabel, createSpacer(), menuButton);

        setAlignment(Pos.CENTER);
        setPadding(new Insets(0, 20, 0, 20));
    }

    private Label setUpFileName(String name) {
        Label label = new Label();
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.setMaxWidth(500.0);
        label.setText(name);
        return label;
    }

    private ImageView setUpFileImage() {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(52.0);
        imageView.setFitWidth(52.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        //String url = "images/icons/" + (isDirectory ? "folder-regular.png" : "file-regular.png");
        imageView.setId(!isDirectory ? "file-icon" : "folder-icon");
        //InputStream iconStream = App.class.getResourceAsStream(url);

        //imageView.setImage(new Image(iconStream));
        return imageView;
    }

    private MenuButton setUpButton(BiConsumer<String, Boolean> download, BiConsumer<String, Boolean> delete) {
        MenuButton button = new MenuButton();
        button.setStyle("-fx-font-size: 12px; -fx-background-color: transparent;");
        MenuItem menuItemDownload = new MenuItem(resourceBundle.getString("download"));
        menuItemDownload.setOnAction(actionEvent -> download.accept(this.path, isDirectory));
        MenuItem menuItemEliminar = new MenuItem(resourceBundle.getString("delete"));
        menuItemEliminar.setOnAction(actionEvent -> delete.accept(this.path, isDirectory));
        button.getItems().addAll(menuItemDownload, menuItemEliminar);

        return button;
    }

    private Region createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
