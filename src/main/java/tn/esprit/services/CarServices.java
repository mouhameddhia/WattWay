package tn.esprit.services;

import tn.esprit.entities.Car;
import tn.esprit.utils.Wattway;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarServices implements Iservice<Car>{
    public CarServices() {}
    Connection conn= Wattway.getInstance().getConn();
    @Override
    public List<Car> retrieve() throws SQLException {
       List<Car> cars = new ArrayList<Car>();
       String sql = "select * from car";
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(sql);
       while (rs.next()) {
           Car car = new Car();
           car.setIdCar(rs.getInt("idCar"));
           car.setModelCar(rs.getString("modelCar"));
           car.setBrandCar(rs.getString("brandCar"));
           car.setYearCar(rs.getInt("yearCar"));
           car.setPriceCar(rs.getFloat("priceCar"));
           car.setKilometrageCar(rs.getInt("kilometrageCar"));
           car.setStatusCar(rs.getString("statusCar"));
           car.setIdWarehouse(rs.getInt("idWarehouse"));
           car.setImgCar(rs.getString("imgCar"));
           cars.add(car);
       }
       return cars;

    }
    public List<Car> retrieveAvailable() throws SQLException {
        List<Car> cars = new ArrayList<Car>();
        String sql = "select * from car WHERE statusCar = 'available'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Car car = new Car();
            car.setIdCar(rs.getInt("idCar"));
            car.setModelCar(rs.getString("modelCar"));
            car.setBrandCar(rs.getString("brandCar"));
            car.setYearCar(rs.getInt("yearCar"));
            car.setPriceCar(rs.getFloat("priceCar"));
            car.setKilometrageCar(rs.getInt("kilometrageCar"));
            car.setStatusCar(rs.getString("statusCar"));
            car.setIdWarehouse(rs.getInt("idWarehouse"));
            car.setImgCar(rs.getString("imgCar"));
            cars.add(car);
        }
        return cars;

    }

    @Override
    public void add(Car car) throws SQLException {
        String query = "INSERT INTO `car`(`modelCar`, `brandCar`, `yearCar`, `priceCar`, `statusCar`, `kilometrageCar`, `idWarehouse`,`imgCar`) " +
                "VALUES ('"+ car.getModelCar()+"','"+car.getBrandCar()+"','"+car.getYearCar()+"','"+car.getPriceCar()+"'" +
                ",'"+car.getStatusCar()+"','"+car.getKilometrageCar()+"','"+car.getIdWarehouse()+"','"+car.getImgCar()+"')";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Car added successfully");

    }

    @Override
    public void update(Car car) throws SQLException {
        String query="UPDATE `car` SET `modelCar`= ?,`brandCar`= ?,`yearCar`=?," +
                "`priceCar`= ?,`statusCar`= ? ,`kilometrageCar`=? , `idWarehouse`=?,`imgCar`=? WHERE idCar = ?";
        PreparedStatement prstmt= conn.prepareStatement(query);
        prstmt.setString(1, car.getModelCar());
        prstmt.setString(2, car.getBrandCar());
        prstmt.setInt(3, car.getYearCar());
        prstmt.setFloat(4, car.getPriceCar());
        prstmt.setString(5, car.getStatusCar());
        prstmt.setInt(6, car.getKilometrageCar());
        prstmt.setInt(9, car.getIdCar());
        prstmt.setString(8, car.getImgCar());
        prstmt.setInt(7, car.getIdWarehouse());
        prstmt.executeUpdate();
        System.out.println("Car updated successfully");

    }

    @Override
    public void delete(int idCar) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM `car` WHERE idCar = '" + idCar + "'");
        System.out.println("Car deleted successfully");
    }
    public Car showCarById(int id) throws SQLException{
        Car car = new Car();
        String query="SELECT* FROM car WHERE idCar=?";
        PreparedStatement prsmt = conn.prepareStatement(query);
        ResultSet rs= prsmt.executeQuery();
        while (rs.next()) {
            car.setIdCar(rs.getInt("idCar"));
            car.setModelCar(rs.getString("modelCar"));
            car.setBrandCar(rs.getString("brandCar"));
            car.setYearCar(rs.getInt("yearCar"));
            car.setPriceCar(rs.getFloat("priceCar"));
            car.setStatusCar(rs.getString("statusCar"));
            car.setKilometrageCar(rs.getInt("kilometrageCar"));
            car.setIdWarehouse(rs.getInt("idWarehouse"));
            car.setImgCar(rs.getString("imgCar"));
        }
        return car;
    }
    public void updateStatusCar(String statusCar , int idCar) throws SQLException {
        String query = "UPDATE car SET statusCar=? WHERE idCar=?";
        PreparedStatement prsmt = conn.prepareStatement(query);
        prsmt.setString(1, statusCar);
        prsmt.setInt(2, idCar);
        prsmt.executeUpdate();
    }
}
