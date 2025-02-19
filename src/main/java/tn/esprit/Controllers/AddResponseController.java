package tn.esprit.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import tn.esprit.entities.Response;
import tn.esprit.entities.Response.TYPERESPONSE;
import tn.esprit.services.ResponseServices;

import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

public class AddResponseController {

    @FXML private TextField messageField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<TYPERESPONSE> typeComboBox;
    @FXML private TextField userIdField;
    @FXML private TextField submissionIdField;

    @FXML
    void initialize() {
        // Initialize ComboBox with enum values
        typeComboBox.getItems().addAll(
                TYPERESPONSE.ACKNOWLEDGMENT,
                TYPERESPONSE.RESOLUTION,
                TYPERESPONSE.CLARIFICATIONREQUEST
        );
    }

    @FXML
    void addResponse(ActionEvent event) {
        if (validateFields()) {
            ResponseServices rs = new ResponseServices();

            try {
                Response response = new Response(
                        messageField.getText(),
                        Date.valueOf(datePicker.getValue()),
                        typeComboBox.getValue(),
                        Integer.parseInt(userIdField.getText()),
                        Integer.parseInt(submissionIdField.getText())
                );

                rs.add(response);

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Response added successfully!");
                clearFields();

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Error adding response: " + e.getMessage());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error",
                        "Invalid number format in ID fields");
            }
        }
    }

    private boolean validateFields() {
        if (messageField.getText().isEmpty() ||
                datePicker.getValue() == null ||
                typeComboBox.getValue() == null ||
                userIdField.getText().isEmpty() ||
                submissionIdField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fill all fields");
            return false;
        }

        try {
            Integer.parseInt(userIdField.getText());
            Integer.parseInt(submissionIdField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error",
                    "User ID and Submission ID must be numbers");
            return false;
        }

        return true;
    }

    private void clearFields() {
        messageField.clear();
        datePicker.setValue(null);
        typeComboBox.getSelectionModel().clearSelection();
        userIdField.clear();
        submissionIdField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void nextP(ActionEvent event) {
        // Implement navigation logic if needed
        System.out.println("Next button clicked!");
    }

    public void setSubmissionId(int idSubmission) {
        this.submissionIdField.setText(String.valueOf(idSubmission));
    }
}