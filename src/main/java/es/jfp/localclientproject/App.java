package es.jfp.localclientproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    private static Stage stage;
    private static String currentUser;

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("search-server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource("styles/search-server-view-styles.css")).toExternalForm());
        stage.setTitle("LocalClientProject");
        stage.setMaximized(false);
        stage.setScene(scene);
        stage.show();
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