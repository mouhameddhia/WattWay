package tn.esprit.services;

import tn.esprit.entities.Assignment;
import tn.esprit.entities.Mechanic;
import tn.esprit.utilities.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentServices implements IService<Assignment>{
    private Connection conn;

    public AssignmentServices() {
        conn = MyDatabase.getInstance().getConnection();
    }
    @Override
    public void add(Assignment assignment) throws SQLException {
        String query = "INSERT INTO `assignment`(`descriptionAssignment`, `statusAssignment`, `idCar`) VALUES ('"
                + assignment.getDescriptionAssignment() + "','"
                + assignment.getStatusAssignment().name() + "',"
                + assignment.getIdCar() + ")";

        Statement stm = conn.createStatement();
        stm.executeUpdate(query);
        System.out.println("Assignment added successfully!");
    }

    @Override
    public void addP(Assignment assignment) throws SQLException {
        String query = "INSERT INTO `assignment`(`descriptionAssignment`, `statusAssignment`, `idCar`,`dateAssignment`) VALUES (?,?,?,?)";
        System.out.println("Executing query: " + query);
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, assignment.getDescriptionAssignment());
        ps.setString(2, assignment.getStatusAssignment().name());
        ps.setInt(3, assignment.getIdCar());
        ps.setTimestamp(4, Timestamp.valueOf(assignment.getDateAssignment()));
        ps.executeUpdate();


        // Get the generated assignment ID
        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            int assignmentId = generatedKeys.getInt(1);

            // Assign selected mechanics to the assignment
            for (Mechanic mechanic : assignment.getMechanics()) {
                assignMechanicToAssignment(assignmentId, mechanic.getIdMechanic());
            }
        }
        //schedule event in google calendar
        try {
            GoogleCalendarService.createEvent(assignment.getDescriptionAssignment(), assignment.getDateAssignment());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Adding assignment: " + assignment.getDescriptionAssignment());
    }

    public void assignMechanicToAssignment(int assignmentId, int mechanicId) throws SQLException {
        String query = "INSERT INTO assignment_mechanics (assignment_id, mechanic_id) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, assignmentId);
        ps.setInt(2, mechanicId);
        ps.executeUpdate();
    }

    @Override
    public void delete(Assignment assignment) throws SQLException {
        String query = "DELETE FROM assignment WHERE idAssignment = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, assignment.getIdAssignment());
        ps.executeUpdate();
        System.out.println("Assignment deleted");
    }

    @Override
    public void update(Assignment assignment) throws SQLException {
        // Update query to include dateAssignment
        String query = "UPDATE assignment SET descriptionAssignment = ?, statusAssignment = ?, idCar = ?, dateAssignment = ? WHERE idAssignment = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, assignment.getDescriptionAssignment());
        ps.setString(2, assignment.getStatusAssignment().name());
        ps.setInt(3, assignment.getIdCar());
        ps.setTimestamp(4, Timestamp.valueOf(assignment.getDateAssignment())); // Set dateAssignment
        ps.setInt(5, assignment.getIdAssignment());
        ps.executeUpdate();

        // Remove existing mechanic assignments for this assignment
        removeMechanicsFromAssignment(assignment.getIdAssignment());

        // Reassign mechanics
        for (Mechanic mechanic : assignment.getMechanics()) {
            assignMechanicToAssignment(assignment.getIdAssignment(), mechanic.getIdMechanic());
        }
        try {
            GoogleCalendarService.createEvent(assignment.getDescriptionAssignment(), assignment.getDateAssignment());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create/update event in Google Calendar: " + e.getMessage());
        }

        System.out.println("Assignment updated with new mechanics and date.");
        System.out.println("Update assignment: " + assignment.getDescriptionAssignment());
    }

    public void removeMechanicsFromAssignment(int assignmentId) throws SQLException {
        String query = "DELETE FROM assignment_mechanics WHERE assignment_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, assignmentId);
        ps.executeUpdate();
    }

    @Override
    public List<Assignment> returnList() throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        String query = "SELECT a.*, c.modelCar FROM assignment a " +
                "JOIN car c ON a.idCar = c.idCar";

        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            Assignment assignment = new Assignment();
            assignment.setIdAssignment(rs.getInt("idAssignment"));
            assignment.setDescriptionAssignment(rs.getString("descriptionAssignment"));

            // Handle status conversion
            String statusStr = rs.getString("statusAssignment");
            try {
                assignment.setStatusAssignment(Assignment.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                assignment.setStatusAssignment(Assignment.Status.PENDING);
                System.err.println("Warning: Invalid status '" + statusStr + "' found in DB, defaulting to PENDING.");
            }

            assignment.setIdCar(rs.getInt("idCar"));
            assignment.setCarModel(rs.getString("modelCar")); // Store car model for display

            // Retrieve and set the dateAssignment field
            Timestamp timestamp = rs.getTimestamp("dateAssignment");
            if (timestamp != null) {
                assignment.setDateAssignment(timestamp.toLocalDateTime());
            } else {
                assignment.setDateAssignment(null); // Handle null dates
            }

            // Fetch mechanics assigned to this assignment
            List<Mechanic> mechanics = getMechanicsByAssignmentId(assignment.getIdAssignment());
            assignment.setMechanics(mechanics);

            assignments.add(assignment);
        }

        return assignments;
    }

    public List<Mechanic> getMechanicsByAssignmentId(int assignmentId) throws SQLException {
        List<Mechanic> mechanics = new ArrayList<>();
        String query = "SELECT m.idMechanic, m.nameMechanic, m.specialityMechanic " +
                "FROM mechanic m " +
                "JOIN assignment_mechanics am ON m.idMechanic = am.mechanic_id " +
                "WHERE am.assignment_id = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, assignmentId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Mechanic mechanic = new Mechanic();
            mechanic.setIdMechanic(rs.getInt("idMechanic"));
            mechanic.setNameMechanic(rs.getString("nameMechanic"));

            // Convert speciality properly
            String specialityString = rs.getString("specialityMechanic").toUpperCase();
            try {
                mechanic.setSpecialityMechanic(Mechanic.Speciality.valueOf(specialityString));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid speciality found in DB: " + specialityString);
            }

            mechanics.add(mechanic);
        }

        return mechanics;
    }

    public void updateAssignmentWithMechanics(int assignmentId, List<Mechanic> mechanics) throws SQLException {
        // First, remove all existing mechanics for the assignment
        String deleteQuery = "DELETE FROM assignment_mechanics WHERE assignment_id = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
        deleteStmt.setInt(1, assignmentId);
        deleteStmt.executeUpdate();

        // Then, reassign selected mechanics
        String insertQuery = "INSERT INTO assignment_mechanics (assignment_id, mechanic_id) VALUES (?, ?)";
        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);

        for (Mechanic mechanic : mechanics) {
            insertStmt.setInt(1, assignmentId);
            insertStmt.setInt(2, mechanic.getIdMechanic());
            insertStmt.addBatch();
        }

        insertStmt.executeBatch();
        System.out.println("Assignment updated with new mechanics.");
    }
}
