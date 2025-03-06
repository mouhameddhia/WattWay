package tn.esprit.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONObject;
import tn.esprit.entities.Bill;
import tn.esprit.entities.Car;
import tn.esprit.services.BillServices;
import tn.esprit.services.CarServices;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Base64;

public class CarInterface {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/BrowseCar.fxml"));
    private int idCar;
    @FXML
    private Label brandCar;

    @FXML
    private Button buttonBuyCar;

    @FXML
    private Button buttonRentCar;

    @FXML
    private ImageView imgCar;

    @FXML
    private Label kilometrageCar;

    @FXML
    private Label modelCar;

    @FXML
    private Label priceCar;

    @FXML
    private Label yearCar;
    public void setData(Car car){
        if(car.getImgCar() != null) {
            File file = new File(car.getImgCar());
            imgCar.setImage(new Image(file.toURI().toString()));
        }
        brandCar.setText(car.getBrandCar());
        modelCar.setText(car.getModelCar());
        kilometrageCar.setText(Integer.toString(car.getKilometrageCar())+"Km");
        yearCar.setText(Integer.toString(car.getYearCar()));
        priceCar.setText(Float.toString(car.getPriceCar()));
        idCar = car.getIdCar();
    }
    @FXML
    void hideButtonsCarInterface(MouseEvent event) {
        buttonBuyCar.setVisible(false);
        buttonRentCar.setVisible(false);
    }

    @FXML
    void showButtonsCarInterface(MouseEvent event) {
        buttonBuyCar.setVisible(true);
        buttonRentCar.setVisible(true);
    }
    @FXML
    void addBillBuyOnClick(ActionEvent event) {

        BillServices bs = new BillServices();
        Bill bill = new Bill();
        float totalAmount;
        totalAmount = (float) ((Float.parseFloat(priceCar.getText()) * 1.08) + 150 + 500 + 300 + 200);
        bill.setTotalAmountBill(totalAmount);
        bill.setIdCar(idCar);

        try {
            bs.add(bill);

            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addBillRentOnClick(ActionEvent event) {
        CarServices cs = new CarServices();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RentBill.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            RentBill rentBill=loader.getController();
            rentBill.setData(idCar,Float.parseFloat(priceCar.getText()));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Rent car");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}


