package tn.esprit.entities;

import javax.sound.sampled.*;

public class AudioFormatChecker {
    public static void main(String[] args) {
        float[] sampleRates = {8000.0F, 16000.0F, 22050.0F, 44100.0F, 48000.0F};
        int[] bits = {8, 16};
        int[] channels = {1, 2};
        boolean[] endianness = {false, true}; // false = little-endian, true = big-endian

        System.out.println("Checking supported formats for TargetDataLine (microphone input)...");
        for (float rate : sampleRates) {
            for (int bit : bits) {
                for (int ch : channels) {
                    for (boolean bigEndian : endianness) {
                        AudioFormat format = new AudioFormat(
                                AudioFormat.Encoding.PCM_SIGNED,
                                rate,
                                bit,
                                ch,
                                (bit / 8) * ch,
                                rate,
                                bigEndian
                        );
                        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                        boolean supported = AudioSystem.isLineSupported(info);
                        System.out.printf(
                                "Rate: %.0f Hz, Bits: %d, Channels: %d, %s-endian => %s%n",
                                rate,
                                bit,
                                ch,
                                bigEndian ? "big" : "little",
                                supported ? "SUPPORTED" : "not supported"
                        );
                    }
                }
            }
        }
    }
}

