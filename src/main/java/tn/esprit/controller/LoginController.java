package tn.esprit.controller;

import tn.esprit.entities.User;
import tn.esprit.entities.Client;
import tn.esprit.entities.Admin;
import tn.esprit.services.UserServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private final UserServices userServices = new UserServices();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in both email and password fields.");
            return;
        }

        User user = userServices.login(email, password);
        if (user != null) {
            redirectToDashboard(user, event);
        } else {
            showAlert("Login Failed", "Invalid email or password.");
        }
    }

    private void redirectToDashboard(User user, ActionEvent event) {
        String fxmlFile = user instanceof Admin ? "/listUsers.fxml" : "/home.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // If the user is a client, set the logged-in client in HomeController
            if (user instanceof Client) {
                HomeController homeController = loader.getController();
                homeController.setLoggedInClient((Client) user); // Cast user to Client
            }

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the dashboard.");
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
