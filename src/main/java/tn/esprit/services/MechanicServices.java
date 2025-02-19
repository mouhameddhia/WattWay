package tn.esprit.services;

import tn.esprit.entities.Mechanic;
import tn.esprit.utilities.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MechanicServices implements IService<Mechanic>{
    private Connection conn;

    public MechanicServices() {
        conn = MyDatabase.getInstance().getConnection();
    }
    @Override
    public void add(Mechanic mechanic) throws SQLException {
        String query = "INSERT INTO `mechanic`(`nameMechanic`, `specialityMechanic`) VALUES ('"+mechanic.getNameMechanic()+"','"+mechanic.getSpecialityMechanic()+"')";
        Statement stm = conn.createStatement();
        stm.executeUpdate(query);
        System.out.println("Mechanic added");
    }

    @Override
    public void addP(Mechanic mechanic) throws SQLException{
        String query = "INSERT INTO `mechanic`(`nameMechanic`, `specialityMechanic`) VALUES (?,?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1,mechanic.getNameMechanic());
        ps.setString(2,mechanic.getSpecialityMechanic());
        ps.executeUpdate();
        System.out.println("Mechanic added");
    }

    @Override
    public void delete(Mechanic mechanic) throws SQLException {
        String query = "DELETE FROM mechanic WHERE idMechanic = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, mechanic.getIdMechanic());
        ps.executeUpdate();
        System.out.println("Mechanic deleted");
    }

    @Override
    public void update(Mechanic mechanic) throws SQLException {
        String query = "UPDATE mechanic SET nameMechanic = ?, specialityMechanic = ? WHERE idMechanic = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, mechanic.getNameMechanic());
        ps.setString(2, mechanic.getSpecialityMechanic());
        ps.setInt(3, mechanic.getIdMechanic());
        ps.executeUpdate();
        System.out.println("Mechanic updated");
    }

    @Override
    public List<Mechanic> returnList() throws SQLException {
        List<Mechanic> mechanics = new ArrayList<>();
        String query = "SELECT * FROM mechanic";
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            Mechanic mechanic = new Mechanic();
            mechanic.setIdMechanic(rs.getInt("idMechanic"));
            mechanic.setNameMechanic(rs.getString("nameMechanic"));
            mechanic.setSpecialityMechanic(rs.getString("specialityMechanic"));
            mechanics.add(mechanic);
        }

        return mechanics;
    }
}
