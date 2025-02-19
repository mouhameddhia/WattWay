package tn.esprit.Controllers;

import tn.esprit.entities.Assignment;
import tn.esprit.services.AssignmentServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;

public class AddAssignmentController {

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

    @FXML
    private void saveAssignment() {
        String description = descriptionAssignmentField.getText();
        String status = statusAssignmentField.getText();
        String idUserText = idUserField.getText();
        String idMechanicText = idMechanicField.getText();

        if (description.isEmpty() || status.isEmpty() || idUserText.isEmpty() || idMechanicText.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }

        try {
            int idUser = Integer.parseInt(idUserText);
            int idMechanic = Integer.parseInt(idMechanicText);

            Assignment newAssignment = new Assignment();
            newAssignment.setDescriptionAssignment(description);
            newAssignment.setStatusAssignment(status);
            newAssignment.setIdUser(idUser);
            newAssignment.setIdMechanic(idMechanic);

            assignmentServices.addP(newAssignment);
            closeWindow();
        } catch (NumberFormatException e) {
            System.out.println("Error: User ID and Mechanic ID must be numbers.");
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
}
