package tn.esprit.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import tn.esprit.entities.Assignment;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.AssignmentServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListAssignmentController {

    @FXML
    private TableView<Assignment> assignmentsTableView;

    @FXML
    private TableColumn<Assignment, String> descriptionAssignmentColumn;

    @FXML
    private TableColumn<Assignment, String> statusAssignmentColumn;

    @FXML
    private TableColumn<Assignment, String> carModelColumn;

    @FXML
    private TableColumn<Assignment, String> mechanicsColumn;

    @FXML
    private TableColumn<Assignment, String> dateAssignmentColumn; // New Column for Date

    @FXML
    private TableColumn<Assignment, Void> actionColumn;

    private final AssignmentServices assignmentServices;

    @FXML
    private TextField searchField;

    public ListAssignmentController() {
        this.assignmentServices = new AssignmentServices();
    }

    @FXML
    private void searchAssignments() throws SQLException {
        String searchText = searchField.getText().toLowerCase();

        if (searchText.isEmpty()) {
            assignmentsTableView.setItems(FXCollections.observableArrayList(assignmentServices.returnList()));
        } else {
            ObservableList<Assignment> filteredList = assignmentsTableView.getItems()
                    .filtered(a -> a.getDescriptionAssignment().toLowerCase().contains(searchText));
            assignmentsTableView.setItems(filteredList);
        }
    }

    @FXML
    private void addAssignment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddAssignment.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Assignment");
            stage.setScene(new Scene(root));
            stage.setOnHiding(event -> {
                try {
                    refreshAssignmentsTable();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshAssignmentsTable() throws SQLException {
        ObservableList<Assignment> updatedAssignments = FXCollections.observableArrayList(assignmentServices.returnList());
        assignmentsTableView.setItems(updatedAssignments);
    }

    @FXML
    private void switchToMechanics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListMechanicInterface.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) assignmentsTableView.getScene().getWindow(); // Get the current window
            stage.getScene().setRoot(root);
            stage.setTitle("List of Mechanics");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateAssignment(Assignment assignment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateAssignmentInterface.fxml"));
            Parent root = loader.load();
            UpdateAssignmentController controller = loader.getController();
            controller.initData(assignment);
            Stage stage = new Stage();
            stage.setTitle("Update Assignment");
            stage.setScene(new Scene(root));
            stage.setOnHiding(event -> {
                try {
                    refreshAssignmentsTable();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteAssignment(Assignment assignment) throws SQLException {
        assignmentServices.delete(assignment);
        assignmentsTableView.getItems().remove(assignment);
        System.out.println("Deleted assignment with ID: " + assignment.getIdAssignment());
    }

    @FXML
    private void initialize() throws SQLException {
        // Bind columns to Assignment model properties
        descriptionAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("descriptionAssignment"));
        statusAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("statusAssignment"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("carModel"));

        // Bind the mechanics column to a list of mechanics
        mechanicsColumn.setCellValueFactory(cellData -> {
            List<Mechanic> mechanics = cellData.getValue().getMechanics();
            String mechanicNames = mechanics.stream()
                    .map(Mechanic::getNameMechanic)
                    .reduce((m1, m2) -> m1 + ", " + m2)
                    .orElse("No Mechanics Assigned");
            return new SimpleStringProperty(mechanicNames);
        });

        // Bind the date column to a formatted string representation of the LocalDateTime
        dateAssignmentColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getDateAssignment();
            if (date != null) {
                String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                return new SimpleStringProperty(formattedDate);
            } else {
                return new SimpleStringProperty("No Date");
            }
        });

        // Load data into the table
        ObservableList<Assignment> assignments = FXCollections.observableArrayList(assignmentServices.returnList());
        assignmentsTableView.setItems(assignments);

        // Set row factory to handle double-click events
        assignmentsTableView.setRowFactory(tv -> {
            TableRow<Assignment> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Assignment assignment = row.getItem();
                    showAssignmentDetails(assignment);
                }
            });
            return row;
        });

        // Set up the action column with update and delete buttons
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                updateButton.getStyleClass().add("primary-button");
                deleteButton.getStyleClass().add("delete-button");

                updateButton.setStyle("-fx-background-color: #00bba8; -fx-text-fill: white;");

                updateButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    updateAssignment(assignment);
                });

                deleteButton.setOnAction(event -> {
                    Assignment assignment = getTableView().getItems().get(getIndex());
                    try {
                        deleteAssignment(assignment);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, updateButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void showAssignmentDetails(Assignment assignment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowDetailsAssignment.fxml"));
            Parent root = loader.load();

            ShowDetailsAssignmentController controller = loader.getController();
            controller.initData(assignment);

            Stage stage = new Stage();
            stage.setTitle("Assignment Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}