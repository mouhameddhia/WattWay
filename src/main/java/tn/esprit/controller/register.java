package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Client;
import tn.esprit.services.UserServices;

import java.io.IOException;

public class register {

    @FXML
    private TextField addressField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstnameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> paymentComboBox;

    @FXML
    private TextField phoneField;

    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;

    @FXML
    private void initialize() {
        // Set action for register button
        registerButton.setOnAction(event -> registerUser());

        // Set action for login button
        loginButton.setOnAction(this::loadLoginPage);

        // Add payment methods to ComboBox
        paymentComboBox.getItems().addAll("Credit Card", "PayPal", "Bank Transfer");
    }

    private void registerUser() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String firstName = firstnameField.getText();
        String lastName = lastnameField.getText();
        String phone = phoneField.getText();
        String payment = paymentComboBox.getValue();
        String address = addressField.getText();

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || payment == null || address.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        Client.PaymentDetails paymentDetails;
        try {
            paymentDetails = Client.PaymentDetails.valueOf(payment.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid payment method selected.");
            return;
        }

        Client client = new Client();
        client.setEmailUser(email);
        client.setPasswordUser(password);
        client.setFirstNameUser(firstName);
        client.setLastNameUser(lastName);
        client.setPhoneNumberClient(phone);
        client.setPaymentDetailsClient(paymentDetails);
        client.setAddressClient(address);

        UserServices userServices = new UserServices();
        userServices.add(client);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
        clearFields();
    }

    private void loadLoginPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Login page.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        emailField.clear();
        passwordField.clear();
        firstnameField.clear();
        lastnameField.clear();
        phoneField.clear();
        paymentComboBox.getSelectionModel().clearSelection();
        addressField.clear();
    }
}
