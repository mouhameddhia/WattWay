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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListAssignmentController {

    @FXML
    private TableView<Assignment> assignmentsTableView;
    @FXML
    private TableColumn<Assignment, Integer> idAssignmentColumn;
    @FXML
    private TableColumn<Assignment, String> descriptionAssignmentColumn;
    @FXML
    private TableColumn<Assignment, String> statusAssignmentColumn;
    @FXML
    private TableColumn<Assignment, Integer> carModelColumn;
    @FXML
    private TableColumn<Assignment, String> mechanicsColumn;
    @FXML
    private TableColumn<Assignment, Void> actionColumn;

    private final AssignmentServices assignmentServices;
    @FXML
    private TextField searchField;

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

    public ListAssignmentController() {
        this.assignmentServices = new AssignmentServices();
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
            System.out.println("Loading FXML from: " + getClass().getResource("/UpdateAssignment.fxml"));

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

    /*
        @FXML
        private void initialize() throws SQLException {
            idAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("idAssignment"));
            descriptionAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("descriptionAssignment"));
            statusAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("statusAssignment"));
            idUserColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
            idMechanicColumn.setCellValueFactory(new PropertyValueFactory<>("idMechanic"));

            ObservableList<Assignment> assignments = FXCollections.observableArrayList(assignmentServices.returnList());
            assignmentsTableView.setItems(assignments);

            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final Button updateButton = new Button("Update");
                private final Button deleteButton = new Button("Delete");

                {
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
    */
    @FXML
    private void initialize() throws SQLException {
        //idAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("idAssignment"));
        descriptionAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("descriptionAssignment"));
        statusAssignmentColumn.setCellValueFactory(new PropertyValueFactory<>("statusAssignment"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("carModel"));

        mechanicsColumn.setCellValueFactory(cellData -> {
            List<Mechanic> mechanics = cellData.getValue().getMechanics();
            String mechanicNames = mechanics.stream()
                    .map(Mechanic::getNameMechanic)
                    .reduce((m1, m2) -> m1 + ", " + m2)
                    .orElse("No Mechanics Assigned");
            return new SimpleStringProperty(mechanicNames);
        });

        ObservableList<Assignment> assignments = FXCollections.observableArrayList(assignmentServices.returnList());
        assignmentsTableView.setItems(assignments);
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