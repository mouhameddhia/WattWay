package tn.esprit.Controllers;

import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

public class UpdateMechanicController {

    @FXML
    private TextField nameMechanicField;
    @FXML
    private ComboBox<Mechanic.Speciality> specialityMechanicComboBox;
    @FXML
    private TextField emailMechanicField;
    @FXML
    private TextField carsRepairedField;
    @FXML
    private Button uploadImageButton;
    @FXML
    private ImageView mechanicImageView;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final MechanicServices mechanicServices = new MechanicServices();
    private Mechanic mechanic;
    private String imagePath = null; // Store the image path

    @FXML
    public void initialize() {
        specialityMechanicComboBox.getItems().addAll(Mechanic.Speciality.values());
    }

    public void initData(Mechanic mechanic) {
        this.mechanic = mechanic;
        nameMechanicField.setText(mechanic.getNameMechanic());
        specialityMechanicComboBox.setValue(mechanic.getSpecialityMechanic());
        emailMechanicField.setText(mechanic.getEmailMechanic());
        carsRepairedField.setText(String.valueOf(mechanic.getCarsRepaired()));

        if (mechanic.getImgMechanic() != null && !mechanic.getImgMechanic().isEmpty()) {
            mechanicImageView.setImage(new Image("file:" + mechanic.getImgMechanic()));
        }
    }

    private boolean isValidName(String name) {
        return name.matches("[A-Za-z ]+");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }


    private boolean isValidNumber(String number) {
        return number.matches("\\d+"); // Only digits allowed
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            mechanicImageView.setImage(new Image("file:" + imagePath));
        }
    }

    @FXML
    private void saveMechanic() {
        String name = nameMechanicField.getText().trim();
        String email = emailMechanicField.getText().trim();
        String carsRepairedText = carsRepairedField.getText().trim();
        Mechanic.Speciality speciality = specialityMechanicComboBox.getValue();

        if (name.isEmpty() || email.isEmpty() || carsRepairedText.isEmpty() || speciality == null) {
            showAlert("Missing Information", "Please fill all required fields.");
            return;
        }

        if (!isValidName(name)) {
            showAlert("Invalid Name", "Mechanic name must contain only letters and spaces.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }

        if (!isValidNumber(carsRepairedText)) {
            showAlert("Invalid Input", "Cars repaired must be a numeric value.");
            return;
        }

        int carsRepaired = Integer.parseInt(carsRepairedText);

        mechanic.setNameMechanic(name);
        mechanic.setSpecialityMechanic(speciality);
        mechanic.setEmailMechanic(email);
        mechanic.setCarsRepaired(carsRepaired);
        if (imagePath != null) {
            mechanic.setImgMechanic(imagePath);
        }

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
