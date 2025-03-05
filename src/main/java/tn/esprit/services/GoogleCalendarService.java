package tn.esprit.services;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory; // Updated to GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import java.awt.Desktop;
import java.net.URI;
//import com.google.api.services.calendar.model.Reminders;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Wattway Assignment Scheduler";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance(); // Updated to GsonFactory
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json"; // Your downloaded file

    public static Credential getCredentials(final HttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void createEvent(String description, LocalDateTime assignmentDate) {
        try {
            Calendar service = getCalendarService();

            Event event = new Event()
                    .setSummary("Mechanic Assignment")
                    .setDescription(description);

            // Convert LocalDateTime to ZonedDateTime
            ZonedDateTime startZonedDateTime = assignmentDate.atZone(ZoneId.systemDefault()).plusHours(8);

            // Add 12 hours to the start date
            ZonedDateTime endZonedDateTime = startZonedDateTime.plusHours(12);

            // Convert ZonedDateTime to Date
            Date startDate = Date.from(startZonedDateTime.toInstant());
            Date endDate = Date.from(endZonedDateTime.toInstant());

            // Set the start and end times with the correct time zone
            String timeZone = "Africa/Lagos"; // Replace with your calendar's time zone
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDate))
                    .setTimeZone(timeZone);
            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endDate))
                    .setTimeZone(timeZone);

            event.setStart(start);
            event.setEnd(end); // Set the end time to 12 hours after the start time

            // Set event visibility
            event.setVisibility("public");

            // Insert the event into the calendar
            String calendarId = "emir.chouaib@gmail.com"; // Use the primary calendar
            Event createdEvent = service.events().insert(calendarId, event).execute();

            // Print the event details
            System.out.println("Event details: " + createdEvent.toString());
            System.out.println("Event ID: " + createdEvent.getId());
            System.out.println("Event Link: " + createdEvent.getHtmlLink());
            System.out.println("Event Status: " + createdEvent.getStatus());

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(createdEvent.getHtmlLink()));
            } else {
                System.err.println("Desktop browsing is not supported on this platform.");
            }

            // Retrieve the event to verify it was created successfully
            Event retrievedEvent = service.events().get(calendarId, createdEvent.getId()).execute();
            System.out.println("Retrieved Event: " + retrievedEvent.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to create event: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

}