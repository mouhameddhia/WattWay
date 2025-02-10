package tn.esprit.services;


import tn.esprit.entities.Car;
import tn.esprit.entities.Warehouse;
import tn.esprit.utils.Wattway;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseServices implements Iservice<Warehouse> {
    private Connection conn;
    Warehouse warehouse;

    //CONSTRUCTORS
    public WarehouseServices(){
        conn= Wattway.getInstance().getConn();
    }
    //CRUD


    @Override
    public void add(Warehouse warehouse) throws SQLException {
        String query="INSERT INTO `warehouse`(`city`, `street`, `postalCode`, `capacityWarehouse`) VALUES ('"+warehouse.getCity()+"','"+warehouse.getStreet()+"'" +
                ",'"+warehouse.getPostalCode()+"','"+warehouse.getCapacityWarehouse()+"')";
        Statement stmt=conn.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Warehouse has been successfully added");
    }

    @Override
    public void update(Warehouse warehouse) throws SQLException {
        String query="UPDATE `warehouse` SET `city`='"+warehouse.getCity()+"'," +
                "`street`='"+warehouse.getStreet()+"',`postalCode`='"+warehouse.getPostalCode()+"'" +
                ",`capacityWarehouse`='"+warehouse.getCapacityWarehouse()+"' WHERE idWarehouse = "+warehouse.getIdWarehouse()+"";
        Statement stmt=conn.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Warehouse has been successfully updated");
    }

    @Override
    public void delete(int idWarehouse) throws SQLException {
        String query="DELETE FROM `warehouse` WHERE `idWarehouse` = ?";
        PreparedStatement prstmt = conn.prepareStatement(query);
        prstmt.setInt(1, idWarehouse);
        prstmt.executeUpdate();
        System.out.println("Warehouse has been successfully deleted");
    }
    public List<Warehouse> retrieve() throws SQLException {
        List<Warehouse> warehouses = new ArrayList<>();
        String query= "SELECT * FROM `warehouse`";
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(query);
        while(res.next()){
            Warehouse warehouse=new Warehouse();
            warehouse.setIdWarehouse(res.getInt("idWarehouse"));
            warehouse.setCity(res.getString("city"));
            warehouse.setStreet(res.getString("street"));
            warehouse.setPostalCode(res.getInt("postalCode"));
            warehouse.setCapacityWarehouse(res.getInt("capacityWarehouse"));
            warehouses.add(warehouse);
        }
        return warehouses;
    }

    // OPERATIONAL METHODS
    public boolean checkCar(Car car){
        return warehouse.getCars().contains(car);
    }
    public void addCar(Car car){
        if(!checkCar(car)){
            warehouse.getCars().add(car);
        }
    }
    public void removeCar(int warehouseId, Car car){
        if (checkCar(car)){
            warehouse.getCars().remove(car);
        }
    }
}
