package tn.esprit.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import tn.esprit.entities.Response;
import tn.esprit.entities.Submission;
import tn.esprit.services.SubmissionServices;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.io.InputStream;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;

public class ListSubmissionController {

    @FXML
    private TextField userIdField;

    @FXML
    private FlowPane submissionsContainer;

    @FXML
    private Button loadSubmissionsButton;

    @FXML
    private Button addSubmissionButton;

    @FXML
    private Pane carAnimationPane;

    private ImageView car;

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
    public void initialize() {
        // Load the car image
        InputStream imageStream = getClass().getResourceAsStream("/car.png");

        if (imageStream == null) {
            System.err.println("Error: Car image not found at path: /car.png");
            throw new IllegalArgumentException("Car image not found");
        }

        Image carImage = new Image(imageStream);
        car = new ImageView(carImage);
        car.setFitWidth(150); // Make the car bigger
        car.setFitHeight(75); // Adjust height accordingly
        car.setLayoutY(carAnimationPane.getHeight() - 100); // Start higher up
        car.setVisible(true);

        // Add the car to the animation pane
        carAnimationPane.getChildren().add(car);

        // Wait for the pane to be properly sized
        carAnimationPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Wait for the scene to be fully laid out
                Platform.runLater(() -> {
                    System.out.println("Pane size after layout: " +
                            carAnimationPane.getWidth() + "x" + carAnimationPane.getHeight());
                    startCarAnimation();
                });
            }
        });
    }

    private void startCarAnimation() {
        if (car != null && carAnimationPane.getWidth() > 0) {
            // Position the car initially off-screen to the left and higher up
            car.setTranslateX(-car.getFitWidth());
            car.setTranslateY(carAnimationPane.getHeight() - 100); // Start higher up

            // Create a TranslateTransition for the car
            TranslateTransition transition = new TranslateTransition(Duration.seconds(5), car);
            transition.setFromX(-car.getFitWidth()); // Start position (off-screen left)
            transition.setToX(carAnimationPane.getWidth()); // End position (off-screen right)
            transition.setFromY(carAnimationPane.getHeight() - 100); // Start higher up
            transition.setToY(50); // Move up to 50 pixels from the top
            transition.setCycleCount(TranslateTransition.INDEFINITE); // Make it continuous
            transition.setAutoReverse(false);

            // Create a ScaleTransition to make the car grow slightly
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(5), car);
            scaleTransition.setFromX(1.0); // Start size
            scaleTransition.setToX(1.1); // Grow to 110%
            scaleTransition.setFromY(1.0); // Start size
            scaleTransition.setToY(1.1); // Grow to 110%

            // Play both transitions together
            transition.play();
            scaleTransition.play();

            System.out.println("Starting animation from " + (-car.getFitWidth()) +
                    " to " + carAnimationPane.getWidth());
        } else {
            System.err.println("Cannot start animation - car is null or pane has no width");
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
        updateButton.getStyleClass().add("button-update");
        updateButton.setOnAction(event -> handleUpdateSubmission(submission));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("button-delete");
        deleteButton.setOnAction(event -> handleDeleteSubmission(submission));

        buttonBox.getChildren().addAll(updateButton, deleteButton);

        // Add responses section
        VBox responsesContainer = createResponsesSection(submission);

        // Add all elements to the card
        card.getChildren().addAll(urgencyIndicator, idLabel, descriptionLabel, statusLabel, dateLabel, buttonBox, responsesContainer);

        // Add hover effect
        card.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });

        card.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });

        // Add fade-in animation when card is created
        FadeTransition ft = new FadeTransition(Duration.millis(1000), card);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

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