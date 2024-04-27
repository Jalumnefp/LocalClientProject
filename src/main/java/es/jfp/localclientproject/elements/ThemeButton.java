package es.jfp.localclientproject.elements;

import es.jfp.localclientproject.App;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

public class ThemeButton extends Button {

    private double iconSize;
    private Color iconColor;

    private boolean dark;

    public ThemeButton() {
        getPrefTheme();
        create();
    }


    private void create() {
        setDefaultIcon();
        setOnAction(actionEvent -> {
            Glyph icon = (Glyph) getGraphic();
            boolean isSunIcon = icon.getIcon().equals(FontAwesome.Glyph.SUN_ALT);
            ((Glyph) getGraphic()).setIcon(isSunIcon ? FontAwesome.Glyph.MOON_ALT : FontAwesome.Glyph.SUN_ALT);
            try {
                App.preferences.put("DARK_THEME", String.valueOf(!dark));
                getPrefTheme();
                setStyleSheet();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setDefaultIcon() {
        Glyph icon = new Glyph("FontAwesome", !dark ? FontAwesome.Glyph.SUN_ALT : FontAwesome.Glyph.MOON_ALT);
        setGraphic(icon);
    }

    private void setStyleSheet() throws URISyntaxException {
        List<String> stylesheets = getScene().getStylesheets();
        Path stylePath = getNewPath(Path.of(new URI(stylesheets.get(0))));
        stylesheets.clear();
        stylesheets.add(App.class.getResource(stylePath.toString().replace('\\', '/')).toExternalForm());
    }

    private Path getNewPath(Path file) {
        Path fileName = file.getFileName();
        Path parent = file.getParent();
        if (dark) {
            System.out.println(dark);
            return parent.getFileName().resolve("dark").resolve(fileName);
        } else {
            System.out.println(dark);
            return parent.getParent().getFileName().resolve(fileName);
        }
    }

    private void getPrefTheme() {
        this.dark = Boolean.parseBoolean(App.preferences.get("DARK_THEME", "false"));
    }

    public double getIconSize() {
        return iconSize;
    }

    public void setIconSize(double iconSize) {
        this.iconSize = iconSize;
    }

    public Color getIconColor() {
        return iconColor;
    }

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
    }
}
