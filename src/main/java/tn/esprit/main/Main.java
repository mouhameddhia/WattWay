package tn.esprit.main;

import tn.esprit.entities.Response;
import tn.esprit.entities.Submission;
import tn.esprit.utils.MyDatabase;
import tn.esprit.services.SubmissionServices;
import tn.esprit.services.ResponseServices;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {

        MyDatabase db1 = MyDatabase.getInstance();
        SubmissionServices ss = new SubmissionServices();
        ResponseServices rs = new ResponseServices();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Submission Management Menu ---");
            System.out.println("1. Add a Submission");
            System.out.println("2. List All Submissions");
            System.out.println("3. Update a Submission");
            System.out.println("4. Delete a Submission");
            System.out.println("5. Add a Response");
            System.out.println("6. List All Responses");
            System.out.println("7. Update a Response");
            System.out.println("8. Delete a Response");
            System.out.println("9. Exit");
            System.out.print("Choose an option (1-9): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Add a Submission
                    System.out.println("\n--- Add a Submission ---");

                    System.out.print("Enter description: ");
                    String description = scanner.nextLine();

                    System.out.print("Enter status (Pending, Under1Review, Responded, Closed): ");
                    Submission.STATUS status = Submission.STATUS.valueOf(scanner.nextLine().toUpperCase());

                    System.out.print("Enter urgency level (Low, Medium, High): ");
                    Submission.URGENCYLEVEL urgencyLevel = Submission.URGENCYLEVEL.valueOf(scanner.nextLine().toUpperCase());

                    System.out.print("Enter date (yyyy-MM-dd): ");
                    String dateString = scanner.nextLine();
                    Date dateSubmission = Date.valueOf(dateString);

                    System.out.print("Enter car ID: ");
                    int idCar = scanner.nextInt();

                    System.out.print("Enter user ID: ");
                    int idUser = scanner.nextInt();

                    Submission submission = new Submission(0, description, status, urgencyLevel, dateSubmission, idCar, idUser);

                    try {
                        ss.add(submission);
                        System.out.println("✅ Submission added successfully!");
                    } catch (SQLException e) {
                        System.out.println("❌ Error: " + e.getMessage());
                    }
                    break;

                case 2:
                    // List all submissions
                    System.out.println("\n--- List All Submissions ---");
                    try {
                        List<Submission> submissions = ss.returnList();
                        if (submissions.isEmpty()) {
                            System.out.println("No submissions found.");
                        } else {
                            for (Submission sub : submissions) {
                                System.out.println("Submission ID: " + sub.getIdSubmission());
                                System.out.println("Description: " + sub.getDescription());
                                System.out.println("Status: " + sub.getStatus());
                                System.out.println("Urgency Level: " + sub.getUrgencyLevel());
                                System.out.println("Date: " + sub.getDateSubmission());
                                System.out.println("Car ID: " + sub.getIdCar());
                                System.out.println("User ID: " + sub.getIdUser());
                                System.out.println("-----------------------------------");
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("❌ Error fetching submissions: " + e.getMessage());
                    }
                    break;

                case 3:
                    // Update a Submission
                    System.out.println("\n--- Update a Submission ---");

                    System.out.print("Enter Submission ID: ");
                    int idSubmission = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    System.out.print("Enter new Description: ");
                    description = scanner.nextLine();

                    System.out.print("Enter new Status (Pending, Under_Review, Responded, Closed): ");
                    status = Submission.STATUS.valueOf(scanner.nextLine().toUpperCase());

                    System.out.print("Enter new Urgency Level (Low, Medium, High): ");
                    urgencyLevel = Submission.URGENCYLEVEL.valueOf(scanner.nextLine().toUpperCase());

                    System.out.print("Enter new Date (yyyy-MM-dd): ");
                    dateSubmission = Date.valueOf(scanner.nextLine());

                    // Create updated submission object
                    Submission updatedSubmission = new Submission(idSubmission, description, status, urgencyLevel, dateSubmission, 0, 0);

                    // Call update method
                    ss.update(updatedSubmission);

                    break;

                case 4:
                    // Delete a Submission
                    System.out.println("\n--- Delete a Submission ---");

                    System.out.print("Enter Submission ID to delete: ");
                    idSubmission = scanner.nextInt();

                    // Create a Submission object with the given ID
                    Submission submissionToDelete = new Submission();
                    submissionToDelete.setIdSubmission(idSubmission);

                    ss.delete(submissionToDelete);
                    break;

                case 5:
                    // Add a Response
                    System.out.println("\n--- Add a Response ---");

                    System.out.print("Enter Submission ID: ");
                    int idSubmissionForResponse = scanner.nextInt();


                    System.out.print(" Enter response message: ");
                    String message = scanner.nextLine();

                    System.out.print(" Enter response date (yyyy-MM-dd): ");
                    Date dateResponse = Date.valueOf(scanner.nextLine());

                    System.out.print("Enter response type (Acknowledgment,Resolution,ClarificationRequest): ");
                    Response.TYPERESPONSE typeResponse = Response.TYPERESPONSE.valueOf(scanner.nextLine().toUpperCase());

                    System.out.print("Enter User ID: ");
                    idUser = scanner.nextInt();



                    Response response = new Response(message, dateResponse, typeResponse, idUser, idSubmissionForResponse);

                    try {
                        rs.add(response);
                        System.out.println("✅ Response added successfully!");
                    } catch (SQLException e) {
                        System.out.println("❌ Error: " + e.getMessage());
                    }
                    break;

                case 6:
                    // List all Responses
                    System.out.println("\n--- List All Responses ---");
                    try {
                        List<Response> responses = rs.returnList();
                        if (responses.isEmpty()) {
                            System.out.println("No responses found.");
                        } else {
                            for (Response res : responses) {
                                System.out.println("Response ID: " + res.getIdResponse());
                                System.out.println("Message: " + res.getMessage());
                                System.out.println("Date: " + res.getDateResponse());
                                System.out.println("Type: " + res.getTypeResponse());
                                System.out.println("User ID: " + res.getIdUser());
                                System.out.println("Submission ID: " + res.getIdSubmission());
                                System.out.println("-----------------------------------");
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("❌ Error fetching responses: " + e.getMessage());
                    }
                    break;

                case 7:
                    // Update a Response
                    System.out.println("\n--- Update a Response ---");

                    System.out.print("Enter Response ID: ");
                    int idResponse = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    System.out.print("Enter new response message: ");
                    message = scanner.nextLine();

                    System.out.print("Enter new response date (yyyy-MM-dd): ");
                    dateResponse = Date.valueOf(scanner.nextLine());

                    System.out.print("Enter new response type (Acknowledgment,Resolution,ClarificationRequest)" ) ;
                    typeResponse = Response.TYPERESPONSE.valueOf(scanner.nextLine().toUpperCase());

                    System.out.print("Enter new User ID: ");
                    idUser = scanner.nextInt();

                    System.out.print("Enter new Submission ID: ");
                    idSubmissionForResponse = scanner.nextInt();

                    Response updatedResponse = new Response(message, dateResponse, typeResponse, idUser, idSubmissionForResponse);
                    updatedResponse.setIdResponse(idResponse); // Keep existing ID

                    rs.update(updatedResponse);
                    break;

                case 8:
                    System.out.println("\n--- Delete a Response ---");

                    System.out.print("Enter Response ID to delete: ");
                    idSubmission = scanner.nextInt();

                    // Create a Submission object with the given ID
                    Response responseToDelete = new Response();
                    responseToDelete.setIdResponse(idSubmission);

                    rs.delete(responseToDelete);
                    break;

                case 9:
                    // Exit
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}