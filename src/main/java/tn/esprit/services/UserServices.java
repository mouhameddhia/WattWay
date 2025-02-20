package tn.esprit.services;

import tn.esprit.utilities.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServices {
    private final Connection conn;

    public UserServices() {
        conn = MyDatabase.getInstance().getConnection();
    }

    public List<Integer> getAllUserIds() throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        String query = "SELECT idUser FROM user"; // Ensure table name is correct

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                userIds.add(rs.getInt("idUser"));
            }
        }

        return userIds;
    }
    public boolean isUserExists(int idUser) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE idUser = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
