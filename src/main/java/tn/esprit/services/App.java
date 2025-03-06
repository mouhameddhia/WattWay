// Make sure to add com.assemblyai:assemblyai-java to your dependencies
/*
import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.requests.TranscriptParams;
import com.assemblyai.api.resources.transcripts.types.*;

import java.io.File;
import java.util.List;

public final class App {

    public static void main(String... args) throws Exception {
        var apiKey = "f2f3415e84d14db9b00c69a51b1726e1";
        var fileUrl = "https://assembly.ai/wildfires.mp3";

        var client = AssemblyAI.builder()
                .apiKey(apiKey)
                .build();

        //// You can also transcribe a local file by passing in a file path
        // var filePath = "./path/to/file.mp3";
        // var uploadedFile = client.files().upload(new File(filePath));
        // fileUrl = uploadedFile.getUploadUrl();

    }

    // transcript parameters
    var transcriptParams = TranscriptParams.builder()
            .audioUrl(fileUrl)
            .build();

    var transcript = client.transcripts().transcribe(transcriptParams);

        if (transcript.getStatus() == TranscriptStatus.ERROR) {
        throw new Exception("Transcript failed with error: " + transcript.getError().get());
    }

        System.out.println(transcript);
}
}

*/