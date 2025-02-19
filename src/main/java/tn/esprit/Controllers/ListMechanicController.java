package tn.esprit.Controllers;

import tn.esprit.entities.Mechanic;
import tn.esprit.services.MechanicServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import java.io.IOException;
import java.sql.SQLException;

public class ListMechanicController {

    @FXML
    private TableView<Mechanic> mechanicsTableView;
    @FXML
    private TableColumn<Mechanic, Integer> idMechanicColumn;
    @FXML
    private TableColumn<Mechanic, String> nameMechanicColumn;
    @FXML
    private TableColumn<Mechanic, String> specialityMechanicColumn;
    @FXML
    private TableColumn<Mechanic, Void> actionColumn;

    private final MechanicServices mechanicServices;

    public ListMechanicController() {
        this.mechanicServices = new MechanicServices();
    }

    @FXML
    private void initialize() throws SQLException {
        idMechanicColumn.setCellValueFactory(new PropertyValueFactory<>("idMechanic"));
        nameMechanicColumn.setCellValueFactory(new PropertyValueFactory<>("nameMechanic"));
        specialityMechanicColumn.setCellValueFactory(new PropertyValueFactory<>("specialityMechanic"));

        ObservableList<Mechanic> mechanics = FXCollections.observableArrayList(mechanicServices.returnList());
        mechanicsTableView.setItems(mechanics);

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                updateButton.setOnAction(event -> {
                    Mechanic mechanic = getTableView().getItems().get(getIndex());
                    updateMechanic(mechanic);
                });

                deleteButton.setOnAction(event -> {
                    Mechanic mechanic = getTableView().getItems().get(getIndex());
                    try {
                        deleteMechanic(mechanic);
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

    private void updateMechanic(Mechanic mechanic) {
        try {
            System.out.println(mechanic.toString());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateMechanicInterface.fxml"));
            Parent root = loader.load();
            UpdateMechanicController controller = loader.getController();
            controller.initData(mechanic);
            Stage stage = new Stage();
            stage.setTitle("Update Mechanic");
            stage.setScene(new Scene(root));
            stage.setOnHiding(event -> {
                try {
                    refreshMechanicsTable();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteMechanic(Mechanic mechanic) throws SQLException {
        mechanicServices.delete(mechanic);
        mechanicsTableView.getItems().remove(mechanic);
        System.out.println("Deleted mechanic with ID: " + mechanic.getIdMechanic());
    }

    @FXML
    private void addMechanic() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMechanic.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Mechanic");
            stage.setScene(new Scene(root));
            stage.setOnHiding(event -> {
                try {
                    refreshMechanicsTable();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshMechanicsTable() throws SQLException {
        ObservableList<Mechanic> updatedMechanics = FXCollections.observableArrayList(mechanicServices.returnList());
        mechanicsTableView.setItems(updatedMechanics);
    }
    @FXML
    private void switchToAssignments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListAssignmentInterface.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) mechanicsTableView.getScene().getWindow(); // Get the current window
            stage.setScene(new Scene(root));
            stage.setTitle("List of Assignments");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
