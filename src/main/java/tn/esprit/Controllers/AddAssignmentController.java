package tn.esprit.Controllers;

import com.assemblyai.api.resources.transcripts.requests.TranscriptParams;
import com.assemblyai.api.resources.transcripts.types.TranscriptStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.converter.StringConverter;
import javafx.scene.control.*;
import tn.esprit.entities.Assignment;
import tn.esprit.entities.Car;
import tn.esprit.entities.Mechanic;
import tn.esprit.services.*;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.Transcript;
import com.assemblyai.api.resources.transcripts.types.TranscriptWord;
import com.assemblyai.api.resources.transcripts.types.TranscriptSentence;

import javax.mail.MessagingException;
import javax.sound.sampled.*;
import java.util.Properties;

//import org.vosk.Model;
//import org.vosk.Recognizer;
//import javax.sound.sampled.*;
//import java.io.ByteArrayOutputStream;

public class AddAssignmentController {

    @FXML
    private TextArea descriptionAssignmentField;
    @FXML
    private ComboBox<Assignment.Status> statusAssignmentComboBox;
    @FXML
    private ComboBox<Car> carIdComboBox;
    @FXML
    private ListView<Mechanic> mechanicListView;
    @FXML
    private TableColumn<Assignment, String> mechanicsColumn;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private DatePicker datePicker;



    private final AssignmentServices assignmentServices = new AssignmentServices();
    private final CarServices carServices = new CarServices();
    private final MechanicServices mechanicServices = new MechanicServices();
    private AssemblyAI client;
    private volatile boolean isRecording = false;
    private File recordedFile;

    private ByteArrayOutputStream recordingOutput;
    private TargetDataLine microphone;
    private Thread recordingThread;

    //private Model voskModel;
    //private TargetDataLine microphone;


    @FXML
    private void initialize() {
        loadMechanics();
        statusAssignmentComboBox.getItems().addAll(Assignment.Status.values());
        //statusAssignmentComboBox.getItems().addAll(Assignment.Status.values());
        loadCars();
        //initializeVoskModel();
        String apiKey = loadApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            showAlert("Error", "AssemblyAI API key is missing. Please check the config.properties file.");
            return;
        }

        // Initialize AssemblyAI client
        client = AssemblyAI.builder()
                .apiKey(apiKey)
                .build();

        mechanicListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

        mechanicListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
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
        carIdComboBox.setCellFactory(lv -> new ListCell<Car>() {
            @Override
            protected void updateItem(Car car, boolean empty) {
                super.updateItem(car, empty);
                if (empty || car == null) {
                    setText(null);
                } else {
                    setText(car.getModelCar() + " (" + car.getBrandCar() + ", " + car.getYearCar() + ")");
                }
            }
        });

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
    private void loadCars() {
        try {
            List<Car> cars = carServices.getAllCarsUnderRepair();
            ObservableList<Car> carList = FXCollections.observableArrayList(cars);

            if (carIdComboBox != null) {
                carIdComboBox.setItems(carList);
            } else {
                System.err.println("Error: carIdComboBox is null!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
    private void initializeVoskModel() {
        try {
            // Load the DLL
            String dllPath = System.getProperty("user.dir") + "/libs/libvosk.dll";
            System.out.println("Loading DLL from: " + dllPath);
            System.load(dllPath);

            // Load the model
            String modelPath = System.getProperty("user.dir") + "/src/vosk-models/vosk-model-small-en-us-0.15";
            System.out.println("Loading model from: " + modelPath);
            voskModel = new Model(modelPath);
        } catch (Exception e) {
            showAlert("Speech Recognition Error", "Failed to initialize speech recognition: " + e.getMessage());
        }
    }
*/


    private void loadMechanics() {
        try {
            List<Mechanic> mechanicsList = mechanicServices.returnList();
            ObservableList<Mechanic> mechanicsObservable = FXCollections.observableArrayList(mechanicsList);

            mechanicListView.setItems(mechanicsObservable);

            mechanicListView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);

            mechanicListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
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
        String description = descriptionAssignmentField.getText();
        Assignment.Status status = statusAssignmentComboBox.getValue();
        Car selectedCar = carIdComboBox.getValue();
        ObservableList<Mechanic> selectedMechanics = mechanicListView.getSelectionModel().getSelectedItems();

        if (description.isEmpty() || status == null  || selectedMechanics.isEmpty()) {
            showAlert("Empty","Please fill all fields.");
            return;
        }
        if (selectedCar == null) {
            showAlert("Empty ID Car","Please select ID Car.");
            return;
        }
        if (datePicker.getValue() == null) {
            showAlert("Invalid Date", "Please select an assignment date.");
            return;
        }
        LocalDateTime selectedDate = datePicker.getValue().atStartOfDay();

        try {
            //int idUser = Integer.parseInt(idUserText);

            Assignment newAssignment = new Assignment();
            newAssignment.setDescriptionAssignment(description);
            newAssignment.setStatusAssignment(status);
            newAssignment.setIdCar(selectedCar.getIdCar());
            newAssignment.setMechanics(new ArrayList<>(selectedMechanics));
            newAssignment.setDateAssignment(selectedDate);

            assignmentServices.addP(newAssignment);
            closeWindow();
        } catch (NumberFormatException e) {
            System.out.println("Error: User ID must be a number.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //send email
        for (Mechanic mechanic : selectedMechanics) {
            if (mechanic.getEmailMechanic() != null && !mechanic.getEmailMechanic().trim().isEmpty()) {
                String subject = "New Assignment Notification";
                String message = "Hello " + mechanic.getNameMechanic() + ",\n\n"
                        + "You have been assigned a new assignment.\n\n"
                        + "Description: " + descriptionAssignmentField.getText() + "\n"
                        + "Assignment Date: " + selectedDate + "\n"
                        + "Car: " + carIdComboBox.getValue().getCarDisplayName() + "\n\n"
                        + "Please check your dashboard for more details.\n\n"
                        + "Best regards,\n"
                        + "Wattway Team";

                try {
                    EmailService.sendEmail(mechanic.getEmailMechanic(), subject, message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Mechanic " + mechanic.getNameMechanic() + " has no email provided, skipping email notification.");
            }
        }

    }



    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        if (stage != null) {
            System.out.println("Closing window...");
            stage.close();
        } else {
            System.out.println("Error: Stage is null");
        }
    }
    /*
    @FXML
    private void startSpeechToText() {
        if (isRecording) {
            stopRecording();
            return;
        }

        new Thread(() -> {
            try {
                isRecording = true;

                // Set up audio capture with a supported format
                AudioFormat format = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        44100.0F,
                        16,
                        1,
                        2,
                        44100.0F,
                        true // big-endian
                );

                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();

                // Notify user
                Platform.runLater(() ->
                        descriptionAssignmentField.setPromptText("Listening... Speak now!")
                );

                // Create a temporary file to store the audio
                File tempFile = File.createTempFile("audio", ".wav");
                tempFile.deleteOnExit(); // Delete the file when the program exits

                // Stream audio to the temporary file
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                while (isRecording) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    out.write(buffer, 0, bytesRead);
                }

                // Save the audio to the temporary file
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(out.toByteArray());
                }

                // Send the audio file to AssemblyAI for transcription
                Transcript transcript = client.transcripts().transcribe(tempFile);
                String text = transcript.getText().orElse(""); // Use an empty string if transcription is empty

                // Update the description field
                Platform.runLater(() ->
                        descriptionAssignmentField.setText(descriptionAssignmentField.getText() + " " + text)
                );

                // Clean up
                microphone.stop();
                microphone.close();
                Platform.runLater(() ->
                        descriptionAssignmentField.setPromptText("Enter assignment description")
                );

            } catch (Exception e) {
                Platform.runLater(() ->
                        showAlert("Recording Error", "Failed to process audio: " + e.getMessage())
                );
            }
        }).start();
    }

    @FXML
    private void startSpeechToText() {
        if (voskModel == null) {
            showAlert("Error", "Speech recognition model not loaded!");
            return;
        }

        if (isRecording) {
            stopRecording();
            return;
        }

        new Thread(() -> {
            try {
                isRecording = true;
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();

                Recognizer recognizer = new Recognizer(voskModel, 16000);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];

                Platform.runLater(() ->
                        descriptionAssignmentField.setPromptText("Listening... Speak now!")
                );

                while (isRecording) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    out.write(buffer, 0, bytesRead);
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        String result = recognizer.getResult();
                        Platform.runLater(() ->
                                descriptionAssignmentField.setText(descriptionAssignmentField.getText() + " " + result)
                        );
                    }
                }

                microphone.stop();
                microphone.close();
                Platform.runLater(() ->
                        descriptionAssignmentField.setPromptText("Enter assignment description")
                );

            } catch (Exception e) {
                Platform.runLater(() ->
                        showAlert("Recording Error", "Failed to process audio: " + e.getMessage())
                );
            }
        }).start();
    }
*/
    private void stopRecording() {
        isRecording = false;
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

            // Update UI prompt immediately
            Platform.runLater(() -> descriptionAssignmentField.setPromptText("Recording..."));

            // Start a thread to continuously capture audio data
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
            Platform.runLater(() -> showAlert("Recording Error", e.getMessage()));
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

    // Call this method when the user clicks the transcribe button
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

    // Recording method: records audio and saves it as a WAV file
    private File recordAudio() throws Exception {
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

        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();

        System.out.println("Recording...");

        // Record audio for a fixed duration (e.g., 5 seconds)
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        long end = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < end) {
            int bytesRead = microphone.read(buffer, 0, buffer.length);
            out.write(buffer, 0, bytesRead);
        }

        microphone.stop();
        microphone.close();

        // Write the recorded data to a WAV file with a proper header.
        File audioFile = new File("recorded_audio.wav");
        try (AudioInputStream ais = new AudioInputStream(
                new java.io.ByteArrayInputStream(out.toByteArray()),
                format,
                out.size() / format.getFrameSize())
        ) {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
        }
        System.out.println("Audio saved to " + audioFile.getAbsolutePath());
        return audioFile;
    }

    // Example transcription method using AssemblyAI (adjust as needed)
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

