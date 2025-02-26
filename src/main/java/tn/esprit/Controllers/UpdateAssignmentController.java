package tn.esprit.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import tn.esprit.entities.Assignment;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.AssignmentServices;
import tn.esprit.services.MechanicServices;
import tn.esprit.services.CarServices;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class UpdateAssignmentController {

    @FXML
    private TextField descriptionAssignmentField;
    @FXML
    private ComboBox<Assignment.Status> statusAssignmentComboBox;
    @FXML
    private ComboBox<Integer> carIdComboBox;
    @FXML
    private ListView<Mechanic> mechanicListView;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final AssignmentServices assignmentServices = new AssignmentServices();
    private final MechanicServices mechanicServices = new MechanicServices();
    private final CarServices carServices = new CarServices();
    private Assignment assignment;

    public void initData(Assignment assignment) {
        this.assignment = assignment;

        // Set Description Field
        descriptionAssignmentField.setText(assignment.getDescriptionAssignment());

        // Populate Status ComboBox
        statusAssignmentComboBox.getItems().addAll(Assignment.Status.values());
        statusAssignmentComboBox.setValue(assignment.getStatusAssignment());  // ✅ Pre-select status

        // Populate User ID ComboBox from Database
        loadUserIds();

        // Set the current User ID
        carIdComboBox.setValue(assignment.getIdCar());

        // Load Mechanics
        loadMechanics(assignment);
    }

    private void loadUserIds() {
        try {
            List<Integer> userIds = carServices.getAllCarIds();  // Fetch user IDs from DB
            ObservableList<Integer> userIdList = FXCollections.observableArrayList(userIds);
            carIdComboBox.setItems(userIdList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMechanics(Assignment assignment) {
        try {
            List<Mechanic> allMechanics = mechanicServices.returnList();
            List<Mechanic> assignedMechanics = assignmentServices.getMechanicsByAssignmentId(assignment.getIdAssignment());

            ObservableList<Mechanic> mechanicsObservable = FXCollections.observableArrayList(allMechanics);
            mechanicListView.setItems(mechanicsObservable);
            mechanicListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            // Pre-select assigned mechanics
            for (Mechanic mechanic : assignedMechanics) {
                for (Mechanic listMechanic : mechanicListView.getItems()) {
                    if (listMechanic.getIdMechanic() == mechanic.getIdMechanic()) {
                        mechanicListView.getSelectionModel().select(listMechanic);
                        break;
                    }
                }
            }

            // Display mechanics properly in the ListView
            mechanicListView.setCellFactory(lv -> new ListCell<>() {
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void saveAssignment() {
        try {
            String description = descriptionAssignmentField.getText();
            Assignment.Status status = statusAssignmentComboBox.getValue();
            Integer carId = carIdComboBox.getValue();

            if (description.isEmpty() || status == null || carId == null) {
                showAlert("Missing Information", "Please fill all required fields.");
                return;
            }

            // Check if the User ID exists
            if (!carServices.isCarExists(carId)) {
                showAlert("Invalid User", "The selected Car ID does not exist.");
                return;
            }

            // Update assignment
            assignment.setDescriptionAssignment(description);
            assignment.setStatusAssignment(status);
            assignment.setIdCar(carId);
            assignmentServices.update(assignment);

            // Update assigned mechanics
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
