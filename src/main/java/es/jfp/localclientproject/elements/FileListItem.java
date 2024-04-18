package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.App;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.io.InputStream;

public class FileListItem extends GridPane   {

    private final boolean isDirectory;

    public FileListItem(String fileName, boolean isDirectory) {
        this.isDirectory = isDirectory;

        Label fileNameLabel = new Label(fileName);
        Glyph glyph = setUpFileImage();

        setPadding(new Insets(5));
        addColumn(0, glyph);
        addColumn(1, fileNameLabel);

        getColumnConstraints().addAll(
            new ColumnConstraints(200),
            new ColumnConstraints(800)
        );
    }

    private Glyph setUpFileImage() {
        FontAwesome.Glyph icon = isDirectory ? FontAwesome.Glyph.FOLDER_ALT : FontAwesome.Glyph.FILE_ALT;
        return new Glyph("FontAwesome", icon);
    }

}
