package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import tn.esprit.entities.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import tn.esprit.services.UserServices;

import java.io.IOException;

public class ProfileController {

    @FXML
    private Label emailLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private Label paymentDetailsLabel;
    @FXML
    private Label addressLabel;

    private Client client;

    public void initialize(Client client) {
        this.client = client;
        populateProfile();
    }

    private void populateProfile() {
        emailLabel.setText(client.getEmailUser());
        firstNameLabel.setText(client.getFirstNameUser());
        lastNameLabel.setText(client.getLastNameUser());
        phoneNumberLabel.setText(client.getPhoneNumberClient());
        paymentDetailsLabel.setText(client.getPaymentDetailsClient().name()); // Enum value as string
        addressLabel.setText(client.getAddressClient());
    }

    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profileUpdate.fxml"));
            Parent root = loader.load();

            ProfileUpdateController updateController = loader.getController();
            updateController.setClient(client);


            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Profile");
            stage.showAndWait();

            // Refresh profile from the database after update
            refreshProfileFromDatabase();
        } catch (IOException e) {
            showAlert("Error", "Failed to load the profile update page: " + e.getMessage());
        }
    }

    private void refreshProfileFromDatabase() {
        // Fetch the updated client data from the database
        UserServices userServices = new UserServices();
        Client updatedClient = (Client) userServices.getById(client.getIdUser()); // Assuming you have a method to fetch a client by ID
        if (updatedClient != null) {
            this.client = updatedClient;
            populateProfile(); // Repopulate the UI with the updated data
        }
    }

    @FXML
    private void handleDeleteProfile(ActionEvent event) {
        try {
            // Delete the account from the database using UserServices
            UserServices userServices = new UserServices();
            userServices.delete(client.getIdUser()); // Assumes delete(int idUser) is implemented in UserServices

            showAlert("Success", "Account deleted successfully!");

            // Redirect to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the login page: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete account: " + e.getMessage());
        }
    }













    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}