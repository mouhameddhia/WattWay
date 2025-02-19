package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Submission;
import tn.esprit.services.SubmissionServices;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

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
        // Initialize combo boxes with enum values
        statusComboBox.getItems().setAll(
                Submission.STATUS.PENDING.toString(),
                Submission.STATUS.APPROVED.toString(),
                Submission.STATUS.RESPONDED.toString()

        );
        statusComboBox.setVisible(false);

        urgencyComboBox.setVisible(false);




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
        try {
            updateSubmissionFromForm();
            submissionServices.update(submission);
            showSuccessAlert();
        } catch (Exception e) {
            showAlert("Update Error", "Error updating submission: " + e.getMessage());
        }
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