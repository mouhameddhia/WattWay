package tn.esprit.Controllers;

import tn.esprit.entities.Assignment;
import tn.esprit.services.AssignmentServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;

public class UpdateAssignmentController {

    @FXML
    private TextField descriptionAssignmentField;
    @FXML
    private TextField statusAssignmentField;
    @FXML
    private TextField idUserField;
    @FXML
    private TextField idMechanicField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final AssignmentServices assignmentServices = new AssignmentServices();
    private Assignment assignment;

    public void initData(Assignment assignment) {
        this.assignment = assignment;
        descriptionAssignmentField.setText(assignment.getDescriptionAssignment());
        statusAssignmentField.setText(assignment.getStatusAssignment());
        idUserField.setText(String.valueOf(assignment.getIdUser()));
        idMechanicField.setText(String.valueOf(assignment.getIdMechanic()));
    }

    @FXML
    private void saveAssignment() {
        assignment.setDescriptionAssignment(descriptionAssignmentField.getText());
        assignment.setStatusAssignment(statusAssignmentField.getText());
        assignment.setIdUser(Integer.parseInt(idUserField.getText()));
        assignment.setIdMechanic(Integer.parseInt(idMechanicField.getText()));
        try {
            assignmentServices.update(assignment);
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
