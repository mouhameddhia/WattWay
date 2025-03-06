package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tn.esprit.entities.Bill;
import tn.esprit.services.BillServices;

import java.sql.SQLException;

public class RentBill {
    @FXML
    private Label exitMessage;
    @FXML
    private TextField daysRentCar;
    @FXML
    private TextField totalAmountBill;
    float priceCar;
    int idCar;
    public void initialize(){
        daysRentCar.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int days = Integer.parseInt(newValue);
                if(days > 0){
                    totalAmountBill.setText(String.format("%.2f", priceCar * 0.00025 * days * 1.02));
                }
            } catch (NumberFormatException e) {
                totalAmountBill.setText("0.00");
            }
        });

    }
    void setData(int idCar, float priceCar){
        this.idCar = idCar;
        this.priceCar=priceCar;
    }
    @FXML
    void addBillOnClick(ActionEvent event) {
        BillServices bs = new BillServices();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Bill bill = new Bill();
        if(daysRentCar.getText().length() > 0 && daysRentCar.getText().matches("\\d+") && Integer.parseInt(daysRentCar.getText()) > 0)
        {
            bill.setTotalAmountBill(Float.parseFloat(totalAmountBill.getText()));
            bill.setIdCar(idCar);
            try {
                bs.add(bill);
                exitMessage.setVisible(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            alert.setTitle("Error");
            alert.setHeaderText("Cannot proceed");
            alert.setContentText("You must provide a valid number of days");
            alert.showAndWait();
        }
    }



}
