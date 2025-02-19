package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Client;
import tn.esprit.entities.User;
import tn.esprit.services.UserServices;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class HomeController {

    private Client loggedInClient;

    public void setLoggedInClient(Client client) {
        this.loggedInClient = client;
        updateWelcomeLabel();
    }


    @FXML
    private Label welcomeLabel;

    private final UserServices userServices = new UserServices();


    private void updateWelcomeLabel() {
        if (loggedInClient != null) {
            String fullName = loggedInClient.getFirstNameUser() + " " + loggedInClient.getLastNameUser();
            welcomeLabel.setText("Welcome, " + fullName + "!");
        } else {
            welcomeLabel.setText("Welcome!");
        }
    }

    public void initialize() {
        updateWelcomeLabel();
    }

    @FXML
    private void handleConsultProfile(ActionEvent event) {
        try {
            // Load the profile FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
            Parent root = loader.load();

            // Pass the logged-in client to the profile controller
            ProfileController profileController = loader.getController();
            profileController.initialize(loggedInClient); // Pass the logged-in client

            // Create a new stage for the profile window
            Stage profileStage = new Stage();
            profileStage.setTitle("Profile");
            profileStage.setScene(new Scene(root));

            // Show the new stage (non-modal by default)
            profileStage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the profile page.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tn/esprit/ressources/login.fxml"));
            Parent root = loader.load();

            // Optionally, if you need to reset any login controller settings, do that here

            // Get the current stage and set the new scene
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the login page: " + e.getMessage());
        }
    }






    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRateUsClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listFeedbacks.fxml"));
            Parent root = loader.load();

            ListFeedbacksController listFeedbacksController = loader.getController();
            // Debug: Print the logged-in client's ID
            System.out.println("Passing Logged-in Client to ListFeedbacksController:");
            System.out.println("User ID: " + loggedInClient.getIdUser());
            listFeedbacksController.setLoggedInClient(loggedInClient);


            Stage stage = new Stage();
            stage.setTitle("Customer Feedbacks");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }















}
