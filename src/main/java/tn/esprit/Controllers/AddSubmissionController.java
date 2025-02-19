package tn.esprit.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Submission;
import tn.esprit.entities.Submission.STATUS;
import tn.esprit.entities.Submission.URGENCYLEVEL;
import tn.esprit.services.SubmissionServices;

import java.sql.Date;
import java.sql.SQLException;

public class AddSubmissionController {

    @FXML private TextField descriptionid;
    @FXML private DatePicker dateid;
    @FXML private TextField carid;
    @FXML private TextField userid;
    @FXML private ComboBox<STATUS> statusid;
    @FXML private ComboBox<URGENCYLEVEL> urgencyLevelid;
    @FXML private Label st;
    @FXML private Label ur;


    @FXML
    void initialize() {
        // Set default values and disable ComboBoxes
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
            SubmissionServices ss = new SubmissionServices();

            try {
                Submission submission = new Submission(
                        descriptionid.getText(),
                        statusid.getValue(), // Default: PENDING
                        urgencyLevelid.getValue(), // Default: MEDIUM
                        Date.valueOf(dateid.getValue()),
                        Integer.parseInt(carid.getText()),
                        Integer.parseInt(userid.getText())
                );

                ss.add(submission);

                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Submission added successfully!");
                clearFields();

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Error adding submission: " + e.getMessage());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error",
                        "Car ID and User ID must be numbers");
            }
        }
    }

    private boolean validateFields() {
        if (descriptionid.getText().isEmpty() ||
                dateid.getValue() == null ||
                carid.getText().isEmpty() ||
                userid.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fill all required fields");
            return false;
        }

        try {
            Integer.parseInt(carid.getText());
            Integer.parseInt(userid.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error",
                    "Car ID and User ID must be numbers");
            return false;
        }

        return true;
    }

    private void clearFields() {
        descriptionid.clear();
        dateid.setValue(null);
        carid.clear();
        userid.clear();
        // Reset to default values if needed
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

    @FXML
    void nextP(ActionEvent event) {
        // Navigation logic if needed
        System.out.println("Next button clicked!");
    }

    public void setCarId(int idCar) {
        this.carid.setText(String.valueOf(idCar));
    }
}