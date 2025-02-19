package tn.esprit.Controllers;

import tn.esprit.entities.Mechanic;
import tn.esprit.services.MechanicServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;

public class AddMechanicController {

    @FXML
    private TextField nameMechanicField;
    @FXML
    private TextField specialityMechanicField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final MechanicServices mechanicServices = new MechanicServices();

    @FXML
    private void saveMechanic() {
        String name = nameMechanicField.getText();
        String speciality = specialityMechanicField.getText();

        if (name.isEmpty() || speciality.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }

        Mechanic newMechanic = new Mechanic();
        newMechanic.setNameMechanic(name);
        newMechanic.setSpecialityMechanic(speciality);

        try {
            mechanicServices.addP(newMechanic);
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
        if (stage != null) {
            System.out.println("Closing window...");
            stage.close();
        } else {
            System.out.println("Error: Stage is null");
        }
    }
}
