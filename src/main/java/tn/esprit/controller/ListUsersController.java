package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.entities.Admin;
import tn.esprit.entities.Client;
import tn.esprit.entities.User;
import tn.esprit.services.UserServices;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.util.Callback;

import java.io.IOException;


public class ListUsersController {

    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableView<Admin> adminTable;

    @FXML
    private TableColumn<Client, String> colClientFirstName;
    @FXML
    private TableColumn<Client, String> colClientLastName;
    @FXML
    private TableColumn<Client, String> colClientEmail;
    @FXML
    private TableColumn<Client, Integer> colClientId;
    @FXML
    private TableColumn<Client, String> colClientPhone;
    @FXML
    private TableColumn<Client, String> colClientPayment;
    @FXML
    private TableColumn<Client, String> colClientAddress;

    @FXML
    private TableColumn<Admin, Integer> colAdminId;
    @FXML
    private TableColumn<Admin, String> colAdminEmail;
    @FXML
    private TableColumn<Admin, String> colAdminFirstName;
    @FXML
    private TableColumn<Admin, String> colAdminLastName;
    @FXML
    private TableColumn<Admin, String> colAdminRole;

    @FXML
    private TableColumn<Admin, String> colAdminDelete;
    @FXML
    private TableColumn<Client, String> colClientDelete;



    @FXML
    public void initialize() {

        /// This method is automatically called when the FXML file is loaded.
        ///
        /// UserServices is used to fetch data from the database.
        ///
        /// ObservableList is a special list that updates the UI when its contents change.

        UserServices userServices = new UserServices();

        ObservableList<Client> clientList = FXCollections.observableArrayList();
        ObservableList<Admin> adminList = FXCollections.observableArrayList();



///Loops through all users fetched from the database.
//
///Checks if the user is a Client or Admin and adds them to the appropriate list.
        // Populate the lists with users
        for (User user : userServices.getAll()) {
            if (user instanceof Client) {
                clientList.add((Client) user);
            } else if (user instanceof Admin) {
                adminList.add((Admin) user);
            }
        }

        /// Links the ObservableList to the TableView so the data is displayed.
        // Set items in TableView
        clientTable.setItems(clientList);
        adminTable.setItems(adminList);


        /// Links each column to a specific attribute of the Client or Admin class using PropertyValueFactory.
        // Set up columns for Client table
        colClientId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colClientEmail.setCellValueFactory(new PropertyValueFactory<>("emailUser"));
        colClientFirstName.setCellValueFactory(new PropertyValueFactory<>("firstNameUser"));
        colClientLastName.setCellValueFactory(new PropertyValueFactory<>("lastNameUser"));
        colClientPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumberClient"));
        colClientPayment.setCellValueFactory(new PropertyValueFactory<>("paymentDetailsClient"));
        colClientAddress.setCellValueFactory(new PropertyValueFactory<>("addressClient"));

        // Set up columns for Admin table
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colAdminEmail.setCellValueFactory(new PropertyValueFactory<>("emailUser"));
        colAdminFirstName.setCellValueFactory(new PropertyValueFactory<>("firstNameUser"));
        colAdminLastName.setCellValueFactory(new PropertyValueFactory<>("lastNameUser"));
        colAdminRole.setCellValueFactory(new PropertyValueFactory<>("roleUser"));




        /// Adds a "Delete" button to each row in the TableView.
        ///
        /// When the button is clicked, it calls the handleDeleteClient or handleDeleteAdmin method.


        colClientDelete.setCellFactory(param -> {
            TableCell<Client, String> cell = new TableCell<Client, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Button deleteButton = new Button("Delete");
                        deleteButton.setOnAction(event -> handleDeleteClient(getTableRow().getItem()));
                        setGraphic(deleteButton);
                    }
                }
            };
            return cell;
        });




        // Add a delete button to each row of the Admin table
        colAdminDelete.setCellFactory(param -> {
            TableCell<Admin, String> cell = new TableCell<Admin, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Button deleteButton = new Button("Delete");
                        deleteButton.setOnAction(event -> handleDeleteAdmin(getTableRow().getItem()));
                        setGraphic(deleteButton);
                    }
                }
            };
            return cell;
        });
    }




   /// Deletes a Client or Admin from the database and removes them from the TableView.

    // Method to handle the deletion of a Client
    private void handleDeleteClient(Client client) {
        if (client != null) {
            // Call the delete function with the client's ID
            UserServices userServices = new UserServices();
            userServices.delete(client.getIdUser()); // Assuming getIdUser() returns the ID of the client

            // Remove the client from the TableView
            clientTable.getItems().remove(client);

            System.out.println("Successfully deleted Client: " + client.getEmailUser());
        }
    }

    // Method to handle the deletion of an Admin
    private void handleDeleteAdmin(Admin admin) {
        if (admin != null) {
            // Call the delete function with the admin's ID
            UserServices userServices = new UserServices();
            userServices.delete(admin.getIdUser()); // Assuming getIdUser() returns the ID of the admin

            // Remove the admin from the TableView
            adminTable.getItems().remove(admin);

            System.out.println("Successfully deleted Admin: " + admin.getEmailUser());
        }
    }






    /// Opens the AddAdminController window when the "Add Admin" button is clicked.
    ///
    /// Passes the ListUsersController instance to AddAdminController.

    public void handleAddAdmin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddAdmin.fxml"));
            Parent root = loader.load();

            // Pass the ListUsersController instance to AddAdminController
            AddAdminController addAdminController = loader.getController();
            addAdminController.setListUsersController(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add Admin");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load the add admin page: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Method to refresh the admin table
    public void refreshAdminTable() {
        UserServices userServices = new UserServices();
        ObservableList<Admin> adminList = FXCollections.observableArrayList();

        // Repopulate the admin list
        for (User user : userServices.getAll()) {
            if (user instanceof Admin) {
                adminList.add((Admin) user);
            }
        }

        // Update the TableView
        adminTable.setItems(adminList);
    }








}
