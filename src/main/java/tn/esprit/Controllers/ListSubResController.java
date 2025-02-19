package tn.esprit.Controllers;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Submission;
import tn.esprit.entities.Response;
import tn.esprit.services.SubmissionServices;
import tn.esprit.services.ResponseServices;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListSubResController {

    @FXML private Text totalSubmissions;
    @FXML private Text scrolling_text;
    @FXML private Pane news_pane;

    // Submission Table Components
    @FXML private TableView<Submission> submission_table;
    @FXML private TableColumn<Submission, Integer> idSubmissionColumn;
    @FXML private TableColumn<Submission, String> descriptionColumn;
    @FXML private TableColumn<Submission, Submission.STATUS> statusColumn;
    @FXML private TableColumn<Submission, Submission.URGENCYLEVEL> urgencyLevelColumn;
    @FXML private TableColumn<Submission, String> dateSubmissionColumn;
    @FXML private TableColumn<Submission, Integer> idCarColumn;
    @FXML private TableColumn<Submission, Integer> idUserColumn;
    @FXML private TableColumn<Submission, Void> deleteSubmissionColumn;
    @FXML private TableColumn<Submission, Void> addResponseColumn;

    // Response Table Components
    @FXML private TableView<Response> response_table;
    @FXML private TableColumn<Response, Integer> idResponseColumn;
    @FXML private TableColumn<Response, String> messageColumn;
    @FXML private TableColumn<Response, String> dateResponseColumn;
    @FXML private TableColumn<Response, String> typeResponseColumn;
    @FXML private TableColumn<Response, Void> actionsColumn;

    private SubmissionServices submissionService;
    private ResponseServices responseService;

    @FXML
    void initialize() {
        submissionService = new SubmissionServices();
        responseService = new ResponseServices();
        //urgencyLevelid.setVisible(false);


        initializeColumns();
        loadData();
        setupScrollingText();
        setupSubmissionColumns();
    }

    private void initializeColumns() {
        // Setup Submission Columns
        idSubmissionColumn.setCellValueFactory(new PropertyValueFactory<>("idSubmission"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        urgencyLevelColumn.setCellValueFactory(new PropertyValueFactory<>("urgencyLevel"));
        dateSubmissionColumn.setCellValueFactory(new PropertyValueFactory<>("dateSubmission"));
        idCarColumn.setCellValueFactory(new PropertyValueFactory<>("idCar"));
        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));

        // Required for Void columns
        addResponseColumn.setCellValueFactory(param -> new SimpleObjectProperty<>());
        deleteSubmissionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>());

        // Setup Response Columns
        idResponseColumn.setCellValueFactory(new PropertyValueFactory<>("idResponse"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        dateResponseColumn.setCellValueFactory(new PropertyValueFactory<>("dateResponse"));
        typeResponseColumn.setCellValueFactory(new PropertyValueFactory<>("typeResponse"));

        // Setup Action Columns
        setupAddResponseColumn();  // Updated method for Add Response button
        setupDeleteSubmissionColumn();
        setupResponseActionColumn();

        // Add selection listener
        submission_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                highlightRelatedResponses(newSelection);
            }
        });
    }

    private void setupAddResponseColumn() {
        addResponseColumn.setCellFactory(param -> new TableCell<>() {
            private final Button addButton = new Button("Add Response");

            {
                addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                addButton.setOnAction(event -> {
                    Submission submission = getTableView().getItems().get(getIndex());
                    if (submission != null) {
                        try {
                            // Load the Add Response form in a dialog
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddResponseInterface.fxml"));
                            Parent root = loader.load();

                            // Get the controller and pass the submission ID
                            AddResponseController controller = loader.getController();
                            controller.setSubmissionId(submission.getIdSubmission());

                            // Create dialog stage
                            Stage dialogStage = new Stage();
                            dialogStage.setTitle("Add New Response");
                            dialogStage.initModality(Modality.APPLICATION_MODAL);
                            dialogStage.setScene(new Scene(root));

                            // Set owner to main window
                            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());

                            // Refresh data after dialog closes
                            dialogStage.showAndWait();
                            loadData(); // Refresh the main table

                        } catch (IOException e) {
                            showAlert("Error", "Failed to load response form: " + e.getMessage());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addButton);
            }
        });
    }

    private void highlightRelatedResponses(Submission selectedSubmission) {
        try {
            List<Response> allResponses = responseService.returnList();
            ObservableList<Response> relatedResponses = FXCollections.observableArrayList(
                    allResponses.stream()
                            .filter(response -> response.getIdSubmission() == selectedSubmission.getIdSubmission())
                            .toList()
            );

            response_table.setItems(relatedResponses);

            if (relatedResponses.isEmpty()) {
                response_table.setPlaceholder(new Label("No responses for this submission"));
            }

            // Animation Logic
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), response_table);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.5);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), response_table);
            fadeIn.setFromValue(0.5);
            fadeIn.setToValue(1.0);

            SequentialTransition sequence = new SequentialTransition(fadeOut, fadeIn);
            sequence.play();

            // Row Highlighting
            submission_table.setRowFactory(tv -> new TableRow<Submission>() {
                @Override
                protected void updateItem(Submission submission, boolean empty) {
                    super.updateItem(submission, empty);
                    if (submission == null || empty) {
                        setStyle("");
                    } else if (submission.equals(selectedSubmission)) {
                        setStyle("-fx-background-color: #e8f5fe;");
                    } else {
                        setStyle("");
                    }
                }
            });

        } catch (SQLException e) {
            showAlert("Error", "Failed to load responses: " + e.getMessage());
        }
    }

    private void setupDeleteSubmissionColumn() {
        deleteSubmissionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Submission submission = getTableView().getItems().get(getIndex());
                    submissionService.delete(submission);
                    loadData();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
    }

    private void setupResponseActionColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final HBox actionButtons = new HBox(5);
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                actionButtons.getChildren().addAll(updateButton, deleteButton);

                updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                updateButton.setOnAction(event -> {
                    Response response = getTableView().getItems().get(getIndex());
                    showResponseUpdateDialog(response);
                });

                deleteButton.setOnAction(event -> {
                    Response response = getTableView().getItems().get(getIndex());
                    responseService.delete(response);
                    loadData();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionButtons);
            }
        });
    }

    private void showResponseUpdateDialog(Response response) {
        Dialog<Response> dialog = new Dialog<>();
        dialog.setTitle("Update Response");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create fields for all response properties
        TextField messageField = new TextField(response.getMessage());
        ComboBox<String> typeResponseBox = new ComboBox<>();
        typeResponseBox.getItems().addAll("ACKNOWLEDGMENT"," RESOLUTION", "CLARIFICATIONREQUEST"); // Add your response types
        typeResponseBox.setValue(String.valueOf(response.getTypeResponse()));

        DatePicker dateResponsePicker = new DatePicker();
        if (response.getDateResponse() != null) {
            dateResponsePicker.setValue(response.getDateResponse().toLocalDate());
        }

        // Add fields to grid
        grid.add(new Label("Message:"), 0, 0);
        grid.add(messageField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeResponseBox, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(dateResponsePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                response.setMessage(messageField.getText());
                response.setTypeResponse(Response.TYPERESPONSE.valueOf(typeResponseBox.getValue()));
                if (dateResponsePicker.getValue() != null) {
                    response.setDateResponse(java.sql.Date.valueOf(dateResponsePicker.getValue()));
                }
                return response;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedResponse -> {
            responseService.update(updatedResponse);
            loadData();
        });
    }

    private void loadData() {
        try {
            submission_table.setItems(FXCollections.observableArrayList(submissionService.returnList()));
            response_table.setItems(FXCollections.observableArrayList(responseService.returnList()));
            animateTotalSubmissions(submissionService.returnList().size());
        } catch (SQLException e) {
            showAlert("Database Error", "Loading failed: " + e.getMessage());
        }
    }

    private void setupScrollingText() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(news_pane.widthProperty());
        clip.heightProperty().bind(news_pane.heightProperty());
        news_pane.setClip(clip);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(15), scrolling_text);
        transition.setFromX(news_pane.getWidth());
        transition.setToX(-scrolling_text.getLayoutBounds().getWidth());
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }

    private void animateTotalSubmissions(int total) {
        IntegerProperty count = new SimpleIntegerProperty(0);
        count.addListener((obs, oldVal, newVal) ->
                totalSubmissions.setText(String.valueOf(newVal)));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(count, total))
        );
        timeline.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupSubmissionColumns() {
        // Add update column for submissions
        TableColumn<Submission, Void> updateSubmissionColumn = new TableColumn<>("Update");
        updateSubmissionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");

            {
                updateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                updateButton.setOnAction(event -> {
                    Submission submission = getTableView().getItems().get(getIndex());
                    showSubmissionUpdateDialog(submission);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : updateButton);
            }
        });

        submission_table.getColumns().add(updateSubmissionColumn);
    }

    private void showSubmissionUpdateDialog(Submission submission) {
        Dialog<Submission> dialog = new Dialog<>();
        dialog.setTitle("Update Submission");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create fields for submission properties
        TextField descriptionField = new TextField(submission.getDescription());

        ComboBox<Submission.STATUS> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(Submission.STATUS.values());
        statusBox.setValue(submission.getStatus());

        ComboBox<Submission.URGENCYLEVEL> urgencyBox = new ComboBox<>();
        urgencyBox.getItems().addAll(Submission.URGENCYLEVEL.values());
        urgencyBox.setValue(submission.getUrgencyLevel());

        DatePicker datePicker = new DatePicker();
        if (submission.getDateSubmission() != null) {
            datePicker.setValue(submission.getDateSubmission().toLocalDate());
        }

        // Add fields to grid
        grid.add(new Label("Description:"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Status:"), 0, 1);
        grid.add(statusBox, 1, 1);
        grid.add(new Label("Urgency Level:"), 0, 2);
        grid.add(urgencyBox, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                submission.setDescription(descriptionField.getText());
                submission.setStatus(statusBox.getValue());
                submission.setUrgencyLevel(urgencyBox.getValue());
                if (datePicker.getValue() != null) {
                    submission.setDateSubmission(java.sql.Date.valueOf(datePicker.getValue()));
                }
                return submission;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedSubmission -> {
            submissionService.update(updatedSubmission);
            loadData();
        });
    }
}