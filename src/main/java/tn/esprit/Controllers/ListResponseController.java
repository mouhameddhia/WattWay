package tn.esprit.Controllers;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tn.esprit.entities.Response;
import tn.esprit.services.ResponseServices;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class ListResponseController {

    @FXML
    private VBox response_box;

    @FXML
    private Text totalResponses;

    @FXML
    private Text scrolling_text;

    @FXML
    private Pane news_pane;

    @FXML
    private TableView<Response> response_table;

    @FXML
    private TableColumn<Response, Integer> idResponseColumn;

    @FXML
    private TableColumn<Response, String> messageColumn;

    @FXML
    private TableColumn<Response, String> dateResponseColumn;

    @FXML
    private TableColumn<Response, String> typeResponseColumn;

    @FXML
    private TableColumn<Response, Integer> idUserColumn;

    @FXML
    private TableColumn<Response, Integer> idSubmissionColumn;

    @FXML
    void initialize() {
        try {
            ResponseServices responseServices = new ResponseServices();
            List<Response> responseList = responseServices.returnList();

            // Convert to ObservableList
            ObservableList<Response> observableList = FXCollections.observableList(responseList);

            // Animate total responses count
            animateTotalResponses(responseList.size());

            // Sort by date (newest first)
            observableList.sort(Comparator.comparing(Response::getDateResponse).reversed());

            // Bind columns to Response properties
            idResponseColumn.setCellValueFactory(new PropertyValueFactory<>("idResponse"));
            messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
            dateResponseColumn.setCellValueFactory(new PropertyValueFactory<>("dateResponse"));
            typeResponseColumn.setCellValueFactory(new PropertyValueFactory<>("typeResponse"));
            idUserColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
            idSubmissionColumn.setCellValueFactory(new PropertyValueFactory<>("idSubmission"));


            // Add data to TableView
            response_table.setItems(observableList);

            // Animate scrolling text
            animateScrollingText();

        } catch (SQLException e) {
            e.printStackTrace();
            // Add error handling (e.g., show alert)
        }
    }

    private void animateTotalResponses(int total) {
        int animationDurationMillis = 2000;
        Timeline timeline = new Timeline();

        totalResponses.setText("0");

        for (int count = 1; count <= total; count++) {
            KeyValue keyValue = new KeyValue(totalResponses.textProperty(), String.valueOf(count));
            KeyFrame keyFrame = new KeyFrame(Duration.millis(animationDurationMillis * count / total), keyValue);
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.play();
    }

    private void animateScrollingText() {
        String newsText = "° Latest responses - Monitor user interactions effectively °";
        scrolling_text.setText(newsText);

        Rectangle clip = new Rectangle(500, 60);
        news_pane.setClip(clip);

        double textWidth = scrolling_text.getLayoutBounds().getWidth();
        TranslateTransition transition = new TranslateTransition(Duration.millis(10000), scrolling_text);
        transition.setFromX(500);
        transition.setToX(-textWidth);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }
}