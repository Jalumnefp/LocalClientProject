module es.jfp.localclientproject {
    requires java.base;
    requires java.prefs;
    requires org.controlsfx.controls;
    requires es.jfp;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires javafx.fxmlEmpty;

    opens es.jfp.localclientproject to javafx.fxml;
    exports es.jfp.localclientproject;
    opens es.jfp.localclientproject.controllers to javafx.fxml;
    exports es.jfp.localclientproject.controllers;
    opens es.jfp.localclientproject.data to java.base;
    exports es.jfp.localclientproject.data;
    opens es.jfp.localclientproject.elements to javafx.fxml;
    exports es.jfp.localclientproject.elements;
}