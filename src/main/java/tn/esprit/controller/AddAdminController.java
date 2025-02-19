package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import tn.esprit.entities.Admin;
import tn.esprit.entities.User;
import tn.esprit.services.UserServices;
import tn.esprit.entities.Admin.FunctionAdmin;

public class AddAdminController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ComboBox<FunctionAdmin> functionComboBox;

    private ListUsersController listUsersController; // Reference to ListUsersController



    // Setter for ListUsersController
    public void setListUsersController(ListUsersController listUsersController) {
        this.listUsersController = listUsersController;
    }




    /// Called when the FXML file is loaded.
///
/// Calls populateFunctionComboBox to fill the ComboBox with FunctionAdmin values.
    @FXML
    private void initialize() {
        populateFunctionComboBox();
    }

    /// Fills the ComboBox with values from the FunctionAdmin enum.
    private void populateFunctionComboBox() {
        ObservableList<FunctionAdmin> functions = FXCollections.observableArrayList(FunctionAdmin.values());
        functionComboBox.setItems(functions);
        if (!functions.isEmpty()) {
            functionComboBox.setValue(functions.get(0)); // Set default value
        }
    }



    ///Saves a new admin to the database and refreshes the TableView in ListUsersController.
    @FXML
    private void handleSaveAdmin() {
        try {
            // Retrieve values from fields
            String email = emailField.getText();
            String password = passwordField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            FunctionAdmin function = functionComboBox.getValue();

            // Validate fields
            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || function == null) {
                showAlert("Error", "All fields are required.");
                return;
            }

            // Create a new Admin instance.
            Admin admin = new Admin(email, password, User.Role.ADMIN, firstName, lastName, function);

            // Save the admin using UserServices
            UserServices userServices = new UserServices();
            userServices.add(admin);  // Make sure this method is implemented in UserServices

            // Refresh the admin table in ListUsersController
            if (listUsersController != null) {
                listUsersController.refreshAdminTable();
            }

            showAlert("Success", "Admin added successfully!");
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add admin: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
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
