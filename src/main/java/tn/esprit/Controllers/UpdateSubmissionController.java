package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Submission;
import tn.esprit.services.SubmissionServices;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class UpdateSubmissionController {

    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> urgencyComboBox;
    @FXML private DatePicker dateSubmissionField;
    @FXML private TextField carIdField;
    @FXML private TextField userIdField;

    private Submission submission;
    private final SubmissionServices submissionServices = new SubmissionServices();
    private int submissionId;

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
        initializeData();
    }

    @FXML
    public void initialize() {
        // Maintain original visibility settings
        statusComboBox.setVisible(false);
        urgencyComboBox.setVisible(false);

        statusComboBox.getItems().setAll(
                Submission.STATUS.PENDING.toString(),
                Submission.STATUS.APPROVED.toString(),
                Submission.STATUS.RESPONDED.toString()
        );

        urgencyComboBox.getItems().setAll(
                Submission.URGENCYLEVEL.LOW.toString(),
                Submission.URGENCYLEVEL.MEDIUM.toString(),
                Submission.URGENCYLEVEL.HIGH.toString()
        );
    }

    private void initializeData() {
        submission = submissionServices.getSubmissionById(submissionId);
        if (submission != null) {
            descriptionField.setText(submission.getDescription());
            statusComboBox.setValue(submission.getStatus().toString());
            urgencyComboBox.setValue(submission.getUrgencyLevel().toString());
            dateSubmissionField.setValue(submission.getDateSubmission().toLocalDate());
            carIdField.setText(String.valueOf(submission.getIdCar()));
            userIdField.setText(String.valueOf(submission.getIdUser()));
        }
    }

    @FXML
    private void handleUpdateSubmission() {
        if (!validateInput()) {
            return;
        }

        try {
            updateSubmissionFromForm();
            submissionServices.update(submission);
            showSuccessAlert();
        } catch (Exception e) {
            showAlert("Update Error", "Error updating submission: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        // Validate description
        if (descriptionField.getText().isEmpty()) {
            showAlert("Validation Error", "Description cannot be empty");
            return false;
        }
        if (descriptionField.getText().length() > 255) {
            showAlert("Validation Error", "Description cannot exceed 255 characters");
            return false;
        }

        // Validate date
        if (dateSubmissionField.getValue() == null) {
            showAlert("Validation Error", "Please select a date");
            return false;
        }
        if (dateSubmissionField.getValue().isAfter(LocalDate.now())) {
            showAlert("Validation Error", "Date cannot be in the future");
            return false;
        }

        // Validate car ID
        try {
            int carId = Integer.parseInt(carIdField.getText());
            if (carId <= 0) {
                showAlert("Validation Error", "Car ID must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid Car ID format");
            return false;
        }

        // Validate user ID
        try {
            int userId = Integer.parseInt(userIdField.getText());
            if (userId <= 0) {
                showAlert("Validation Error", "User ID must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid User ID format");
            return false;
        }

        // Validate hidden combobox values
        if (statusComboBox.getValue() == null) {
            showAlert("Validation Error", "Invalid status value");
            return false;
        }
        if (urgencyComboBox.getValue() == null) {
            showAlert("Validation Error", "Invalid urgency level");
            return false;
        }

        return true;
    }

    private void updateSubmissionFromForm() {
        submission.setDescription(descriptionField.getText());
        submission.setStatus(Submission.STATUS.valueOf(statusComboBox.getValue()));
        submission.setUrgencyLevel(Submission.URGENCYLEVEL.valueOf(urgencyComboBox.getValue()));
        submission.setDateSubmission(Date.valueOf(dateSubmissionField.getValue()));
        submission.setIdCar(Integer.parseInt(carIdField.getText()));
        submission.setIdUser(Integer.parseInt(userIdField.getText()));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Submission Updated");
        alert.setHeaderText(null);
        alert.setContentText("Submission updated successfully!");
        alert.showAndWait();
    }
}