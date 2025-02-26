package tn.esprit.services;

import tn.esprit.entities.Car;
import tn.esprit.utilities.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarServices  {
    private Connection conn;

    public CarServices() {
        conn = MyDatabase.getInstance().getConnection();
    }
    public List<Integer> getAllCarIds() throws SQLException {
        List<Integer> carIds = new ArrayList<>();
        String query = "SELECT idCar FROM car";
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            carIds.add(rs.getInt("idCar"));
        }

        return carIds;
    }

    public Car getCarById(int idCar) throws SQLException {
        String query = "SELECT * FROM car WHERE idCar = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idCar);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Car(
                    rs.getInt("idCar"),
                    rs.getString("modelCar"),
                    rs.getString("brandCar"),
                    rs.getInt("yearCar"),
                    rs.getFloat("priceCar"),
                    rs.getString("statusCar"),
                    //Car.Status.valueOf(rs.getString("statusCar").toUpperCase()),
                    rs.getFloat("kilometrageCar"),
                    rs.getInt("idWarehouse"),
                    rs.getString("imgCar")
            );
        }

        return null; // No car found with the given ID
    }
    public boolean isCarExists(int idCar) throws SQLException {
        String query = "SELECT COUNT(*) FROM car WHERE idCar = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idCar);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

        return false;
    }
    public List<Car> getAllCarsUnderRepair() throws SQLException {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT idCar, modelCar, brandCar, yearCar FROM car WHERE statusCar = 'under repair'";
        // 🔹 Only selecting cars that are under repair

        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(query);

        while (rs.next()) {
            Car car = new Car();
            car.setIdCar(rs.getInt("idCar"));  // ✅ ID
            car.setModelCar(rs.getString("modelCar")); // ✅ Model
            car.setBrandCar(rs.getString("brandCar")); // ✅ Brand
            car.setYearCar(rs.getInt("yearCar"));  // ✅ Year
            cars.add(car);
        }
        return cars;
    }

}
