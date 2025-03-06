package tn.esprit.Controllers;

import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.Transcript;
import com.assemblyai.api.resources.transcripts.types.TranscriptStatus;
//import com.google.protobuf.compiler.PluginProtos;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import tn.esprit.entities.Assignment;
import tn.esprit.entities.Car;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.AssignmentServices;
import tn.esprit.services.GoogleCalendarService;
import tn.esprit.services.MechanicServices;
import tn.esprit.services.CarServices;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.InputStream;
import java.nio.file.Files;
import java.io.File;
import java.io.FileOutputStream;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class UpdateAssignmentController {

    @FXML
    private TextField descriptionAssignmentField;

    @FXML
    private ComboBox<Assignment.Status> statusAssignmentComboBox;

    @FXML
    private ComboBox<Car> carIdComboBox;

    @FXML
    private ListView<Mechanic> mechanicListView;

    @FXML
    private DatePicker dateAssignmentPicker; // Add this line

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;
    @FXML
    private TextField searchMechanicField;

    private final AssignmentServices assignmentServices = new AssignmentServices();
    private final MechanicServices mechanicServices = new MechanicServices();
    private final CarServices carServices = new CarServices();
    private Assignment assignment;
    private AssemblyAI client;
    private volatile boolean isRecording = false;
    private ByteArrayOutputStream recordingOutput;
    private TargetDataLine microphone;
    private Thread recordingThread;
    private File recordedFile;

    public void initData(Assignment assignment) throws SQLException {
        this.assignment = assignment;
        String apiKey = loadApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            showAlert("Error", "AssemblyAI API key is missing. Please check the config.properties file.");
            return;
        }
        client = AssemblyAI.builder()
                .apiKey(apiKey)
                .build();

        // Set Description Field
        descriptionAssignmentField.setText(assignment.getDescriptionAssignment());

        // Populate Status ComboBox
        statusAssignmentComboBox.getItems().addAll(Assignment.Status.values());
        statusAssignmentComboBox.setValue(assignment.getStatusAssignment());

        // Populate Car ID ComboBox from Database
        loadCars();

        Car selectedCar = carServices.getCarById(assignment.getIdCar());
        carIdComboBox.setValue(selectedCar);

        // Set DatePicker value
        if (assignment.getDateAssignment() != null) {
            dateAssignmentPicker.setValue(assignment.getDateAssignment().toLocalDate());
        }

        // Load Mechanics
        loadMechanics(assignment);
        searchMechanicField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                filterMechanics(newValue);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private void filterMechanics(String filter) throws SQLException {
        List<Mechanic> mechanicsList = mechanicServices.returnList();
        ObservableList<Mechanic> mechanicsObservable = FXCollections.observableArrayList(mechanicsList);

        if (filter == null || filter.isEmpty()) {
            // If search field is empty, show all mechanics
            mechanicListView.setItems(mechanicsObservable);
        } else {
            // Filter list by name (case-insensitive)
            ObservableList<Mechanic> filteredList = mechanicsObservable.filtered(mechanic ->
                    mechanic.getNameMechanic().toLowerCase().contains(filter.toLowerCase()));
            mechanicListView.setItems(filteredList);
        }
    }

    private void loadCars() {
        try {
            List<Car> cars = carServices.getAllCarsUnderRepair();
            ObservableList<Car> carList = FXCollections.observableArrayList(cars);

            if (carIdComboBox != null) {
                carIdComboBox.setItems(carList);

                carIdComboBox.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(Car car, boolean empty) {
                        super.updateItem(car, empty);
                        if (empty || car == null) {
                            setText(null);
                        } else {
                            setText(car.getCarDisplayName()); // "Model (Brand, Year)"
                        }
                    }
                });

                carIdComboBox.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(Car car, boolean empty) {
                        super.updateItem(car, empty);
                        if (empty || car == null) {
                            setText(null);
                        } else {
                            setText(car.getCarDisplayName());
                        }
                    }
                });

            } else {
                System.err.println("Error: carIdComboBox is null!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMechanics(Assignment assignment) {
        try {
            List<Mechanic> allMechanics = mechanicServices.returnList();
            List<Mechanic> assignedMechanics = assignmentServices.getMechanicsByAssignmentId(assignment.getIdAssignment());

            ObservableList<Mechanic> mechanicsObservable = FXCollections.observableArrayList(allMechanics);
            mechanicListView.setItems(mechanicsObservable);
            mechanicListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            // Pre-select assigned mechanics
            for (Mechanic mechanic : assignedMechanics) {
                for (Mechanic listMechanic : mechanicListView.getItems()) {
                    if (listMechanic.getIdMechanic() == mechanic.getIdMechanic()) {
                        mechanicListView.getSelectionModel().select(listMechanic);
                        break;
                    }
                }
            }

            // Display mechanics properly in the ListView
            mechanicListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Mechanic mechanic, boolean empty) {
                    super.updateItem(mechanic, empty);
                    if (empty || mechanic == null) {
                        setText(null);
                    } else {
                        setText(mechanic.getNameMechanic() + " - " + mechanic.getSpecialityMechanic() + " (ID: " + mechanic.getIdMechanic() + ")");
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void saveAssignment() {
        try {
            String description = descriptionAssignmentField.getText();
            Assignment.Status status = statusAssignmentComboBox.getValue();
            Car selectedCar = carIdComboBox.getValue();
            LocalDate newDate = dateAssignmentPicker.getValue();

            if (description.isEmpty() || status == null || selectedCar == null || newDate == null) {
                showAlert("Missing Information", "Please fill all required fields.");
                return;
            }

            if (!carServices.isCarExists(selectedCar.getIdCar())) {
                showAlert("Invalid User", "The selected Car ID does not exist.");
                return;
            }

            List<Mechanic> selectedMechanics = mechanicListView.getSelectionModel().getSelectedItems();
            assignmentServices.updateAssignmentWithMechanics(assignment.getIdAssignment(), selectedMechanics);
            if (status == Assignment.Status.COMPLETED) {
                for (Mechanic m : selectedMechanics) {
                    mechanicServices.incrementCarsRepaired(m.getIdMechanic());
                    System.out.println("Added car repaired to the assigned mechanic");
                }
            }

            // Convert the new LocalDate to a LocalDateTime
            LocalDateTime newDateTime = newDate.atStartOfDay();
            LocalDateTime oldDateTime = assignment.getDateAssignment();
            boolean dateChanged = !newDateTime.equals(oldDateTime);

            // Update assignment details
            assignment.setDescriptionAssignment(description);
            assignment.setStatusAssignment(status);
            assignment.setIdCar(selectedCar.getIdCar());
            assignment.setDateAssignment(newDateTime);
            // Only update the Google Calendar event if the date has changed.
            if (dateChanged && assignment.getGoogleCalendarEventId() != null) {
                GoogleCalendarService.updateEvent(assignment);
                System.out.println("Google Calendar event updated.");
            } else {
                System.out.println("Google Calendar event not updated as the date remains the same or event id is missing.");
            }

            assignmentServices.update(assignment);



            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleRecordButton() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecordingAndSaveFile();
        }
    }

    private void startRecording() {
        try {
            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100.0F,
                    16,
                    2,      // stereo
                    4,      // frame size: 2 bytes per sample * 2 channels
                    44100.0F,
                    false   // little-endian
            );
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                throw new Exception("Audio format not supported on this system.");
            }
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            recordingOutput = new ByteArrayOutputStream();
            isRecording = true;
            // Update UI to indicate recording has started
            javafx.application.Platform.runLater(() -> descriptionAssignmentField.setPromptText("Recording..."));
            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[4096];
                while (isRecording) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        recordingOutput.write(buffer, 0, bytesRead);
                    }
                }
            });
            recordingThread.start();
            System.out.println("Recording started.");
        } catch (Exception e) {
            javafx.application.Platform.runLater(() -> showAlert("Recording Error", e.getMessage()));
        }
    }

    private void stopRecordingAndSaveFile() {
        isRecording = false; // signal the thread to stop
        try {
            // Wait for the recording thread to finish
            if (recordingThread != null) {
                recordingThread.join();
            }
            microphone.stop();
            microphone.close();

            // Restore the UI prompt
            Platform.runLater(() -> descriptionAssignmentField.setPromptText("Enter assignment description"));

            // Write the recorded data to a WAV file with proper header
            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100.0F,
                    16,
                    2,      // stereo
                    4,      // frame size
                    44100.0F,
                    false   // little-endian
            );
            recordedFile = new File("recorded_audio.wav");
            try (AudioInputStream ais = new AudioInputStream(
                    new java.io.ByteArrayInputStream(recordingOutput.toByteArray()),
                    format,
                    recordingOutput.size() / format.getFrameSize())
            ) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, recordedFile);
            }
            System.out.println("Recording stopped and saved to " + recordedFile.getAbsolutePath());
        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Recording Stop Error", e.getMessage()));
        }
    }

    // When the transcribe button is clicked, upload and transcribe the recorded file.
    @FXML
    private void handleTranscribeButton() {
        try {
            if (recordedFile == null || !recordedFile.exists()) {
                showAlert("Error", "No recorded file found. Please record audio first.");
                return;
            }
            transcribeAudio(recordedFile);
        } catch (Exception e) {
            showAlert("Transcription Error", e.getMessage());
        }
    }
    private String loadApiKey() {
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("assemblyai.api.key");
        } catch (Exception e) {
            showAlert("Error", "Failed to load API key: " + e.getMessage());
            return null;
        }
    }

    private void transcribeAudio(File audioFile) throws Exception {
        String apiKey = loadApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            showAlert("Error", "AssemblyAI API key is missing. Please check the config.properties file.");
            return;
        }
        // Convert the file to a byte array
        byte[] audioBytes = Files.readAllBytes(audioFile.toPath());

        // Upload the file using the byte array
        var uploadedFile = client.files().upload(audioBytes);
        String fileUrl = uploadedFile.getUploadUrl();

        // Transcribe using the file URL (the SDK expects a String)
        Transcript transcript = client.transcripts().transcribe(fileUrl);

        if (transcript.getStatus() == TranscriptStatus.ERROR) {
            throw new Exception("Transcript failed with error: " +
                    transcript.getError().orElse("Unknown error"));
        }
        String transcribedText = transcript.getText().orElse("No text transcribed");
        Platform.runLater(() -> {
            String currentText = descriptionAssignmentField.getText();
            descriptionAssignmentField.setText(currentText + " " + transcribedText);
        });
        System.out.println("Transcription: " + transcript.getText().orElse("No text transcribed"));
    }
}