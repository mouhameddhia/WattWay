package tn.esprit.services;

import tn.esprit.entities.Response;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResponseServices implements IService<Response> {

    private Connection conn;

    public ResponseServices() {
        conn = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Response response) throws SQLException {
        String query = "INSERT INTO `response` (`idResponse`, `message`, `dateResponse`, `typeResponse`, `idUser`, `idSubmission`) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, response.getIdResponse());
            pstmt.setString(2, response.getMessage());
            pstmt.setDate(3, response.getDateResponse());
            pstmt.setString(4, response.getTypeResponse().name());
            pstmt.setInt(5, response.getIdUser());
            pstmt.setInt(6, response.getIdSubmission());

            pstmt.executeUpdate();
            System.out.println("Response added");
        }
    }

    @Override
    public void addC(Response response) {
        // Implement if needed
    }

    @Override
    public void delete(Response response) {
        try {
            // Check if the ID exists
            String checkQuery = "SELECT COUNT(*) FROM response WHERE idResponse = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, response.getIdResponse());

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        System.out.println("❌ Response ID " + response.getIdResponse() + " does not exist in the database!");
                        return;
                    }
                }
            }

            // Delete query
            String query = "DELETE FROM response WHERE idResponse = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, response.getIdResponse());

                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("✅ Response ID " + response.getIdResponse() + " deleted successfully.");
                } else {
                    System.out.println("⚠ No response found with ID " + response.getIdResponse());
                }
            }



        } catch (SQLException e) {
            System.out.println("❌ Error while deleting response: " + e.getMessage());
        }
    }

    @Override
    public void update(Response response) {
        String query = "UPDATE response SET message = ?, dateResponse = ?, typeResponse = ?, idUser = ?, idSubmission = ? WHERE idResponse = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, response.getMessage());
            pstmt.setDate(2, response.getDateResponse());
            pstmt.setString(3, String.valueOf(response.getTypeResponse()));
            pstmt.setInt(4, response.getIdUser());
            pstmt.setInt(5, response.getIdSubmission());
            pstmt.setInt(6, response.getIdResponse());

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Response ID " + response.getIdResponse() + " updated successfully.");
            } else {
                System.out.println("⚠ No response found with ID " + response.getIdResponse());
            }
        } catch (SQLException e) {
            System.out.println("❌ Error while updating response: " + e.getMessage());
        }
    }

    @Override
    public List<Response> returnList() throws SQLException {
        List<Response> responses = new ArrayList<>();
        String query = "SELECT * FROM `response`";

        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            // Convert typeResponse string to TYPERESPONSE enum
            String typeResponseStr = rs.getString("typeResponse").toUpperCase();
            Response.TYPERESPONSE typeResponse = null;
            try {
                typeResponse = Response.TYPERESPONSE.valueOf(typeResponseStr);
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ Unknown typeResponse value: " + typeResponseStr + ". Assigning default type: INFO");
            }

            // Create Response object using the constructor
            Response response = new Response(
                    rs.getString("message"),
                    rs.getDate("dateResponse"),
                    typeResponse,
                    rs.getInt("idUser"),
                    rs.getInt("idSubmission")
            );

            response.setIdResponse(rs.getInt("idResponse")); // Set the ID separately
            responses.add(response);
        }

        return responses;
    }

    public List<Response> findResponsesBySubmissionId(int submissionId) throws SQLException {
        String query = "SELECT * FROM responses WHERE submission_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, submissionId);
        ResultSet rs = ps.executeQuery();
        List<Response> responses = new ArrayList<>();

        while (rs.next()) {
            Response response = new Response();
            response.setIdSubmission(rs.getInt("idSubmission"));
            response.setMessage(rs.getString("message"));
            response.setDateResponse(rs.getDate("date_response"));
            response.setTypeResponse(Response.TYPERESPONSE.valueOf(rs.getString("typeResponse")));
            responses.add(response);
        }
        return responses;
    }


}