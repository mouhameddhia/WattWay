package tn.esprit.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.services.CarServices;

public class MainFx extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        CarServices cs = new CarServices();
        Parent root= FXMLLoader.load(getClass().getResource("/AddWarehouse.fxml"));
        Scene scene=new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("Add Warehouse");
    }
    public static void main(String[] args) {launch(args);}
}
