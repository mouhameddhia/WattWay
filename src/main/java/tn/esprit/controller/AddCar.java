package tn.esprit.controller;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.Car;
import tn.esprit.entities.Warehouse;
import tn.esprit.services.CarServices;
import tn.esprit.services.WarehouseServices;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import java.util.List;
import java.util.Objects;

public class AddCar {

    private String selectedWarehouse="";
    @FXML
    private TextField imgCar;
    @FXML
    private TextField brandCar;
    @FXML
    private TextField kilometrageCar;
    @FXML
    private TextField modelCar;
    @FXML
    private TableColumn<Car, String> tableViewBrandCar;
    @FXML
    private TableView<Car> tableViewCar;
    @FXML
    private TableColumn<Car, Void> tableViewDeleteCar;
    @FXML
    private TableColumn<Car, Void> tableViewImportCar;
    @FXML
    private TableColumn<Car, Float> tableViewKilometrageCar;
    @FXML
    private TableColumn<Car, String> tableViewModelCar;
    @FXML
    private TableColumn<Car, String> tableViewStatusCar;
    @FXML
    private TableColumn<Car, String> tableViewStoredWarehouse;
    @FXML
    private TableColumn<Car, Void> tableViewUpdateCar;
    @FXML
    private TableColumn<Car, Integer> tableViewYearCar;
    @FXML
    private TableColumn<Car, Float> tableViewPriceCar;
    @FXML
    private TextField yearCar;
    @FXML
    private TextField priceCar;
    @FXML
    private MenuButton menuButtonCar;
    @FXML
    private ImageView imageViewCar;
    @FXML
    public void initialize(){
        CarServices cs = new CarServices();
        WarehouseServices warehouseServices = new WarehouseServices();
        List<Warehouse> warehouses;
        try {
            warehouses= warehouseServices.retrieve();
            for(Warehouse w : warehouses){
                try{
                    String address=warehouseServices.getWarehouseAddressById(w.getIdWarehouse());
                    MenuItem menuItem = new MenuItem(address);
                    menuItem.setOnAction(event -> {
                        menuButtonCar.setText(address);
                        selectedWarehouse = ((MenuItem) event.getSource()).getText();
                    });
                    menuButtonCar.getItems().add(menuItem);

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            ObservableList<Car> observableList = FXCollections.observableList(cs.retrieve());
            tableViewCar.setItems(observableList);
            tableViewBrandCar.setCellValueFactory(new PropertyValueFactory<>("brandCar"));
            tableViewModelCar.setCellValueFactory(new PropertyValueFactory<>("modelCar"));
            tableViewStatusCar.setCellValueFactory(new PropertyValueFactory<>("statusCar"));
            tableViewKilometrageCar.setCellValueFactory(new PropertyValueFactory<>("kilometrageCar"));
            tableViewYearCar.setCellValueFactory(new PropertyValueFactory<>("yearCar"));
            tableViewPriceCar.setCellValueFactory(new PropertyValueFactory<>("priceCar"));
            tableViewStoredWarehouse.setCellValueFactory(cellData -> {
                int warehouseId = cellData.getValue().getIdWarehouse();
                String address = null;
                try {
                    address = warehouseServices.getWarehouseAddressById(warehouseId);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return new SimpleStringProperty(address);
            });
            tableViewDeleteCar.setCellFactory(tc -> new TableCell<>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        Car car = getTableView().getItems().get(getIndex());
                        try {
                            cs.delete(car.getIdCar());
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
            tableViewUpdateCar.setCellFactory(tc -> new TableCell<>() {
                private final Button updateButton = new Button("Update");

                {
                    updateButton.setOnAction(event -> {

                        Car car = new Car();
                        car.setIdCar(getTableView().getItems().get(getIndex()).getIdCar());
                        car.setImgCar(imgCar.getText());

                        System.out.println(car.getImgCar());
                        car.setModelCar(modelCar.getText());
                        car.setBrandCar(brandCar.getText());
                        if(kilometrageCar.getText().length()==0)
                            car.setKilometrageCar(-1);
                        else
                            car.setKilometrageCar(Integer.parseInt(kilometrageCar.getText()));
                        if(priceCar.getText().length()==0)
                            car.setPriceCar(-1);
                        else
                            car.setPriceCar(Float.parseFloat(priceCar.getText()));
                        if(yearCar.getText().length()==0)
                            car.setYearCar(-1);
                        else
                            car.setYearCar(Integer.parseInt(yearCar.getText()));
                        try {
                            System.out.println(selectedWarehouse);
                            car.setIdWarehouse(warehouseServices.getWarehouseIdByAddress(selectedWarehouse));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        car.setStatusCar("available");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        if (!verifCarDetails(car).equals("")) {

                            alert.setTitle("Fatal error");
                            alert.setHeaderText("Car cannot be added");
                            alert.setContentText(verifCarDetails(car));
                            alert.showAndWait();
                        } else {
                            try {
                                cs.update(car);
                                initialize();
                                alert.setTitle("Success");
                                alert.setHeaderText("Car service");
                                alert.setContentText("Car has been updated successfully");
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
            tableViewImportCar.setCellFactory(tc -> new TableCell<>() {
                private final Button importButton = new Button("Import");

                {
                    importButton.setOnAction(event -> {
                        Car car = getTableView().getItems().get(getIndex());
                        brandCar.setText(car.getBrandCar());
                        modelCar.setText(car.getModelCar());
                        yearCar.setText(Integer.toString(car.getYearCar()));
                        kilometrageCar.setText(Integer.toString(car.getKilometrageCar()));
                        priceCar.setText(Float.toString(car.getPriceCar()));
                        try {
                            System.out.println(car.getIdWarehouse());
                            menuButtonCar.setText(warehouseServices.getWarehouseAddressById(car.getIdWarehouse()));
                            selectedWarehouse=menuButtonCar.getText();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        if (car.getImgCar()!=null) {
                            imgCar.setText(car.getImgCar());
                            loadImageFromPath(car.getImgCar());
                        }
                        else {
                            imgCar.setText("");
                            imageViewCar.setImage(null);
                        }
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @FXML
    void addCarOnClick(ActionEvent event) {
        WarehouseServices warehouseServices = new WarehouseServices();
        Car car = new Car();
        car.setImgCar(imgCar.getText());
        car.setBrandCar(brandCar.getText());
        car.setModelCar(modelCar.getText());
        if(yearCar.getText().length()==0)
            car.setYearCar(-1);
        else
            car.setYearCar(Integer.parseInt(yearCar.getText()));
        if(priceCar.getText().length()==0)
            car.setPriceCar(-1);
        else
            car.setPriceCar(Float.parseFloat(priceCar.getText()));

        if (kilometrageCar.getText().length()==0)
            car.setKilometrageCar(-1);
        else
            car.setKilometrageCar(Integer.parseInt(kilometrageCar.getText()));
        if (!Objects.equals(verifCarDetails(car), "")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fatal error");
            alert.setHeaderText("Cannot add car");
            alert.setContentText(verifCarDetails(car));
            alert.showAndWait();
        }
        else{
            CarServices cs = new CarServices();
            car.setStatusCar("available");
            try {
                if(selectedWarehouse!="") {
                    car.setIdWarehouse(warehouseServices.getWarehouseIdByAddress(selectedWarehouse));
                    cs.add(car);
                    initialize();
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Fatal error");
                    alert.setHeaderText("Cannot add car");
                    alert.setContentText("No warehouse selected, A car must be added to the lot");
                    alert.showAndWait();
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

    }
    String verifCarDetails(Car car) {
        StringBuilder error = new StringBuilder();

        if (car.getBrandCar()==null || !car.getBrandCar().matches("[a-zA-Z ]+")) {
            error.append("\nCar brand is not valid. ");
        }

        if (car.getModelCar()==null || !car.getModelCar().matches("[a-zA-Z0-9 ]+")) {
            error.append("\nCar model is not valid. ");
        }

        if (car.getKilometrageCar() < 0) {
            error.append("\nKilometrage should be a positive number. ");
        }

        if (car.getYearCar() < 1996 || car.getYearCar() > 2025) {
            error.append("\nCar year is not valid. ");
        }

        if (car.getPriceCar() < 0.0) {
            error.append("\nCar price should be a positive number. ");
        }
        if(car.getImgCar()==null)
            error.append("Please upload an image for the car");
        return error.toString();
    }

    @FXML
    void clearElementsCar(ActionEvent event) {
        brandCar.setText(null);
        modelCar.setText(null);
        yearCar.setText(null);
        kilometrageCar.setText("0");
        priceCar.setText(null);
        menuButtonCar.setText("Select Warehouse");
    }
    @FXML
    void uploadImageCar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            imgCar.setText(selectedFile.toURI().toString().substring(6,selectedFile.toURI().toString().length()));
            loadImageFromPath(selectedFile.toURI().toString().substring(6,selectedFile.toURI().toString().length()));
        }
    }

    void loadImageFromPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            imageViewCar.setImage(new Image(file.toURI().toString()));
        } else {
            System.out.println("File not found: " + path);
        }
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

