module es.jfp.localclientproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires org.controlsfx.controls;

    opens es.jfp.localclientproject to javafx.fxml;
    exports es.jfp.localclientproject;
    opens es.jfp.localclientproject.controllers to javafx.fxml;
    exports es.jfp.localclientproject.controllers;
    opens es.jfp.localclientproject.data to java.base;
    exports es.jfp.localclientproject.data;
    opens es.jfp.localclientproject.elements to javafx.fxml;
    exports es.jfp.localclientproject.elements;
}