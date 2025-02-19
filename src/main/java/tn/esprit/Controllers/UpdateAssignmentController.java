package tn.esprit.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.entities.Assignment;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.AssignmentServices;
import tn.esprit.services.MechanicServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateAssignmentController {

    @FXML
    private TextField descriptionAssignmentField;
    @FXML
    private TextField statusAssignmentField;
    @FXML
    private TextField idUserField;
    @FXML
    private ListView<Mechanic> mechanicListView;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final AssignmentServices assignmentServices = new AssignmentServices();
    private final MechanicServices mechanicServices = new MechanicServices();
    private Assignment assignment;

    public void initData(Assignment assignment) {
        this.assignment = assignment;
        descriptionAssignmentField.setText(assignment.getDescriptionAssignment());
        statusAssignmentField.setText(assignment.getStatusAssignment());
        idUserField.setText(String.valueOf(assignment.getIdUser()));

        loadMechanics(assignment);
    }

    private void loadMechanics(Assignment assignment) {
        try {
            // Get all mechanics
            List<Mechanic> allMechanics = mechanicServices.returnList();
            // Get mechanics assigned to this assignment
            List<Mechanic> assignedMechanics = assignmentServices.getMechanicsByAssignmentId(assignment.getIdAssignment());

            // Convert to observable list
            ObservableList<Mechanic> mechanicsObservable = FXCollections.observableArrayList(allMechanics);
            mechanicListView.setItems(mechanicsObservable);
            mechanicListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

            // Preselect assigned mechanics
            for (Mechanic mechanic : assignedMechanics) {
                mechanicListView.getSelectionModel().select(mechanic);
            }

            // Display mechanic info properly
            mechanicListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Mechanic mechanic, boolean empty) {
                    super.updateItem(mechanic, empty);
                    if (empty || mechanic == null) {
                        setText(null);
                    } else {
                        setText(mechanic.getNameMechanic() + " - " + mechanic.getSpecialityMechanic() + " (ID: " + mechanic.getIdMechanic() + ")");
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveAssignment() {
        try {
            // Get input values
            String description = descriptionAssignmentField.getText();
            String status = statusAssignmentField.getText();
            int idUser;

            // Validate if idUser is a valid integer
            try {
                idUser = Integer.parseInt(idUserField.getText());
            } catch (NumberFormatException e) {
                System.out.println("Invalid User ID format.");
                return; // Exit if not a valid number
            }

            // Check if idUser exists in the user table
            if (!assignmentServices.isUserExists(idUser)) {
                System.out.println("User ID does not exist in the database.");
                return; // Exit if user doesn't exist
            }

            // Update assignment object
            assignment.setDescriptionAssignment(description);
            assignment.setStatusAssignment(status);
            assignment.setIdUser(idUser);

            // Update assignment in the database
            assignmentServices.update(assignment);

            // Update selected mechanics
            List<Mechanic> selectedMechanics = mechanicListView.getSelectionModel().getSelectedItems();
            assignmentServices.updateAssignmentWithMechanics(assignment.getIdAssignment(), selectedMechanics);

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
