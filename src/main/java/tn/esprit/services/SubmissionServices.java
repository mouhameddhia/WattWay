package tn.esprit.services;


import tn.esprit.entities.Response;
import tn.esprit.entities.Submission;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubmissionServices implements IService<Submission> {

    private Connection conn;

    public SubmissionServices() {
        conn = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Submission submission) throws SQLException {
        String query = "INSERT INTO `submission` (`idSubmission`, `description`, `status`, `urgencyLevel`, `dateSubmission`, `idCar`, `idUser`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, submission.getIdSubmission());
            pstmt.setString(2, submission.getDescription());
            pstmt.setString(3, submission.getStatus().name()); // Use .name() for enum
            pstmt.setString(4, submission.getUrgencyLevel().name()); // Use .name() for enum
            pstmt.setDate(5, submission.getDateSubmission());
            pstmt.setInt(6, submission.getIdCar());
            pstmt.setInt(7, submission.getIdUser());
            pstmt.executeUpdate();
            System.out.println("Submission added");
        }
    }

    @Override
    public void addC(Submission submission) {
        // Implementation if needed
    }

    @Override
    public void delete(Submission submission) {
        try {
            String deleteResponsesQuery = "DELETE FROM response WHERE idSubmission = ?";
            try (PreparedStatement deleteResponsesStmt = conn.prepareStatement(deleteResponsesQuery)) {
                deleteResponsesStmt.setInt(1, submission.getIdSubmission());
                deleteResponsesStmt.executeUpdate();
            }

            String query = "DELETE FROM submission WHERE idSubmission = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, submission.getIdSubmission());
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("✅ Submission ID " + submission.getIdSubmission() + " deleted successfully.");
                } else {
                    System.out.println("⚠ No submission found with ID " + submission.getIdSubmission());
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error while deleting submission: " + e.getMessage());
        }
    }

    @Override
    public void update(Submission submission) {
        String query = "UPDATE submission SET description = ?, status = ?, urgencyLevel = ?, dateSubmission = ? WHERE idSubmission = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, submission.getDescription());
            pstmt.setString(2, submission.getStatus().name()); // Use .name() for enum
            pstmt.setString(3, submission.getUrgencyLevel().name()); // Use .name() for enum
            pstmt.setDate(4, submission.getDateSubmission());
            pstmt.setInt(5, submission.getIdSubmission());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Submission ID " + submission.getIdSubmission() + " updated successfully.");
            } else {
                System.out.println("⚠ No submission found with ID " + submission.getIdSubmission());
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while updating submission: " + e.getMessage());
        }
    }

    @Override
    public List<Submission> returnList() throws SQLException {
        List<Submission> submissions = new ArrayList<>();
        String query = "SELECT * FROM `submission`";

        try (Statement stm = conn.createStatement(); ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                // Convert status string to STATUS enum
                Submission.STATUS status;
                try {
                    status = Submission.STATUS.valueOf(rs.getString("status").toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠ Unknown status value: " + rs.getString("status") + ". Assigning default status: Pending");
                    status = Submission.STATUS.PENDING; // Default value
                }

                // Convert urgencyLevel string to URGENCYLEVEL enum
                Submission.URGENCYLEVEL urgencyLevel;
                try {
                    urgencyLevel = Submission.URGENCYLEVEL.valueOf(rs.getString("urgencyLevel").toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠ Unknown urgencyLevel value: " + rs.getString("urgencyLevel") + ". Assigning default urgency: Low");
                    urgencyLevel = Submission.URGENCYLEVEL.LOW; // Default value
                }

                // Create Submission object
                Submission submission = new Submission(
                        rs.getInt("idSubmission"),
                        rs.getString("description"),
                        status,
                        urgencyLevel,
                        rs.getDate("dateSubmission"),
                        rs.getInt("idCar"),
                        rs.getInt("idUser")
                );
                submissions.add(submission);
            }
        }
        return submissions;
    }

    // New functions
    public List<Integer> getAllSubmissionIds() {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT idSubmission FROM submission";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ids.add(rs.getInt("idSubmission"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while retrieving submission IDs: " + e.getMessage());
        }
        return ids;
    }

    public Submission getSubmissionById(int id) {
        Submission submission = null;
        String query = "SELECT * FROM submission WHERE idSubmission = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    submission = new Submission(
                            rs.getInt("idSubmission"),
                            rs.getString("description"),
                            Submission.STATUS.valueOf(rs.getString("status").toUpperCase()),
                            Submission.URGENCYLEVEL.valueOf(rs.getString("urgencyLevel").toUpperCase()),
                            rs.getDate("dateSubmission"),
                            rs.getInt("idCar"),
                            rs.getInt("idUser")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while retrieving submission: " + e.getMessage());
        }
        return submission;
    }


    public List<Integer> getAllSubmissionIds(int userId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT id_submission FROM submission WHERE idUFFser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("idSubmission"));
            }
        }
        return ids;
    }


    public List<Submission> getSubmissionsByUserId(int userId) throws SQLException {
        List<Submission> submissions = new ArrayList<>();
        String query = "SELECT * FROM submission WHERE idUser = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Submission submission = new Submission();
                    submission.setIdSubmission(rs.getInt("idSubmission"));
                    submission.setDescription(rs.getString("description"));
                    submission.setStatus(Submission.STATUS.valueOf(rs.getString("status")));
                    submission.setUrgencyLevel(Submission.URGENCYLEVEL.valueOf(rs.getString("urgencyLevel")));
                    submission.setDateSubmission(rs.getDate("dateSubmission"));
                    submission.setIdCar(rs.getInt("idCar"));
                    submission.setIdUser(rs.getInt("idUser"));

                    submissions.add(submission);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching submissions: " + e.getMessage());
            throw e;
        }
        return submissions;
    }

    public List<Response> getResponsesBySubmissionId(int submissionId) throws SQLException {
        List<Response> responses = new ArrayList<>();
        String sql = "SELECT * FROM response WHERE idSubmission = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Response response = new Response();
                    response.setIdResponse(rs.getInt("idResponse"));
                    response.setMessage(rs.getString("message"));
                    response.setDateResponse(rs.getDate("dateResponse"));

                    responses.add(response);
                }
            }
        }
        return responses;
    }


}