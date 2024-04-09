package es.jfp.localclientproject.controllers;

import es.jfp.localclientproject.data.Server;
import es.jfp.localclientproject.models.ServerHistoryModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class AddServerDialogController {

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

    private final ValidationSupport validator = new ValidationSupport();
    private String serverAlias;
    private String serverIpv4;
    private String port;

    Validator<String> ipv4Validator = (control, value) -> {
        String regex = "^\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b$";
        boolean matches = value.matches(regex);
        String errorMsg = "La ipv4 introducida es incorrecta";
        return ValidationResult.fromMessageIf(control, errorMsg, Severity.ERROR, !matches);
    };

    Validator<String> portValidator = ((control, value) -> {
        int portNum = Integer.parseInt(value);
        boolean portInRange = portNum > 0 && portNum <= 65535;
        String errorMsg = "El puerto introducido es incorrecto";
        return ValidationResult.fromMessageIf(control, errorMsg, Severity.ERROR, !portInRange);
    });

    @FXML
    private void initialize() {

        validator.registerValidator(ipv4TextField, false, ipv4Validator);
        validator.registerValidator(portTextField, false, portValidator);

        saveServerCheckBox.setSelected(true);
        closeButton.setOnMouseClicked(mouseEvent -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
        serverAliasTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            serverAlias = (newValue);
        });
        ipv4TextField.textProperty().addListener(((observableValue, s, t1) -> {
            serverIpv4 = (t1);
        }));
        portTextField.textProperty().addListener(((observableValue, s, t1) -> {
            port = (t1);
        }));
        acceptButton.setOnMouseClicked(mouseEvent -> {
            if (!validator.isInvalid()) {
                try {
                    Server server = new Server(
                            serverAlias,
                            InetAddress.getByName(serverIpv4),
                            Integer.parseInt(port)
                    );
                    if (saveServerCheckBox.isSelected()) {
                        List<Server> serverList = ServerHistoryModel.getInstance().requestGetServers();
                        serverList.add(server);
                        ServerHistoryModel.getInstance().requestSaveServers(serverList);
                    } else {
                        ServerHistoryModel.getInstance().setTempServer(server);
                    }
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            }
        });
    }
}
