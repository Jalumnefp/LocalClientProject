package es.jfp.localclientproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.prefs.Preferences;
import org.controlsfx.glyphfont.FontAwesome;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class App extends Application {

    public final static Preferences preferences = Preferences.userRoot();
    private static Stage stage;
    private static String currentUser;

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;

        loadSceneInRootStage("search-server-view");

        // Conf stage
        stage.setTitle("LocalClientProject");
        stage.setMaximized(false);

        stage.show();
    }

    public static void loadSceneInRootStage(String view) throws IOException {
        Locale lang = Locale.of(preferences.get("LANGUAGE", "es_ES"));
        boolean dark = Boolean.parseBoolean(preferences.get("DARK_THEME", "false"));
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(view + ".fxml"));
        fxmlLoader.setResources(ResourceBundle.getBundle("es/jfp/localclientproject/bundle/strings", lang));
        Scene scene = new Scene(fxmlLoader.load());

        String stylesheet = String.format("styles/%s%s-styles.css", dark ? "dark/" : "", view);
        scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource(stylesheet)).toExternalForm());
        stage.setScene(scene);
    }

    public static Stage getRootStage() {
        return stage;
    }
    public static String getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(String currentUser) {
        App.currentUser = currentUser;
    }

    public static void main(String[] args) {
        launch();
    }
}