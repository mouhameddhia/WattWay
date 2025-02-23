package tn.esprit.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import tn.esprit.entities.Response;
import tn.esprit.entities.Response.TYPERESPONSE;
import tn.esprit.services.ResponseServices;

import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.function.UnaryOperator;

public class AddResponseController {

    @FXML private TextField messageField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<TYPERESPONSE> typeComboBox;
    @FXML private TextField userIdField;
    @FXML private TextField submissionIdField;


    private String pendingMessage = null;

    @FXML
    void initialize() {
        // ComboBox initialization
        typeComboBox.getItems().addAll(TYPERESPONSE.values());
        if (messageField == null) {
            System.out.println("messageField is null in initialize!");
        }
        // Add this block
        if (pendingMessage != null && messageField != null) {
            messageField.setText(pendingMessage);
            pendingMessage = null;
            System.out.println("Applied pending message: " + messageField.getText());
        }
        // DatePicker configuration
        datePicker.setValue(LocalDate.now());

        // Input restrictions
        configureTextFields();

        // Make submission ID non-editable
        submissionIdField.setEditable(false);

        System.out.println("AddResponseController initialized");
        if (messageField == null) {
            System.out.println("messageField is null in initialize!");
        }
    }

    private void configureTextFields() {
        // Message field: 255 characters limit
        messageField.setTextFormatter(new TextFormatter<>(lengthFilter(255)));

        // User ID: numeric only
        userIdField.setTextFormatter(new TextFormatter<>(numericFilter()));
    }

    private UnaryOperator<TextFormatter.Change> lengthFilter(int maxLength) {
        return change -> {
            if (change.getControlNewText().length() <= maxLength) {
                return change;
            }
            return null;
        };
    }

    private UnaryOperator<TextFormatter.Change> numericFilter() {
        return change -> {
            if (change.getText().matches("\\d*")) {
                return change;
            }
            return null;
        };
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
            }
        }
    }

    private boolean validateFields() {
        // Empty field check
        if (messageField.getText().isEmpty() || datePicker.getValue() == null
                || typeComboBox.getValue() == null || userIdField.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fill all required fields");
            return false;
        }

        // Message length check
        if (messageField.getText().length() > 255) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Message cannot exceed 255 characters");
            return false;
        }

        // Date validation
        if (datePicker.getValue().isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Date cannot be in the future");
            return false;
        }

        // ID validation
        try {
            int userId = Integer.parseInt(userIdField.getText());
            int submissionId = Integer.parseInt(submissionIdField.getText());

            if (userId <= 0 || submissionId <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error",
                        "IDs must be positive numbers");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Invalid number format in ID fields");
            return false;
        }

        return true;
    }

    private void clearFields() {
        messageField.clear();
        datePicker.setValue(LocalDate.now());
        typeComboBox.getSelectionModel().clearSelection();
        userIdField.clear();
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
        // Navigation logic
        System.out.println("Next button clicked!");
    }

    public void setSubmissionId(int idSubmission) {
        if (submissionIdField != null) {
            submissionIdField.setText(String.valueOf(idSubmission));
        }
    }


    public void setGeneratedMessage(String message) {
        System.out.println("Setting message: " + message);
        if (messageField == null) {
            System.out.println("messageField is null. Message stored: " + message);
            pendingMessage = message;
        } else {
            Platform.runLater(() -> {
                messageField.setText(message);
                System.out.println("Message set to field: " + message);
            });
        }
    }


}


