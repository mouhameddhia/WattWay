package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.Warehouse;
import tn.esprit.services.WarehouseServices;

import java.io.IOException;
import java.sql.SQLException;


public class AddWarehouse {
    @FXML
    private TextField capacityWarehouse;

    @FXML
    private TextField cityWarehouse;

    @FXML
    private TextField postalCodeWarehouse;

    @FXML
    private TextField streetWarehouse;

    @FXML
    private TableColumn<Warehouse, Integer> tableViewCapacityWarehouse;

    @FXML
    private TableColumn<Warehouse, String> tableViewCityWarehouse;

    @FXML
    private TableColumn<Warehouse, Integer> tableViewPostalCodeWarehouse;

    @FXML
    private TableColumn<Warehouse, String> tableViewStreetWarehouse;
    @FXML
    private TableView<Warehouse> tableViewWarehouse;
    @FXML
    private TableColumn<Warehouse, Void> tableViewDeleteWarehouse;
    @FXML
    private TableColumn<Warehouse, Void> tableViewUpdateWarehouse;
    @FXML
    private TableColumn<Warehouse, Void> tableViewImportWarehouse;
    @FXML
    public void initialize() {
        WarehouseServices ws = new WarehouseServices();
        try {
            ObservableList<Warehouse> observableList = FXCollections.observableList(ws.retrieve());
            tableViewWarehouse.setItems(observableList);
            tableViewCityWarehouse.setCellValueFactory(new PropertyValueFactory<>("city"));
            tableViewStreetWarehouse.setCellValueFactory(new PropertyValueFactory<>("street"));
            tableViewPostalCodeWarehouse.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            tableViewCapacityWarehouse.setCellValueFactory(new PropertyValueFactory<>("capacityWarehouse"));

            tableViewDeleteWarehouse.setCellFactory(tc -> new TableCell<>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        Warehouse warehouse = getTableView().getItems().get(getIndex());
                        try {
                            ws.delete(warehouse.getIdWarehouse());
                            initialize();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }


            @Override
            protected void updateItem (Void item,boolean empty){
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                    HBox hBox = new HBox(deleteButton);
                    hBox.setAlignment(Pos.CENTER);
                    setGraphic(hBox);
                }
            }
            });
            tableViewUpdateWarehouse.setCellFactory(tc -> new TableCell<>() {
                private final Button updateButton = new Button("Update");

                {
                    updateButton.setOnAction(event -> {
                        Warehouse warehouse = new Warehouse();
                        warehouse.setIdWarehouse(getTableView().getItems().get(getIndex()).getIdWarehouse());
                        warehouse.setCity(cityWarehouse.getText());
                        warehouse.setStreet(streetWarehouse.getText());
                        if(postalCodeWarehouse.getText().length()==0)
                            warehouse.setPostalCode(-1);
                        else
                            warehouse.setPostalCode(Integer.parseInt(postalCodeWarehouse.getText()));
                        if(capacityWarehouse.getText().length()==0)
                            warehouse.setCapacityWarehouse(-1);
                        else
                            warehouse.setCapacityWarehouse(Integer.parseInt(capacityWarehouse.getText()));

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        if (!verifWarehouseDetails(warehouse).equals("")) {

                            alert.setTitle("Fatal error");
                            alert.setHeaderText("Warehouse cannot be added");
                            alert.setContentText(verifWarehouseDetails(warehouse));
                            alert.showAndWait();
                        } else {
                            WarehouseServices ws = new WarehouseServices();
                            try {
                                ws.update(warehouse);
                                initialize();
                                alert.setTitle("Success");
                                alert.setHeaderText("Warehouse service");
                                alert.setContentText("Warehouse in " + warehouse.getStreet() + " has been updated successfully");
                                alert.showAndWait();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }


                @Override
                protected void updateItem (Void item,boolean empty){
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        updateButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                        HBox hBox = new HBox(updateButton);
                        hBox.setAlignment(Pos.CENTER);
                        setGraphic(hBox);
                    }
                }
            });
            tableViewImportWarehouse.setCellFactory(tc -> new TableCell<>() {
                private final Button importButton = new Button("Import");

                {
                    importButton.setOnAction(event -> {
                        Warehouse warehouse = getTableView().getItems().get(getIndex());
                        cityWarehouse.setText(warehouse.getCity());
                        streetWarehouse.setText(warehouse.getStreet());
                        postalCodeWarehouse.setText(Integer.toString(warehouse.getPostalCode()));
                        capacityWarehouse.setText(Integer.toString(warehouse.getCapacityWarehouse()));
                    });
                }


                @Override
                protected void updateItem (Void item,boolean empty){
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        importButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                        HBox hBox = new HBox(importButton);
                        hBox.setAlignment(Pos.CENTER);
                        setGraphic(hBox);
                    }
                }
            });
        }
        catch (SQLException es) {
            throw new RuntimeException(es);
        }
    }

    @FXML
    public void addWarehouseOnClick(ActionEvent event) {
        Warehouse warehouse = new Warehouse();
        warehouse.setCity(cityWarehouse.getText());
        warehouse.setStreet(streetWarehouse.getText());
        if(postalCodeWarehouse.getText().length()==0)
            warehouse.setPostalCode(-1);
        else
            warehouse.setPostalCode(Integer.parseInt(postalCodeWarehouse.getText()));
        if(capacityWarehouse.getText().length()==0)
            warehouse.setCapacityWarehouse(-1);
        else
            warehouse.setCapacityWarehouse(Integer.parseInt(capacityWarehouse.getText()));

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (!verifWarehouseDetails(warehouse).equals("")) {

            alert.setTitle("Fatal error");
            alert.setHeaderText("Warehouse cannot be added");
            alert.setContentText(verifWarehouseDetails(warehouse));
            alert.showAndWait();
        } else {
            WarehouseServices ws = new WarehouseServices();
            try {
                ws.add(warehouse);
                initialize();
                alert.setTitle("Success");
                alert.setHeaderText("Warehouse service");
                alert.setContentText("Warehouse in " + warehouse.getStreet() + " has been added successfully");
                alert.showAndWait();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public String verifWarehouseDetails(Warehouse warehouse) {
        StringBuilder error = new StringBuilder();
        if (warehouse.getCity() == null || warehouse.getCity().isEmpty()) {
            error.append("\nCity cannot be empty.");
        } else if (!warehouse.getCity().matches("[a-zA-Z]+")) {
            error.append("\nCity should be alphabetical.");
        }
        if (warehouse.getStreet() == null || warehouse.getStreet().isEmpty()) {
            error.append("\nStreet cannot be empty.");
        } else if (!warehouse.getStreet().matches("[a-zA-Z0-9 ]+")) {
            error.append("\nStreet cannot contain special characters or symbols.");
        }
        if (warehouse.getPostalCode() <= 1000 || warehouse.getPostalCode() > 9999) {
            error.append("\nPostal code is invalid.");
        }
        if (warehouse.getCapacityWarehouse() < 0) {
            error.append("\nWarehouse capacity must be a positive number.");
        }
        return error.toString();
    }
    @FXML
    void clearElementsWarehouse(ActionEvent event) {
        cityWarehouse.setText(null);
        streetWarehouse.setText(null);
        postalCodeWarehouse.setText(null);
        capacityWarehouse.setText(null);
    }
    @FXML
    void openAddCar(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AddCar.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Car");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void openAddWarehouse(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AddWarehouse.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Warehouse");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void openShowBill(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ShowBill.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("List of bills");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

