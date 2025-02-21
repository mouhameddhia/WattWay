package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import tn.esprit.entities.Bill;
import tn.esprit.entities.Car;
import tn.esprit.services.BillServices;
import tn.esprit.services.CarServices;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class ShowBillUserInterface {

    @FXML
    private Label brandCar;
    @FXML
    private Label dateBill;
    @FXML
    private Label idBill;
    @FXML
    private Label kilometrageCar;
    @FXML
    private Label modelCar;
    @FXML
    private Label totalAmountBill;
    @FXML
    private Label yearCar;
    @FXML
    private Label exitMessage;

    @FXML
    void payBill(ActionEvent event) {
        BillServices bs = new BillServices();
        Car car;
        CarServices cs = new CarServices();
        try {
            bs.payBill(Integer.parseInt(idBill.getText()));
            car=bs.getCarByBillId(Integer.parseInt(idBill.getText()));
            cs.updateStatusCar("not available",car.getIdCar());
            exitMessage.setVisible(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setData(Bill bill){
        Car car= new Car();
        BillServices bs = new BillServices();
        try {
            car=bs.getCarByBillId(bill.getIdBill());
            idBill.setText(Integer.toString(bill.getIdBill()));
            dateBill.setText(bill.getDateBill().toString());
            totalAmountBill.setText(Float.toString(bill.getTotalAmountBill()));
            brandCar.setText(car.getBrandCar());
            modelCar.setText(car.getModelCar());
            yearCar.setText(Integer.toString(car.getYearCar()));
            kilometrageCar.setText(Integer.toString(car.getKilometrageCar()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
