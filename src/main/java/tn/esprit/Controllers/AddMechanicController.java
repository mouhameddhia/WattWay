package tn.esprit.Controllers;

import javafx.scene.control.ComboBox;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.MechanicServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;
import javafx.scene.control.Alert;


public class AddMechanicController {

    @FXML
    private TextField nameMechanicField;
    @FXML
    private ComboBox<String> specialityMechanicComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final MechanicServices mechanicServices = new MechanicServices();

    @FXML
    private void initialize() {
        specialityMechanicComboBox.getItems().addAll("Electrician","Mechanic","Software");
    }

    private boolean isValidName(String name) {
        return name.matches("[A-Za-z ]+");
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void saveMechanic() throws SQLException {
        String name = nameMechanicField.getText();
        String specialityString = specialityMechanicComboBox.getValue();

        if (name.isEmpty() || specialityString.isEmpty() || specialityString == null) {
            showAlert("Empty","Please fill the fields");
            return;
        }
        if (!isValidName(name)) {
            showAlert("Invalid Name", "Mechanic name must contain only letters and spaces.");
            return;
        }

        //Mechanic newMechanic = new Mechanic();
        //newMechanic.setNameMechanic(name);
        Mechanic.Speciality speciality = Mechanic.Speciality.valueOf(specialityString.toUpperCase());
        Mechanic newMechanic = new Mechanic(0,name,speciality);

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
