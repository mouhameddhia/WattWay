package tn.esprit.Controllers;

import javafx.scene.control.*;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ListMechanicController {

    @FXML
    private TableView<Mechanic> mechanicsTableView;
    @FXML
    private TableColumn<Mechanic, String> nameMechanicColumn;
    @FXML
    private TableColumn<Mechanic, String> specialityMechanicColumn;
    @FXML
    private TableColumn<Mechanic, String> emailMechanicColumn;
    @FXML
    private TableColumn<Mechanic, Integer> carsRepairedColumn;
    @FXML
    private TableColumn<Mechanic, String> imageColumn;
    @FXML
    private TableColumn<Mechanic, Void> actionColumn;
    @FXML
    private TextField searchField;

    private final MechanicServices mechanicServices;

    public ListMechanicController() {
        this.mechanicServices = new MechanicServices();
    }

    @FXML
    private void initialize() throws SQLException {
        nameMechanicColumn.setCellValueFactory(new PropertyValueFactory<>("nameMechanic"));
        specialityMechanicColumn.setCellValueFactory(new PropertyValueFactory<>("specialityMechanic"));
        emailMechanicColumn.setCellValueFactory(new PropertyValueFactory<>("emailMechanic"));
        carsRepairedColumn.setCellValueFactory(new PropertyValueFactory<>("carsRepaired"));

        // Display Image using ImageView
        imageColumn.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    File file = new File(imagePath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString(), 50, 50, true, true);
                        imageView.setImage(image);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Load mechanics into the table
        refreshMechanicsTable();

        // Action buttons (Update & Delete)
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                updateButton.getStyleClass().add("primary-button");
                deleteButton.getStyleClass().add("delete-button");

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
        mechanicsTableView.setRowFactory(tv -> {
            TableRow<Mechanic> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Mechanic mechanic = row.getItem();
                    showMechanicDetails(mechanic);
                }
            });
            return row;
        });
    }
    private void showMechanicDetails(Mechanic mechanic) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowDetailsMechanic.fxml"));
            Parent root = loader.load();

            ShowDetailsMechanicController controller = loader.getController();
            controller.initData(mechanic);

            Stage stage = new Stage();
            stage.setTitle("Mechanic Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void searchMechanics() throws SQLException {
        String searchText = searchField.getText().toLowerCase();

        if (searchText.isEmpty()) {
            mechanicsTableView.setItems(FXCollections.observableArrayList(mechanicServices.returnList()));
        } else {
            ObservableList<Mechanic> filteredList = FXCollections.observableArrayList();
            for (Mechanic m : mechanicServices.returnList()) {
                if (m.getNameMechanic().toLowerCase().contains(searchText) ||
                        m.getEmailMechanic().toLowerCase().contains(searchText) ||
                        String.valueOf(m.getCarsRepaired()).contains(searchText)) {
                    filteredList.add(m);
                }
            }
            mechanicsTableView.setItems(filteredList);
        }
    }

    private void updateMechanic(Mechanic mechanic) {
        try {
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

            Stage stage = (Stage) mechanicsTableView.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("List of Assignments");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openMechanicStatistics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MechanicStatistics.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mechanic Statistics");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
