package es.jfp.localclientproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BooleanSupplier;

public class App extends Application {

    public final static Preferences preferences = Preferences.userRoot();
    private static Stage stage;
    private static String currentUser;
    private static String currentScene;


    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;

        loadSceneInRootStage("search-server-view", null);

        // Conf stage
        stage.setTitle("LocalClientProject");
        stage.setMaximized(false);

        stage.show();
    }

    public static void loadSceneInRootStage(String view, Consumer<Stage> consumer) throws IOException {
        if (!Objects.equals(currentScene, view)) {
            currentScene = view;
        }
        if (consumer != null) {
            consumer.accept(stage);
        }
        // Obtener preferencias de usuario
        Locale lang = Locale.of(preferences.get("LANGUAGE", "es_ES"));
        boolean dark = Boolean.parseBoolean(preferences.get("DARK_THEME", "false"));
        // Cargar fxml inicial
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(view + ".fxml"));
        fxmlLoader.setResources(ResourceBundle.getBundle("es/jfp/localclientproject/bundle/strings", lang));
        Scene scene = new Scene(fxmlLoader.load());

        String stylesheet = String.format("styles/%s%s-styles.css", dark ? "dark/" : "", view);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource(stylesheet)).toExternalForm());
        stage.setScene(scene);
    }

    public static String getCurrentScene() {
        return currentScene;
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