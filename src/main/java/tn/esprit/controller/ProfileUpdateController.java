package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import tn.esprit.entities.Client;
import tn.esprit.services.UserServices;

public class ProfileUpdateController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private ComboBox<Client.PaymentDetails> paymentDetailsComboBox; // Changed from TextField to ComboBox
    @FXML
    private TextField addressField;

    private Client client;

    // Use this method to pass the Client object after FXML is loaded
    public void setClient(Client client) {
        this.client = client;
        populateFields();
        populatePaymentDetails();
    }

    // Populate the fields with the existing client data
    private void populateFields() {
        emailField.setText(client.getEmailUser());
        firstNameField.setText(client.getFirstNameUser());
        lastNameField.setText(client.getLastNameUser());
        phoneNumberField.setText(client.getPhoneNumberClient());
        paymentDetailsComboBox.setValue(client.getPaymentDetailsClient()); // Set the current enum value
        addressField.setText(client.getAddressClient());
    }

    // Populate the ComboBox with enum values
    private void populatePaymentDetails() {
        ObservableList<Client.PaymentDetails> detailsList = FXCollections.observableArrayList(
                Client.PaymentDetails.PAYPAL,
                Client.PaymentDetails.CREDIT_CARD,
                Client.PaymentDetails.BANK_TRANSFER
        );
        paymentDetailsComboBox.setItems(detailsList);
    }


    @FXML
    private void handleSaveProfile() {
        try {
            // Update the client object with new values from the fields
            client.setEmailUser(emailField.getText());
            client.setFirstNameUser(firstNameField.getText());
            client.setLastNameUser(lastNameField.getText());
            client.setPhoneNumberClient(phoneNumberField.getText());
            client.setPaymentDetailsClient(paymentDetailsComboBox.getValue());
            client.setAddressClient(addressField.getText());

            // Save the updated client to the database using your updateAccount method
            UserServices userServices = new UserServices();
            boolean isUpdated = userServices.updateAccount(client.getIdUser(), client);

            if (isUpdated) {
                showAlert("Success", "Profile updated successfully!");
            } else {
                showAlert("Error", "Failed to update profile.");
            }

            // Close the update window
          closeWindow();
        } catch (Exception e) {
            showAlert("Error", "Failed to update profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        // Get the current stage and close it
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
