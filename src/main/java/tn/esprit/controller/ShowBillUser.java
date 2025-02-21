package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Bill;
import tn.esprit.services.BillServices;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ShowBillUser {
    @FXML
    private GridPane gridPaneBill;
    public void initialize() {
        BillServices bs = new BillServices();
        int column=1;
        int row=1;
        try {
            List<Bill> bills=bs.billsByUser(1);
            for(int i=0;i<bills.size();i++){
                System.out.println(bills.get(i));
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ShowBillUserInterface.fxml"));
                AnchorPane box = fxmlLoader.load();
                ShowBillUserInterface controller = fxmlLoader.getController();
                controller.setData(bills.get(i));
                row++;
                gridPaneBill.add(box,column,row);
                gridPaneBill.setMargin(box,new Insets(10));
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
