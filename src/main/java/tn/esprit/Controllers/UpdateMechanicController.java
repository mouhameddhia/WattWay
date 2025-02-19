package tn.esprit.Controllers;

import tn.esprit.entities.Mechanic;
import tn.esprit.services.MechanicServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;

public class UpdateMechanicController {

    @FXML
    private TextField nameMechanicField;
    @FXML
    private TextField specialityMechanicField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final MechanicServices mechanicServices = new MechanicServices();
    private Mechanic mechanic;

    public void initData(Mechanic mechanic) {
        this.mechanic = mechanic;
        nameMechanicField.setText(mechanic.getNameMechanic());
        specialityMechanicField.setText(mechanic.getSpecialityMechanic());
    }

    @FXML
    private void saveMechanic() {
        mechanic.setNameMechanic(nameMechanicField.getText());
        mechanic.setSpecialityMechanic(specialityMechanicField.getText());
        try {
            mechanicServices.update(mechanic);
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
