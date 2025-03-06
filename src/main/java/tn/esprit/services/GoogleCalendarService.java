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
import tn.esprit.entities.Assignment;

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

    public static Event createEvent(Assignment assignment) {
        try {
            Calendar service = getCalendarService();

            Event event = new Event()
                    .setSummary("Mechanic Assignment")
                    .setDescription(assignment.getDescriptionAssignment());

            // Convert the assignment's date to start and end times.
            ZonedDateTime startZonedDateTime = assignment.getDateAssignment()
                    .atZone(ZoneId.systemDefault())
                    .plusHours(8);
            ZonedDateTime endZonedDateTime = startZonedDateTime.plusHours(12);

            Date startDate = Date.from(startZonedDateTime.toInstant());
            Date endDate = Date.from(endZonedDateTime.toInstant());

            String timeZone = "Africa/Lagos";
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDate))
                    .setTimeZone(timeZone);
            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endDate))
                    .setTimeZone(timeZone);

            event.setStart(start);
            event.setEnd(end);
            event.setVisibility("public");

            String calendarId = "emir.chouaib@gmail.com"; // or "primary"
            Event createdEvent = service.events().insert(calendarId, event).execute();

            System.out.println("Created Event: " + createdEvent);
            System.out.println("Event ID: " + createdEvent.getId());

            // Save the event ID in the assignment.
            assignment.setGoogleCalendarEventId(createdEvent.getId());
            // Then update your assignment in the database so the event ID is stored.
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(createdEvent.getHtmlLink()));
            } else {
                System.err.println("Desktop browsing is not supported on this platform.");
            }

            return createdEvent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateEvent(Assignment assignment) {
        // This method assumes that assignment.getGoogleCalendarEventId() is not null.
        try {
            Calendar service = getCalendarService();
            String calendarId = "emir.chouaib@gmail.com"; // or "primary"

            // Retrieve the event using the stored event ID.
            String eventId = assignment.getGoogleCalendarEventId();
            if (eventId == null || eventId.isEmpty()) {
                System.err.println("No Google Calendar event ID found.");
                return;
            }
            Event event = service.events().get(calendarId, eventId).execute();

            // Update event description
            event.setDescription(assignment.getDescriptionAssignment());

            // Calculate new start and end times based on the updated assignment date.
            ZonedDateTime startZonedDateTime = assignment.getDateAssignment()
                    .atZone(ZoneId.systemDefault())
                    .plusHours(8);
            ZonedDateTime endZonedDateTime = startZonedDateTime.plusHours(12);

            Date startDate = Date.from(startZonedDateTime.toInstant());
            Date endDate = Date.from(endZonedDateTime.toInstant());

            String timeZone = "Africa/Lagos";
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDate))
                    .setTimeZone(timeZone);
            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(endDate))
                    .setTimeZone(timeZone);

            event.setStart(start);
            event.setEnd(end);

            // Update the event on Google Calendar.
            Event updatedEvent = service.events().update(calendarId, eventId, event).execute();
            System.out.println("Updated Event: " + updatedEvent);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to update event: " + e.getMessage());
        }
    }



}