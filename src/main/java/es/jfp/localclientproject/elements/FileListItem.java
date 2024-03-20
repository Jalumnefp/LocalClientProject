package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.App;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.InputStream;

public class FileListItem extends AnchorPane {

    private final boolean isDirectory;

    public FileListItem(String fileName, boolean isDirectory) {
        Label fileNameLabel = setUpFileName(fileName);
        this.isDirectory = isDirectory;
        ImageView fileImageView = setUpFileImage();

        getChildren().addAll(fileImageView, fileNameLabel);
    }

    private Label setUpFileName(String name) {
        Label label = new Label();
        label.setLayoutX(137.0);
        label.setLayoutY(22.0);
        label.setPrefHeight(42.0);
        label.setPrefWidth(28.0);
        label.setText(name);
        AnchorPane.setBottomAnchor(label, 12.0);
        AnchorPane.setLeftAnchor(label, 200.0);
        AnchorPane.setRightAnchor(label, 200.0);
        AnchorPane.setTopAnchor(label, 12.0);
        return label;
    }

    private ImageView setUpFileImage() {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(78.0);
        imageView.setFitWidth(78.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        AnchorPane.setBottomAnchor(imageView, 5.0);
        AnchorPane.setLeftAnchor(imageView, 15.0);
        AnchorPane.setRightAnchor(imageView, 520.0);
        AnchorPane.setTopAnchor(imageView, 5.0);
        String url = "images/icons/" + (isDirectory ? "folder-regular.png" : "file-regular.png");
        InputStream iconStream = App.class.getResourceAsStream(url);
        imageView.setImage(new Image(iconStream));
        return imageView;
    }

}
