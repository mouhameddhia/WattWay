package tn.esprit.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ListMechanicInterface.fxml"))); // Ensure correct path

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Mechanic Management");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
