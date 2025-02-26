package tn.esprit.Controllers;

import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.MechanicServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
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
    @FXML
    private TextField emailMechanicField;
    @FXML
    private TextField carsRepairedField;
    @FXML
    private String imgMechanicPath;

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
        String email = emailMechanicField.getText();
        int carsRepaired = Integer.parseInt(carsRepairedField.getText());

        if (name.isEmpty() || specialityString.isEmpty() || specialityString == null || specialityString.isEmpty() || email.isEmpty()) {
            showAlert("Empty","Please fill the fields");
            return;
        }
        if (!isValidName(name)) {
            showAlert("Invalid Name", "Mechanic name must contain only letters and spaces.");
            return;
        }
        if (mechanicServices.isMechanicNameTaken(name)) {
            showAlert("Duplicate Name", "A mechanic with this name already exists. Please choose another name.");
            return;
        }
        if (!isValidEmail(email)) {
            showAlert("Invalid Email", "Mechanic email not written in proper form.");
            return;
        }

        //Mechanic newMechanic = new Mechanic();
        //newMechanic.setNameMechanic(name);
        Mechanic.Speciality speciality = Mechanic.Speciality.valueOf(specialityString.toUpperCase());
        Mechanic newMechanic = new Mechanic(0,name,speciality,imgMechanicPath, email, carsRepaired);

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
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imgMechanicPath = file.getAbsolutePath();
            System.out.println("Selected Image: " + imgMechanicPath);
        }
    }
}
