package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Car;
import tn.esprit.services.CarServices;
import tn.esprit.services.WarehouseServices;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;

public class RepairCarInterface {
    @FXML
    private Label labelThanks;
    @FXML
    private ImageView imageViewCar;
    @FXML
    private TextField brandCar;

    @FXML
    private TextField imgCar;

    @FXML
    private TextField kilometrageCar;

    @FXML
    private TextField modelCar;

    @FXML
    private TextField yearCar;

    @FXML
    void addCar(ActionEvent event) {
        WarehouseServices warehouseServices = new WarehouseServices();
        Car car = new Car();
        car.setImgCar(imgCar.getText());
        car.setBrandCar(brandCar.getText());
        car.setModelCar(modelCar.getText());
        if(yearCar.getText().length()==0)
            car.setYearCar(-1);
        else
            car.setYearCar(Integer.parseInt(yearCar.getText()));
        car.setPriceCar(0);

        if (kilometrageCar.getText().length()==0)
            car.setKilometrageCar(-1);
        else
            car.setKilometrageCar(Integer.parseInt(kilometrageCar.getText()));
        car.setIdWarehouse(1);
        if (!Objects.equals(verifCarDetails(car), "")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fatal error");
            alert.setHeaderText("Cannot add car");
            alert.setContentText(verifCarDetails(car));
            alert.showAndWait();
        }
        else{
            CarServices cs = new CarServices();
            car.setStatusCar("under repair");
            try {
                cs.add(car);
                labelThanks.setVisible(true);
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

        if(car.getImgCar()==null)
            error.append("Please upload an image for the car");
        return error.toString();
    }

    @FXML
    void uploadImgCar(ActionEvent event) {
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
}
