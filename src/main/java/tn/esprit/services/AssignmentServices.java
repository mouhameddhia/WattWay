package tn.esprit.services;

import tn.esprit.entities.Assignment;
import tn.esprit.utilities.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentServices implements IService<Assignment> {
    private Connection conn;

    public AssignmentServices() {
        conn = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void add(Assignment assignment) throws SQLException {
        String query = "INSERT INTO `assignment`(`descriptionAssignment`, `statusAssignment`, `idUser`, `idMechanic`) VALUES ('"
                + assignment.getDescriptionAssignment() + "','"
                + assignment.getStatusAssignment() + "',"
                + assignment.getIdUser() + ","
                + assignment.getIdMechanic() + ")";
        Statement stm = conn.createStatement();
        stm.executeUpdate(query);
        System.out.println("Assignment added");
    }

    @Override
    public void addP(Assignment assignment) throws SQLException {
        String query = "INSERT INTO `assignment`(`descriptionAssignment`, `statusAssignment`, `idUser`, `idMechanic`) VALUES (?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, assignment.getDescriptionAssignment());
        ps.setString(2, assignment.getStatusAssignment());
        ps.setInt(3, assignment.getIdUser());
        ps.setInt(4, assignment.getIdMechanic());
        ps.executeUpdate();
        System.out.println("Assignment added");
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
        String query = "UPDATE assignment SET descriptionAssignment = ?, statusAssignment = ?, idUser = ?, idMechanic = ? WHERE idAssignment = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, assignment.getDescriptionAssignment());
        ps.setString(2, assignment.getStatusAssignment());
        ps.setInt(3, assignment.getIdUser());
        ps.setInt(4, assignment.getIdMechanic());
        ps.setInt(5, assignment.getIdAssignment());
        ps.executeUpdate();
        System.out.println("Assignment updated");
    }

    @Override
    public List<Assignment> returnList() throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        String query = "SELECT * FROM assignment";
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            Assignment assignment = new Assignment();
            assignment.setIdAssignment(rs.getInt("idAssignment"));
            assignment.setDescriptionAssignment(rs.getString("descriptionAssignment"));
            assignment.setStatusAssignment(rs.getString("statusAssignment"));
            assignment.setIdUser(rs.getInt("idUser"));
            assignment.setIdMechanic(rs.getInt("idMechanic"));
            assignments.add(assignment);
        }

        return assignments;
    }
}
