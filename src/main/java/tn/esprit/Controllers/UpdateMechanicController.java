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


public class UpdateMechanicController {

    @FXML
    private TextField nameMechanicField;
    @FXML
    private ComboBox<Mechanic.Speciality> specialityMechanicComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final MechanicServices mechanicServices = new MechanicServices();
    private Mechanic mechanic;

    @FXML
    public void initialize() {
        // ✅ Populate ComboBox with enum values
        specialityMechanicComboBox.getItems().addAll(Mechanic.Speciality.values());
    }

    public void initData(Mechanic mechanic) {
        this.mechanic = mechanic;
        nameMechanicField.setText(mechanic.getNameMechanic());
        specialityMechanicComboBox.setValue(mechanic.getSpecialityMechanic());
    }
    private boolean isValidName(String name) {
        return name.matches("[A-Za-z ]+"); // Allows only letters and spaces
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void saveMechanic() {
        String name = nameMechanicField.getText().trim();
        Mechanic.Speciality speciality = specialityMechanicComboBox.getValue();
        //mechanic.setNameMechanic(nameMechanicField.getText());

        if (speciality == null) {
            showAlert("Empty","Please fill the fields");
            return;
        }
        if (!isValidName(name)) {
            showAlert("Invalid Name", "Mechanic name must contain only letters and spaces.");
            return;
        }
        mechanic.setNameMechanic(name);
        mechanic.setSpecialityMechanic(speciality);

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
