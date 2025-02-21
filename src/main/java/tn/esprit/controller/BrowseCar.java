package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Car;
import tn.esprit.services.BillServices;
import tn.esprit.services.CarServices;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BrowseCar {
    int nbBills;
    @FXML
    private Label numberOfBills;
    @FXML
    private Button repairCarButton;
    @FXML
    private Button finalizeDealsButton;
    @FXML
    private GridPane gridCar;
    public void initialize(){
        BillServices bs = new BillServices();
        try {
            nbBills=bs.numberBillsByUser(1);
            numberOfBills.setText(String.valueOf(nbBills));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(nbBills>0){
            finalizeDealsButton.setDisable(false);
        }
        CarServices cs = new CarServices();
        int column=0;
        int row=1;
        try {
            List<Car> cars = cs.retrieveAvailable();
            for (Car car : cars) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CarInterface.fxml"));
                VBox box = fxmlLoader.load();
                CarInterface carInterface = fxmlLoader.getController();
                carInterface.setData(car);

                gridCar.add(box, column++, row);
                GridPane.setMargin(box, new Insets(10));

                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }
    @FXML
    void openRepairCarInterface(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RepairCarInterface.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Repair Car Interface");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void showUserBills(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowBillUser.fxml"));
        Parent root = null;
        try {
            root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("List of Bills");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
