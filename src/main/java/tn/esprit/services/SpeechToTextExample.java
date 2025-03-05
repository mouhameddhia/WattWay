package tn.esprit.services;
import com.alphacephei.vosk.Model;
import com.alphacephei.vosk.Recognizer;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SpeechToTextExample {
    public static void main(String[] args) throws Exception {
        // Path to the Vosk model
        String modelPath = "vosk-models/vosk-model-small-en-us-0.15";

        // Load the Vosk model
        Model model = new Model(modelPath);

        // Set up audio capture
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();

        // Initialize recognizer
        Recognizer recognizer = new Recognizer(model, 16000);

        // Capture audio and transcribe
        System.out.println("Speak now...");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = microphone.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, bytesRead);
            if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                System.out.println("Transcription: " + recognizer.getResult());
            } else {
                System.out.println("Partial: " + recognizer.getPartialResult());
            }
        }

        // Clean up
        microphone.stop();
        microphone.close();
        System.out.println("Final: " + recognizer.getFinalResult());
    }
}