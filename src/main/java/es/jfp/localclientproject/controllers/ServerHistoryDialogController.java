package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.data.Server;
import es.jfp.localclientproject.models.ServerHistoryModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class ServerHistoryDialogController {

    @FXML
    private CheckBox saveServerCheckBox;
    @FXML
    private TextField serverAliasTextField;
    @FXML
    private TextField ipv4TextField;
    @FXML
    private TextField portTextField;
    @FXML
    private Button closeButton;
    @FXML
    private Button acceptButton;
    private final ServerHistoryModel model = ServerHistoryModel.getInstance();
    private final ValidationSupport validator = new ValidationSupport();

    @FXML
    private void initialize() {

        /*Server oldServer = ServerHistoryModel.getInstance().getTempServer();
        if (oldServer!=null) {
            serverAliasTextField.setText(oldServer.getAlias());
            ipv4TextField.setText(oldServer.getIpv4().getHostAddress());
            portTextField.setText(String.valueOf(oldServer.getPort()));
            saveServerCheckBox.setVisible(false);
        }*/

        for (TextField textField: new TextField[] {serverAliasTextField, ipv4TextField, portTextField}) {
            validator.registerValidator(textField, false, ((control, o) -> {
                String errorMsg = "Campo requerido";
                return ValidationResult.fromErrorIf(control, errorMsg, o.toString().isEmpty());
            }));
            textField.textProperty().addListener((observableValue, s, t1) -> acceptButton.setDisable(validator.isInvalid()));
        }

        validator.registerValidator(ipv4TextField, false, ((control, o) -> {
            String regex = "^\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b$";
            boolean matches = o.toString().matches(regex);
            String errorMsg = "La ipv4 introducida es incorrecta";
            return ValidationResult.fromErrorIf(control, errorMsg, !matches);
        }));
        validator.registerValidator(portTextField, false, ((control, o) -> {
            int portNum = Integer.parseInt(o.toString());
            boolean portInRange = portNum > 0 && portNum <= 65535;
            String errorMsg = "El puerto introducido es incorrecto";
            return ValidationResult.fromErrorIf(control, errorMsg, !portInRange);
        }));

        saveServerCheckBox.setSelected(true);
        acceptButton.setDisable(validator.isInvalid());

        closeButton.setOnMouseClicked(mouseEvent -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        portTextField.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if (text.matches("^\\d*$")) {
                return change;
            }
            return null;
        }));

        acceptButton.setOnMouseClicked(mouseEvent -> {
            if (!validator.isInvalid()) {
                String alias = serverAliasTextField.getText();
                InetAddress ipv4 = null;
                try {ipv4 = InetAddress.getByName(ipv4TextField.getText());} catch (UnknownHostException e) {e.printStackTrace();}
                int port = Integer.parseInt(portTextField.getText());
                Server server = new Server(alias, ipv4, port);
                if (saveServerCheckBox.isSelected()) {
                    List<Server> serverList = model.requestGetServers();
                    /*if (oldServer != null) {
                        int oldServerPosition = serverList.indexOf(oldServer);
                        serverList.set(oldServerPosition, server);
                    } else {*/
                        serverList.add(server);
                    //}
                    model.requestSaveServers(serverList);
                } else {
                    model.setTempServer(server);
                }
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            }
        });

    }

}
