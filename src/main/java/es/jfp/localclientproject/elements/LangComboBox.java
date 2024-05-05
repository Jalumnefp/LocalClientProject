package es.jfp.localclientproject.elements;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import es.jfp.localclientproject.App;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class LangComboBox extends ComboBox<String> {

    private String context;
    private final Map<String, String> languages = Map.of(
            "CAS", "es_ES",
            "ENG", "en_US",
            "VAL", "ca_ES_VALENCIA"
    );


    public LangComboBox() {
        super();

        this.context = context;

        this.setItems(FXCollections.observableArrayList("CAS", "ENG", "VAL"));

        String langPref = App.preferences.get("LANGUAGE", "es_ES");
        for (Map.Entry<String, String> entry: languages.entrySet()) {
            if (entry.getValue().equals(langPref)) {
                this.setValue(entry.getKey());
                break;
            }
        }

        this.setOnAction(actionEvent -> {
            String selection = this.getSelectionModel().getSelectedItem();
            String lang = languages.get(selection);
            App.preferences.put("LANGUAGE", lang);
            try {
                String currentScene = App.getCurrentScene();
                System.out.println(currentScene);
                App.loadSceneInRootStage(currentScene, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }



}
