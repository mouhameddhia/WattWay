package tn.esprit.Controllers;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import org.json.JSONObject;
import tn.esprit.entities.Submission;
import tn.esprit.entities.Response;
import tn.esprit.services.SentimentAnalysisService;
import tn.esprit.services.SubmissionServices;
import tn.esprit.services.ResponseServices;
import tn.esprit.services.AIResponseService;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @FXML private Button generateAIResponseButton;

    private SubmissionServices submissionService;
    private ResponseServices responseService;
    private AIResponseService aiService;
    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private POSTaggerME posTagger;


    @FXML
    private TableColumn<Submission, Void> sentimentColumn; // Make sure to import the correct class

    @FXML
    void initialize() {
        submissionService = new SubmissionServices();
        responseService = new ResponseServices();
        aiService = new AIResponseService("AIzaSyDmPbDHQxHwKEDH8mkuFBiUXm0aiNrdVC0");
        //urgencyLevelid.setVisible(false);

        initializeColumns();
        loadData();
        setupScrollingText();
        setupSubmissionColumns();
        setupQuickView();
        initializeNLPModels();
        setupSmartSummarizer();

        // Set up the button action
        generateAIResponseButton.setOnAction(event -> {
            System.out.println("Generate button clicked"); // Debug print
            generateAIResponse();
        });
// Set up the sentiment analysis button in the sentimentColumn
        sentimentColumn.setCellFactory(col -> new TableCell<Submission, Void>() {
            private final Button analyzeButton = new Button("Analyze");

            {
                analyzeButton.setOnAction(event -> {
                    Submission submission = getTableView().getItems().get(getIndex());
                    analyzeSentiment(submission);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item != null) {
                    setGraphic(null);
                } else {
                    setGraphic(analyzeButton);
                }
            }
        });


    }
    @FXML
    private void analyzeSentiment(Submission submission) {
        if (submission == null) {
            showAlert("Error", "Please select a submission first.");
            return;
        }

        String description = submission.getDescription();
        SentimentAnalysisService sentimentService = new SentimentAnalysisService();

        try {
            String sentimentResult = sentimentService.analyzeSentiment(description);
            JSONObject jsonResponse = new JSONObject(sentimentResult);

            // Create a custom dialog for better visualization
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Sentiment Analysis Result");
            dialog.setHeaderText("Analysis for submission: " + submission.getIdSubmission());

            // Create the content
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));

            // Description
            Label descLabel = new Label("Text Analyzed:");
            TextArea descArea = new TextArea(description);
            descArea.setWrapText(true);
            descArea.setEditable(false);
            descArea.setPrefRowCount(2);

            // Sentiment Result
            Label sentimentLabel = new Label("Sentiment: " + jsonResponse.getString("sentiment"));
            sentimentLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + jsonResponse.getString("color"));

            // Confidence
            double confidence = jsonResponse.getDouble("confidence");
            ProgressBar confidenceBar = new ProgressBar(confidence / 100);
            confidenceBar.setPrefWidth(200);

            Label confidenceLabel = new Label(String.format("Confidence: %.1f%%", confidence));

            // Details
            Label detailsLabel = new Label(String.format(
                    "Polarity: %.2f\nSubjectivity: %.2f",
                    jsonResponse.getDouble("polarity"),
                    jsonResponse.getDouble("subjectivity")
            ));

            content.getChildren().addAll(
                    descLabel, descArea,
                    new Separator(),
                    sentimentLabel,
                    new HBox(10, confidenceBar, confidenceLabel),
                    detailsLabel
            );

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

            // Add custom styling
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            dialog.showAndWait();

        } catch (Exception e) {
            showAlert("Error", "Failed to analyze sentiment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeColumns() {
        // Submission Table Columns
        idSubmissionColumn.setCellValueFactory(new PropertyValueFactory<>("idSubmission"));
        idSubmissionColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");
        idSubmissionColumn.setPrefWidth(80);  // Increased width

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 14px;");
        descriptionColumn.setPrefWidth(300);  // Increased width

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");
        statusColumn.setPrefWidth(120);  // Increased width

        urgencyLevelColumn.setCellValueFactory(new PropertyValueFactory<>("urgencyLevel"));
        urgencyLevelColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");
        urgencyLevelColumn.setPrefWidth(120);  // Increased width

        dateSubmissionColumn.setCellValueFactory(new PropertyValueFactory<>("dateSubmission"));
        dateSubmissionColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");
        dateSubmissionColumn.setPrefWidth(120);  // Increased width

        idCarColumn.setCellValueFactory(new PropertyValueFactory<>("idCar"));
        idCarColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");
        idCarColumn.setPrefWidth(80);  // Increased width

        idUserColumn.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        idUserColumn.setStyle("-fx-alignment: CENTER; -fx-font-size: 14px;");
        idUserColumn.setPrefWidth(80);  // Increased width

        addResponseColumn.setCellValueFactory(param -> new SimpleObjectProperty<>());
        deleteSubmissionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>());

        idResponseColumn.setCellValueFactory(new PropertyValueFactory<>("idResponse"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        dateResponseColumn.setCellValueFactory(new PropertyValueFactory<>("dateResponse"));
        typeResponseColumn.setCellValueFactory(new PropertyValueFactory<>("typeResponse"));

        setupAddResponseColumn();  // Updated method for Add Response button
        setupDeleteSubmissionColumn();
        setupResponseActionColumn();

        submission_table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                highlightRelatedResponses(newSelection);
            }
        });
    }

    private void initializeNLPModels() {
        try {
            // Load sentence detector model
            try (InputStream sentModelIn = getClass().getResourceAsStream("/en-sent.bin")) {
                SentenceModel sentModel = new SentenceModel(sentModelIn);
                sentenceDetector = new SentenceDetectorME(sentModel);
            }

            // Load tokenizer model
            try (InputStream tokenModelIn = getClass().getResourceAsStream("/en-token.bin")) {
                TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
                tokenizer = new TokenizerME(tokenModel);
            }

            // Load POS tagger model
            try (InputStream posModelIn = getClass().getResourceAsStream("/en-pos-maxent.bin")) {
                POSModel posModel = new POSModel(posModelIn);
                posTagger = new POSTaggerME(posModel);
            }
        } catch (IOException e) {
            showErrorAlert("Error", "Failed to load NLP models: " + e.getMessage());
        }
    }

    private void setupSmartSummarizer() {
        descriptionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button summarizeBtn = new Button("🔍");

            {
                summarizeBtn.setStyle("-fx-background-color: transparent;");
                summarizeBtn.setOnAction(event -> {
                    Submission submission = getTableRow().getItem();
                    if (submission != null) {
                        showNLPSummary(submission.getDescription());
                    }
                });
            }

            @Override
            protected void updateItem(String description, boolean empty) {
                super.updateItem(description, empty);
                if (empty || description == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(description);
                    setGraphic(summarizeBtn);
                }
            }
        });
    }

    private void showNLPSummary(String text) {
        if (text == null || text.trim().isEmpty()) {
            showErrorAlert("Error", "Cannot summarize empty text");
            return;
        }

        try {
            // Generate summary using NLP
            String summary = generateNLPSummary(text);
            Set<String> keyTerms = extractKeyTerms(text);

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("NLP Summary");

            VBox content = new VBox(10);
            content.setPadding(new Insets(15));

            // Summary section
            Label summaryLabel = new Label("Summary:");
            summaryLabel.setStyle("-fx-font-weight: bold");
            TextArea summaryArea = new TextArea(summary);
            summaryArea.setEditable(false);
            summaryArea.setWrapText(true);
            summaryArea.setPrefRowCount(3);

            // Key terms section
            Label termsLabel = new Label("Key Terms:");
            termsLabel.setStyle("-fx-font-weight: bold");
            FlowPane keyTermsPane = new FlowPane(5, 5);
            keyTerms.forEach(term -> {
                Label termLabel = new Label(term);
                termLabel.setStyle(
                        "-fx-background-color: #e9ecef;" +
                                "-fx-padding: 5px;" +
                                "-fx-background-radius: 3px"
                );
                keyTermsPane.getChildren().add(termLabel);
            });

            content.getChildren().addAll(
                    summaryLabel,
                    summaryArea,
                    termsLabel,
                    keyTermsPane
            );

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();

        } catch (Exception e) {
            showErrorAlert("Error", "Could not generate summary: " + e.getMessage());
        }
    }

    private String generateNLPSummary(String text) {
        if (sentenceDetector == null) {
            throw new IllegalStateException("NLP models not initialized");
        }

        // Detect sentences
        String[] sentences = sentenceDetector.sentDetect(text);

        // If text is short, return as is
        if (sentences.length <= 2) {
            return text;
        }

        // Score sentences using NLP features
        Map<String, Double> scores = new HashMap<>();
        for (String sentence : sentences) {
            double score = 0.0;

            // Tokenize sentence
            String[] tokens = tokenizer.tokenize(sentence);

            // Get POS tags
            String[] tags = posTagger.tag(tokens);

            // Score based on POS patterns
            for (int i = 0; i < tags.length; i++) {
                // Nouns and verbs are important
                if (tags[i].startsWith("NN")) score += 0.2; // Noun
                if (tags[i].startsWith("VB")) score += 0.1; // Verb
                if (tags[i].startsWith("JJ")) score += 0.1; // Adjective
            }

            scores.put(sentence, score);
        }

        // Get top scoring sentences
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(2)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(" "));
    }

    private Set<String> extractKeyTerms(String text) {
        if (tokenizer == null || posTagger == null) {
            throw new IllegalStateException("NLP models not initialized");
        }

        // Tokenize text
        String[] tokens = tokenizer.tokenize(text);

        // Get POS tags
        String[] tags = posTagger.tag(tokens);

        // Extract nouns and important verbs
        return IntStream.range(0, tokens.length)
                .filter(i -> tags[i].startsWith("NN") || tags[i].startsWith("VB"))
                .mapToObj(i -> tokens[i].toLowerCase())
                .filter(token -> token.length() > 3)
                .distinct()
                .limit(5)
                .collect(Collectors.toSet());
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
        // Create the dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Response");
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Create the content pane with gradient background
        AnchorPane dialogPane = new AnchorPane();
        dialogPane.getStyleClass().add("gradient-pane");

        // Create the form container
        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("form-container");
        formContainer.setPrefWidth(500);
        formContainer.setPrefHeight(500);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(20));

        // Create form elements
        Label titleLabel = new Label("Update Response");
        titleLabel.getStyleClass().add("form-title");

        Label messageLabel = new Label("Message:");
        messageLabel.getStyleClass().add("form-label");
        TextArea messageField = new TextArea(response.getMessage());
        messageField.getStyleClass().add("form-input");
        messageField.setStyle("-fx-text-fill: black;");
        messageField.setWrapText(true);
        messageField.setPrefRowCount(3);

        Label typeLabel = new Label("Response Type:");
        typeLabel.getStyleClass().add("form-label");

        // Replace TextField with ComboBox for type
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getStyleClass().add("form-input");
        typeComboBox.getItems().addAll("Technical", "Administrative", "Financial"); // Add your response types here
        typeComboBox.setValue(String.valueOf(response.getTypeResponse()));
        typeComboBox.setStyle("-fx-text-fill: black;");

        // Button container with only Update and Cancel buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("primary-button");
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");

        buttonBox.getChildren().addAll(updateButton, cancelButton);

        // Add all elements to the form container
        formContainer.getChildren().addAll(
                titleLabel,
                messageLabel, messageField,
                typeLabel, typeComboBox,
                buttonBox
        );

        // Add form container to dialog pane
        dialogPane.getChildren().add(formContainer);
        AnchorPane.setTopAnchor(formContainer, 20.0);
        AnchorPane.setLeftAnchor(formContainer, 20.0);
        AnchorPane.setRightAnchor(formContainer, 20.0);
        AnchorPane.setBottomAnchor(formContainer, 20.0);

        // Set the dialog content
        dialog.getDialogPane().setContent(dialogPane);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Add buttons to dialog
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the update action
        updateButton.setOnAction(e -> {
            try {
                response.setMessage(messageField.getText());
                response.setTypeResponse(Response.TYPERESPONSE.valueOf(typeComboBox.getValue()));

                responseService.update(response);
                loadData();
                showSuccessAlert("Success", "Response updated successfully!");
                dialog.close();
            } catch (Exception ex) {
                showErrorAlert("Error", "Failed to update response: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        dialog.showAndWait();
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
        if (news_pane == null || scrolling_text == null) {
            return; // Skip if components are not available
        }

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
        // Create the dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Submission");
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Create the content pane with gradient background
        AnchorPane dialogPane = new AnchorPane();
        dialogPane.getStyleClass().add("gradient-pane");

        // Create the form container
        VBox formContainer = new VBox(15);
        formContainer.getStyleClass().add("form-container");
        formContainer.setPrefWidth(500);
        formContainer.setPrefHeight(600);
        formContainer.setAlignment(Pos.CENTER);

        // Add padding to the form
        formContainer.setPadding(new Insets(20));

        // Create form elements
        Label titleLabel = new Label("Update Submission");
        titleLabel.getStyleClass().add("form-title");

        Label descLabel = new Label("Description:");
        descLabel.getStyleClass().add("form-label");
        TextArea descField = new TextArea(submission.getDescription());
        descField.getStyleClass().add("form-input");
        descField.setStyle("-fx-text-fill: black;");
        descField.setWrapText(true);
        descField.setPrefRowCount(3);

        Label statusLabel = new Label("Status:");
        statusLabel.getStyleClass().add("form-label");
        ComboBox<Submission.STATUS> statusCombo = new ComboBox<>();
        statusCombo.getStyleClass().add("form-input");
        statusCombo.getItems().addAll(Submission.STATUS.values());
        statusCombo.setValue(submission.getStatus());

        Label urgencyLabel = new Label("Urgency Level:");
        urgencyLabel.getStyleClass().add("form-label");
        ComboBox<Submission.URGENCYLEVEL> urgencyCombo = new ComboBox<>();
        urgencyCombo.getStyleClass().add("form-input");
        urgencyCombo.getItems().addAll(Submission.URGENCYLEVEL.values());
        urgencyCombo.setValue(submission.getUrgencyLevel());

        // Button container with only Update and Cancel buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("primary-button");
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");

        buttonBox.getChildren().addAll(updateButton, cancelButton);

        // Add all elements to the form container
        formContainer.getChildren().addAll(
                titleLabel,
                descLabel, descField,
                statusLabel, statusCombo,
                urgencyLabel, urgencyCombo,
                buttonBox
        );

        // Add form container to dialog pane
        dialogPane.getChildren().add(formContainer);
        AnchorPane.setTopAnchor(formContainer, 20.0);
        AnchorPane.setLeftAnchor(formContainer, 20.0);
        AnchorPane.setRightAnchor(formContainer, 20.0);
        AnchorPane.setBottomAnchor(formContainer, 20.0);

        // Set the dialog content
        dialog.getDialogPane().setContent(dialogPane);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Add buttons to dialog
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the update action
        updateButton.setOnAction(e -> {
            try {
                submission.setDescription(descField.getText());
                submission.setStatus(statusCombo.getValue());
                submission.setUrgencyLevel(urgencyCombo.getValue());

                submissionService.update(submission);
                loadData();
                showSuccessAlert("Success", "Submission updated successfully!");
                dialog.close();
            } catch (Exception ex) {
                showErrorAlert("Error", "Failed to update submission: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        dialog.showAndWait();
    }

    // Add these helper methods for showing alerts
    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

    private void setupQuickView() {
        submission_table.setRowFactory(tv -> {
            TableRow<Submission> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showQuickViewDialog(row.getItem());
                }
            });
            return row;
        });
    }

    private void showQuickViewDialog(Submission submission) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Quick View");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                new Label("ID: " + submission.getIdSubmission()),
                new Label("Description: " + submission.getDescription()),
                new Label("Status: " + submission.getStatus()),
                new Label("Urgency: " + submission.getUrgencyLevel()),
                new Label("Date: " + submission.getDateSubmission())
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }


    private void generateAIResponse() {
        try {
            Submission selectedSubmission = submission_table.getSelectionModel().getSelectedItem();
            if (selectedSubmission == null) {
                showAlert("Error", "Please select a submission first.");
                return;
            }

            System.out.println("Selected submission description: " + selectedSubmission.getDescription());

            String prompt = String.format("Generate a professional response for the following submission: %s",
                    selectedSubmission.getDescription());
            System.out.println("Sending prompt: " + prompt);

            String aiMessage = aiService.generateResponse(prompt);
            System.out.println("Received AI message: " + aiMessage);

            // Load the AddResponse form
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddResponseInterface.fxml"));
            Parent root = loader.load();

            // Get the controller and set the generated message
            AddResponseController controller = loader.getController();
            if (controller == null) {
                System.out.println("Controller is null!");
                return;
            }

            controller.setSubmissionId(selectedSubmission.getIdSubmission());
            System.out.println("Setting message in controller: " + aiMessage);
            controller.setGeneratedMessage(aiMessage);

            // Create and show the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Response");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));

            // Show the dialog and wait for it to close
            dialogStage.showAndWait();

        } catch (Exception e) {
            System.out.println("Error in generateAIResponse: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to generate AI response: " + e.getMessage());
        }
    }

    private void loadResponses() {
        try {
            Submission selectedSubmission = submission_table.getSelectionModel().getSelectedItem();
            if (selectedSubmission != null) {
                // Get responses for the selected submission using its ID
                List<Response> responses = responseService.findResponsesBySubmissionId(
                        selectedSubmission.getIdSubmission()
                );
                response_table.getItems().clear();
                response_table.getItems().addAll(responses);
            }
        } catch (Exception e) {
            showAlert("Error", "Could not load responses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}


