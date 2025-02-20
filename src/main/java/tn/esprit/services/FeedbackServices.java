package tn.esprit.services;

import tn.esprit.entities.Client;
import tn.esprit.entities.Feedback;
import tn.esprit.entities.User;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackServices implements IService<Feedback> {

    private Connection con;

    public FeedbackServices() {
        con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Feedback feedback) {
        String query = "INSERT INTO Feedback (contentFeedback, ratingFeedback, dateFeedback, idUser) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, feedback.getContentFeedback());
            ps.setInt(2, feedback.getRatingFeedback());
            if (feedback.getDateFeedback() != null) {
                ps.setDate(3, new java.sql.Date(feedback.getDateFeedback().getTime()));
            } else {
                ps.setDate(3, new java.sql.Date(System.currentTimeMillis())); // Use current date if NULL
            }
            ps.setInt(4, feedback.getIdUser());
            ps.executeUpdate();
            System.out.println("Feedback added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding feedback: " + e.getMessage());
        }
    }

    @Override
    public boolean updateAccount(int id, Feedback feedback) {
        String query = "UPDATE Feedback SET contentFeedback=?, ratingFeedback=?, dateFeedback=? WHERE idFeedback=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, feedback.getContentFeedback()); ///sets the first placeholder (?) with the content of the feedback
            ps.setInt(2, feedback.getRatingFeedback());
            ps.setDate(3, new java.sql.Date(feedback.getDateFeedback().getTime()));
            ps.setInt(4, id);
            int rowsUpdated = ps.executeUpdate();  ///execute the query
            if (rowsUpdated > 0) {
                System.out.println("Feedback updated successfully!");
            } else {
                System.out.println("No feedback found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error updating feedback: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM Feedback WHERE idFeedback=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Feedback deleted successfully!");
            } else {
                System.out.println("No feedback found with ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting feedback: " + e.getMessage());
        }
    }

    @Override
    public Feedback getById(int id) {
        String query = "SELECT * FROM Feedback WHERE idFeedback=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery(); /// holds the rows returned by the query.
            if (rs.next()) {  /// checks if the ResultSet contains any rows true /false
                return new Feedback(
                        rs.getInt("idFeedback"),
                        rs.getString("contentFeedback"), /// retrieves the contentFeedback value (the feedback text).
                        rs.getInt("ratingFeedback"),
                        rs.getDate("dateFeedback"),
                        rs.getInt("idUser")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving feedback: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Feedback> getAll() {
        List<Feedback> feedbackList = new ArrayList<>();
        String query = "SELECT * FROM Feedback";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                feedbackList.add(new Feedback(
                        rs.getInt("idFeedback"),
                        rs.getString("contentFeedback"),
                        rs.getInt("ratingFeedback"),
                        rs.getDate("dateFeedback"),
                        rs.getInt("idUser")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all feedback: " + e.getMessage());
        }
        return feedbackList;
    }
    @Override
    public User login(String email, String password) {
        // Empty implementation since login is not needed here
        return null;
    }






    public List<Map<String, Object>> getAllWithUserNames() {
        List<Map<String, Object>> feedbackList = new ArrayList<>();
        String query = "SELECT f.idFeedback, f.contentFeedback, f.ratingFeedback, f.dateFeedback, u.firstNameUser, u.lastNameUser " +
                "FROM feedback f " +
                "JOIN user u ON f.idUser = u.idUser";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> feedbackMap = new HashMap<>();
                feedbackMap.put("idFeedback", rs.getInt("idFeedback"));
                feedbackMap.put("contentFeedback", rs.getString("contentFeedback"));
                feedbackMap.put("ratingFeedback", rs.getInt("ratingFeedback"));
                feedbackMap.put("dateFeedback", rs.getDate("dateFeedback"));
                feedbackMap.put("userName", rs.getString("firstNameUser") + " " + rs.getString("lastNameUser"));
                feedbackList.add(feedbackMap);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving feedbacks with user names: " + e.getMessage());
        }
        return feedbackList;
    }

}






















///PreparedStatement is best used for queries with parameters (such as when you insert or select data based on user input).
/// Statement is best used for static queries that don’t need any parameters or dynamic values.