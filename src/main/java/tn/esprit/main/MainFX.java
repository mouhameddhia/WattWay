package tn.esprit.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Show choice dialog
        String choice = showChoiceDialog();

        String fxmlFile = null;
        if ("Mechanics".equals(choice)) {
            fxmlFile = "/ListMechanicInterface.fxml";
        } else if ("Assignments".equals(choice)) {
            fxmlFile = "/ListAssignmentInterface.fxml";
        }

        if (fxmlFile != null) {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Application");
            primaryStage.show();
        } else {
            System.out.println("No valid choice selected. Exiting...");
            System.exit(0);
        }
    }

    private String showChoiceDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choose View");
        alert.setHeaderText("Select the list you want to view:");
        alert.setContentText("Choose an option:");

        ButtonType buttonMechanics = new ButtonType("Mechanics");
        ButtonType buttonAssignments = new ButtonType("Assignments");
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(buttonMechanics, buttonAssignments, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonMechanics) {
                return "Mechanics";
            } else if (result.get() == buttonAssignments) {
                return "Assignments";
            }
        }
        return null; // Exit if no choice is made
    }

    public static void main(String[] args) {
        launch(args);
    }
}
