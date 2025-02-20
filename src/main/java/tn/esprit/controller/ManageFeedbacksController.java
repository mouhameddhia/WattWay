package tn.esprit.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import tn.esprit.services.FeedbackServices;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ManageFeedbacksController implements Initializable {

    @FXML
    private TableView<Map<String, Object>> feedbackTable;

    @FXML
    private TableColumn<Map<String, Object>, String> colContent;

    @FXML
    private TableColumn<Map<String, Object>, Integer> colRating;

    @FXML
    private TableColumn<Map<String, Object>, String> colDate;

    @FXML
    private TableColumn<Map<String, Object>, String> colUserName;

    @FXML
    private TableColumn<Map<String, Object>, String> colDelete; // Delete button column

    private FeedbackServices feedbackService = new FeedbackServices();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the table columns using custom callbacks
        colContent.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map<String, Object>, String> param) {
                return new SimpleStringProperty((String) param.getValue().get("contentFeedback"));
            }
        });

        colRating.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Map<String, Object>, Integer> param) {
                return new SimpleIntegerProperty((Integer) param.getValue().get("ratingFeedback")).asObject();
            }
        });

        colDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map<String, Object>, String> param) {
                return new SimpleStringProperty(param.getValue().get("dateFeedback").toString());
            }
        });

        colUserName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map<String, Object>, String> param) {
                return new SimpleStringProperty((String) param.getValue().get("userName"));
            }
        });

        // Set up the delete button column
        colDelete.setCellFactory(param -> {
            TableCell<Map<String, Object>, String> cell = new TableCell<Map<String, Object>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Button deleteButton = new Button("Delete");
                        deleteButton.setOnAction(event -> handleDeleteFeedback(getTableRow().getItem()));
                        setGraphic(deleteButton);
                    }
                }
            };
            return cell;
        });

        // Load feedback data
        loadFeedbacks();
    }

    private void loadFeedbacks() {
        // Fetch feedbacks from the database
        List<Map<String, Object>> feedbacks = feedbackService.getAllWithUserNames();
        ObservableList<Map<String, Object>> observableFeedbacks = FXCollections.observableArrayList(feedbacks);
        feedbackTable.setItems(observableFeedbacks);
    }

    private void handleDeleteFeedback(Map<String, Object> feedback) {
        if (feedback != null) {
            // Call the delete function with the feedback's ID
            int feedbackId = (int) feedback.get("idFeedback");
            feedbackService.delete(feedbackId);

            // Remove the feedback from the TableView
            feedbackTable.getItems().remove(feedback);

            System.out.println("Successfully deleted Feedback: " + feedback.get("contentFeedback"));
        }
    }
}