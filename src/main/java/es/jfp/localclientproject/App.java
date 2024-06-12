package es.jfp.localclientproject;

import es.jfp.localclientproject.repositorys.ServerRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class App extends Application {

    public final static Preferences preferences = Preferences.userRoot();
    private static Stage stage = null;
    private static String currentUser = null;
    private static String currentScene = null;
    private static Locale currentLocale = null;

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;

        stage.setOnCloseRequest(windowEvent -> {
            ServerRepository.getInstance().closeConnection();
            System.out.println("END");
        });

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
        setCurrentLocale(lang);
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
    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("es/jfp/localclientproject/bundle/strings", currentLocale);
    }
    public static void setCurrentLocale(Locale currentLocale) {
        App.currentLocale = currentLocale;
    }

    public static void main(String[] args) {
        launch();
    }
}