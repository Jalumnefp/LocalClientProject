module es.jfp.localclientproject {
    requires javafx.controls;
    requires javafx.fxml;

    opens es.jfp.localclientproject to javafx.fxml;
    exports es.jfp.localclientproject;
    opens es.jfp.localclientproject.controllers to javafx.fxml;
    exports es.jfp.localclientproject.controllers;
}