package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entities.Client;
import tn.esprit.entities.Feedback;
import tn.esprit.services.FeedbackServices;

public class AddFeedbackController {

    @FXML
    private TextField contentField;

    @FXML
    private ComboBox<Integer> ratingComboBox;

    private ListFeedbacksController listFeedbacksController;

    public void setListFeedbacksController(ListFeedbacksController listFeedbacksController) {
        this.listFeedbacksController = listFeedbacksController;
    }



    private Client loggedInClient; // Store the logged-in client

    // Setter for the logged-in client
    public void setLoggedInClient(Client loggedInClient) {
        this.loggedInClient = loggedInClient;
        // Debug: Print the logged-in client's ID
        System.out.println("Logged-in Client Set in AddFeedbackController:");
        System.out.println("User ID: " + loggedInClient.getIdUser());

    }



    @FXML
    private void initialize() {
        ratingComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingComboBox.setValue(5);
    }

    @FXML
    private void handleSubmit() {
        String content = contentField.getText();
        Integer rating = ratingComboBox.getValue();

        if (content.isEmpty() || rating == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        System.out.println("Logged-in User ID: " + loggedInClient.getIdUser());

        Feedback feedback = new Feedback(content, rating,null, loggedInClient.getIdUser());
        // Debug: Print the Feedback object details
        System.out.println("Feedback Object Created:");
        System.out.println("Content: " + feedback.getContentFeedback());
        System.out.println("Rating: " + feedback.getRatingFeedback());
        System.out.println("User ID: " + feedback.getIdUser());
        FeedbackServices service = new FeedbackServices();
        service.add(feedback);

        showAlert("Success", "Feedback submitted!");

        // Refresh the feedback list in the ListFeedbacksController
        if (listFeedbacksController != null) {
            listFeedbacksController.refreshFeedbackList();
        }

        closeWindow();
    }
    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) contentField.getScene().getWindow();
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
