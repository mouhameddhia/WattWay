package tn.esprit.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.esprit.entities.Bill;
import tn.esprit.entities.Car;
import tn.esprit.services.BillServices;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class CarInterface {
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BrowseCar"));
        BillServices bs = new BillServices();
        Bill bill = new Bill();
        float totalAmount;
        totalAmount = (float) ((Float.parseFloat(priceCar.getText())*1.08)+150+500+300+200);
        bill.setTotalAmountBill(totalAmount);
        bill.setIdCar(idCar);
        try {
            bs.add(bill);
            loader.load();
            BrowseCar controller = loader.getController();
            controller.initialize();

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void addBillRentOnClick(ActionEvent event) {
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
