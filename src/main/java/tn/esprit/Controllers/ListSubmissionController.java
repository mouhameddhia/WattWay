package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import tn.esprit.entities.Response;
import tn.esprit.entities.Submission;
import tn.esprit.services.SubmissionServices;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListSubmissionController {

    @FXML
    private TextField userIdField;

    @FXML
    private FlowPane submissionsContainer;

    private final SubmissionServices submissionServices = new SubmissionServices();
    @FXML
    private void handleAddSubmission() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddSubmissionInterface.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Submission");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the add submission interface.");
        }
    }


    @FXML
    private void handleLoadSubmissions() {
        submissionsContainer.getChildren().clear();
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            List<Submission> submissions = submissionServices.getSubmissionsByUserId(userId);

            if (submissions.isEmpty()) {
                showAlert("Info", "No submissions found for this user.");
            } else {
                displaySubmissionsAsCards(submissions);
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid User ID.");
        } catch (SQLException e) {
            showAlert("Error", "Failed to load submissions: " + e.getMessage());
        }
    }

    private void displaySubmissionsAsCards(List<Submission> submissions) {
        submissionsContainer.getChildren().clear();
        for (Submission submission : submissions) {
            try {
                // Fetch responses for each submission
                submission.setResponses(submissionServices.getResponsesBySubmissionId(submission.getIdSubmission()));
                submissionsContainer.getChildren().add(createSubmissionCard(submission));
            } catch (SQLException e) {
                showAlert("Error", "Failed to load responses for submission " + submission.getIdSubmission());
            }
        }
        submissionsContainer.requestLayout();
    }

    private VBox createSubmissionCard(Submission submission) {
        VBox card = new VBox(10);
        card.getStyleClass().add("submission-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");

        Rectangle urgencyIndicator = new Rectangle(300, 5);
        urgencyIndicator.setFill(getUrgencyColor(submission.getUrgencyLevel()));

        Label idLabel = new Label("ID: " + submission.getIdSubmission());
        Label descriptionLabel = new Label("Description: " + submission.getDescription());
        Label statusLabel = new Label("Status: " + submission.getStatus());
        Label dateLabel = new Label("Date: " + submission.getDateSubmission());

        styleLabel(idLabel, true);
        styleLabel(descriptionLabel, false);
        styleLabel(statusLabel, false);
        styleLabel(dateLabel, false);

        // Add Update and Delete buttons
        HBox buttonBox = new HBox(10);
        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("button-primary");
        updateButton.setOnAction(event -> handleUpdateSubmission(submission));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("button-delete");
        deleteButton.setOnAction(event -> handleDeleteSubmission(submission));

        buttonBox.getChildren().addAll(updateButton, deleteButton);

        // Add responses section
        VBox responsesContainer = createResponsesSection(submission);

        // Add all elements to the card
        card.getChildren().addAll(urgencyIndicator, idLabel, descriptionLabel, statusLabel, dateLabel, buttonBox, responsesContainer);
        return card;
    }

    private VBox createResponsesSection(Submission submission) {
        VBox responsesContainer = new VBox(8);
        responsesContainer.setStyle("-fx-padding: 0 0 0 10;");

        Label responsesTitle = new Label("Responses:");
        responsesTitle.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");
        responsesContainer.getChildren().add(responsesTitle);

        if (submission.getResponses() != null && !submission.getResponses().isEmpty()) {
            submission.getResponses().forEach(response -> responsesContainer.getChildren().add(createResponseCard(response)));
        } else {
            Label noResponseLabel = new Label("No responses available");
            noResponseLabel.setStyle("-fx-font-style: italic;");
            responsesContainer.getChildren().add(noResponseLabel);
        }
        return responsesContainer;
    }

    private VBox createResponseCard(Response response) {
        VBox responseCard = new VBox(5);
        responseCard.getStyleClass().add("response-card");
        responseCard.setPadding(new Insets(10));
        responseCard.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-margin: 5;");
        responseCard.setMaxWidth(280);

        Label messageLabel = new Label("Response: " + response.getMessage());
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;"); // Changed text color to black

        HBox detailsBox = new HBox(10);
        Label dateLabel = new Label(response.getDateResponse() != null ?
                "Date: " + response.getDateResponse() : "No date");
        Label typeLabel = new Label(response.getTypeResponse() != null ?
                "Type: " + response.getTypeResponse() : "No type");
        styleLabel(dateLabel, false);
        styleLabel(typeLabel, false);

        detailsBox.getChildren().addAll(dateLabel, typeLabel);
        responseCard.getChildren().addAll(messageLabel, detailsBox);
        return responseCard;
    }

    private Color getUrgencyColor(Submission.URGENCYLEVEL urgencyLevel) {
        return switch (urgencyLevel) {
            case LOW -> Color.GREEN;
            case MEDIUM -> Color.ORANGE;
            case HIGH -> Color.RED;
        };
    }

    private void styleLabel(Label label, boolean isBold) {
        label.setStyle((isBold ? "-fx-font-weight: bold; " : "") + "-fx-text-fill: black;");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleUpdateSubmission(Submission submission) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateSubmissionInterface.fxml"));
            Parent root = loader.load();

            UpdateSubmissionController controller = loader.getController();
            controller.setSubmissionId(submission.getIdSubmission());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the update interface.");
        }
    }

    private void handleDeleteSubmission(Submission submission) {
        submissionServices.delete(submission);
        showAlert("Success", "Submission deleted successfully.");
        handleLoadSubmissions(); // Refresh the list after deletion
    }


}