package tn.esprit.controller;
import javafx.concurrent.Worker;
import netscape.javascript.JSObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.JSONObject;
import tn.esprit.entities.Warehouse;
import tn.esprit.services.WarehouseServices;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private WebView webViewMap;
    @FXML
    public void initialize() {
        WarehouseServices ws = new WarehouseServices();
        WebEngine webEngine = webViewMap.getEngine();
        File file = new File("src/main/resources/Assets/map.html");
        webEngine.load(file.toURI().toString());
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaApp", this);
            }
        });
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
        WarehouseServices ws = new WarehouseServices();
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
        String address=warehouse.getStreet()+", "+ warehouse.getCity() +", "+warehouse.getPostalCode();
        WarehouseServices ws = new WarehouseServices();
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
        try {
            if(ws.checkWarehouseExists(address)){
                error.append("\nWarehouse already exists.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    public void sendCoordinates(double lat, double lng) {
        Platform.runLater(() -> {
            fetchAddressFromCoordinates(lat, lng);
        });
    }
    private void fetchAddressFromCoordinates(double lat, double lng) {
        String url = "https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + lng + "&format=json&accept-language=en";

        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject address = jsonResponse.getJSONObject("address");

                String city = address.optString("city", address.optString("town", address.optString("village", "Unknown City")));
                String street = address.optString("road", "Unknown Street");
                String postalCode = address.optString("postcode", "Unknown Postal Code");
                Platform.runLater(() -> {
                    cityWarehouse.setText(city);
                    streetWarehouse.setText(street);
                    postalCodeWarehouse.setText(postalCode);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    cityWarehouse.setText("Error");
                    streetWarehouse.setText("Error");
                    postalCodeWarehouse.setText("Error");
                });
            }
        }).start();
    }


}

