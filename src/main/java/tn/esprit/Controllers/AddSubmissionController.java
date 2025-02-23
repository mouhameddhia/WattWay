package tn.esprit.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Submission;
import tn.esprit.entities.Submission.STATUS;
import tn.esprit.entities.Submission.URGENCYLEVEL;
import tn.esprit.services.SubmissionServices;
import tn.esprit.utils.ProfanityFilter;
import tn.esprit.utils.SoundPlayer;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddSubmissionController {

    @FXML private TextField descriptionid;
    @FXML private DatePicker dateid;
    @FXML private TextField carid;
    @FXML private TextField userid;
    @FXML private ComboBox<STATUS> statusid;
    @FXML private ComboBox<URGENCYLEVEL> urgencyLevelid;
    @FXML private Label st;
    @FXML private Label ur;

    private int parsedCarId;
    private int parsedUserId;
    private ProfanityFilter profanityFilter;

    @FXML
    void initialize() {
        profanityFilter = new ProfanityFilter();

        // Add listener to description field
        descriptionid.textProperty().addListener((observable, oldValue, newValue) -> {
            if (profanityFilter.containsProfanity(newValue)) {
                // Revert to old value and show warning
                descriptionid.setText(oldValue);
                showProfanityAlert();
            }
        });

        // Initialize ComboBoxes with default values
        statusid.setVisible(false);
        urgencyLevelid.setVisible(false);
        st.setVisible(false);
        ur.setVisible(false);

        statusid.getItems().add(STATUS.PENDING);
        statusid.setValue(STATUS.PENDING);
        statusid.setDisable(true);

        urgencyLevelid.getItems().add(URGENCYLEVEL.MEDIUM);
        urgencyLevelid.setValue(URGENCYLEVEL.MEDIUM);
        urgencyLevelid.setDisable(true);
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        if (validateFields()) {
            try {
                // Additional profanity check before submission
                if (profanityFilter.containsProfanity(descriptionid.getText())) {
                    showProfanityAlert();
                    return;
                }

                Submission submission = new Submission(
                        descriptionid.getText(),
                        statusid.getValue(),
                        urgencyLevelid.getValue(),
                        Date.valueOf(dateid.getValue()),
                        parsedCarId,
                        parsedUserId
                );

                SubmissionServices ss = new SubmissionServices();
                ss.add(submission);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Submission added successfully!");
                clearFields();

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error adding submission: " + e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        // Check for profanity first
        if (profanityFilter.containsProfanity(descriptionid.getText())) {
            showProfanityAlert();
            return false;
        }

        // Check empty fields
        if (descriptionid.getText().isEmpty() || dateid.getValue() == null
                || carid.getText().isEmpty() || userid.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill all required fields");
            return false;
        }

        // Validate description length
        if (descriptionid.getText().length() > 255) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Description cannot exceed 255 characters");
            return false;
        }

        // Validate date not in future
        if (dateid.getValue().isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Date cannot be in the future");
            return false;
        }

        // Validate numeric IDs
        try {
            parsedCarId = Integer.parseInt(carid.getText());
            parsedUserId = Integer.parseInt(userid.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Car ID and User ID must be numeric values");
            return false;
        }

        // Validate positive IDs
        if (parsedCarId <= 0 || parsedUserId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Car ID and User ID must be positive numbers");
            return false;
        }

        return true;
    }

    private void clearFields() {
        descriptionid.clear();
        dateid.setValue(null);
        carid.clear();
        userid.clear();
        statusid.setValue(STATUS.PENDING);
        urgencyLevelid.setValue(URGENCYLEVEL.MEDIUM);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showProfanityAlert() {
        // Play the alert sound
        SoundPlayer.playSound("src/main/resources/beep-05.wav"); // Adjust the path as necessary

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Inappropriate Content Detected");
        alert.setHeaderText("Your message contains inappropriate content");
        alert.setContentText("Please revise your message. Inappropriate content is not allowed.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("/addSubmission.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

    @FXML
    void nextP(ActionEvent event) {
        // Navigation logic
        System.out.println("Next button clicked!");
    }

    public void setCarId(int idCar) {
        this.carid.setText(String.valueOf(idCar));
    }
}