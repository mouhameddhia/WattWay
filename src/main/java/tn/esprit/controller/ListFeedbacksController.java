package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Client;
import tn.esprit.entities.Feedback;
import tn.esprit.services.FeedbackServices;
import tn.esprit.services.UserServices;

import java.io.IOException;
import java.util.List;

public class ListFeedbacksController {

    @FXML
    private HBox feedbackContainer;

    private FeedbackServices feedbackService = new FeedbackServices();
    private List<Feedback> feedbackList;
    private int currentIndex = 0;

    private Client loggedInClient; // Store the logged-in client

    // Setter for the logged-in client
    public void setLoggedInClient(Client loggedInClient) {
        this.loggedInClient = loggedInClient;
        // Debug: Print the logged-in client's ID
        System.out.println("Logged-in Client Set in ListFeedbacksController:");
        System.out.println("User ID: " + loggedInClient.getIdUser());
    }

    UserServices userServices = new UserServices();



    @FXML
    private void initialize() {
        feedbackList = feedbackService.getAll();
        updateFeedbackView();
    }

    private void updateFeedbackView() {
        feedbackContainer.getChildren().clear();
        int end = Math.min(currentIndex + 3, feedbackList.size());

        for (int i = currentIndex; i < end; i++) {
            Feedback feedback = feedbackList.get(i);

            VBox feedbackBox = new VBox();
            feedbackBox.getChildren().add(new Label(feedback.getContentFeedback()));
            feedbackBox.getChildren().add(new Label("Rating: " + feedback.getRatingFeedback()));
            String clientName;
            if (loggedInClient != null && feedback.getIdUser() == loggedInClient.getIdUser()) {
                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(e -> deleteFeedback(feedback.getIdFeedback()));
                feedbackBox.getChildren().add(deleteButton);
            }

            feedbackContainer.getChildren().add(feedbackBox);
        }
    }

    @FXML
    private void prevFeedbacks() {
        if (currentIndex > 0) {
            currentIndex -= 3;
            updateFeedbackView();
        }
    }

    @FXML
    private void nextFeedbacks() {
        if (currentIndex + 3 < feedbackList.size()) {
            currentIndex += 3;
            updateFeedbackView();
        }
    }

    private void deleteFeedback(int feedbackId) {
        feedbackService.delete(feedbackId);
        feedbackList = feedbackService.getAll();
        updateFeedbackView();
    }

    public void refreshFeedbackList() {
        feedbackList = feedbackService.getAll(); // Reload the feedback list
        currentIndex = 0; // Reset to the first page
        updateFeedbackView(); // Update the view
    }



    @FXML
    private void handleGiveFeedback() {

        try {

            // Load the AddAdmin.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddFeedback.fxml"));
            Parent root = loader.load();

            // Get the controller
            //AddAdminController addAdminController = loader.getController();
            AddFeedbackController addFeedbackController = loader.getController();
            addFeedbackController.setLoggedInClient(loggedInClient);


            addFeedbackController.setListFeedbacksController(this);

            // Create and show the stage
            Stage stage = new Stage();
            stage.setTitle("Add Admin");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading AddAdmin.fxml: " + e.getMessage());
        }
    }








}