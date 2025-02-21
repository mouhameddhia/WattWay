package tn.esprit.services;

import tn.esprit.entities.Bill;
import tn.esprit.entities.Car;
import tn.esprit.utils.Wattway;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//TESTING GIT
public class BillServices implements Iservice<Bill>{
    Connection conn = Wattway.getInstance().getConn();

    @Override
    public void add(Bill bill) throws SQLException {
        String query="INSERT INTO bill (dateBill,totalAmountBill, statusBill,idCar,idClient) VALUES ('"+ java.sql.Date.valueOf(bill.getDateBill())+"'," +
                "'"+bill.getTotalAmountBill()+"','"+bill.getStatusBill()+"','"+bill.getIdCar()+"','"+bill.getIdClient()+"')";
        Statement stmt=conn.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Bill added successfully");
    }

    @Override
    public List<Bill> retrieve() throws SQLException {
        List<Bill> bills=new ArrayList<Bill>();
        String query="SELECT * FROM bill";
        Statement stmt=conn.createStatement();
        ResultSet rs=stmt.executeQuery(query);
        while(rs.next()){
            Bill bill = new Bill();
            bill.setIdBill(rs.getInt("idBill"));
            bill.setDateBill(rs.getDate("dateBill").toLocalDate());
            bill.setTotalAmountBill(rs.getFloat("totalAmountBill"));
            bill.setIdCar(rs.getInt("idCar"));
            bill.setStatusBill(rs.getInt("statusBill"));
            bills.add(bill);
        }
        return bills;
    }
    public List<Bill> billsByUser(int userId) throws SQLException {
        List<Bill> bills=new ArrayList<>();
        String query="SELECT * FROM bill WHERE idClient=? AND statusBill=0";
        PreparedStatement stmt=conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            Bill bill=new Bill();
            bill.setIdBill(rs.getInt("idBill"));
            bill.setDateBill(rs.getDate("dateBill").toLocalDate());
            bill.setTotalAmountBill(rs.getFloat("totalAmountBill"));
            bill.setIdCar(rs.getInt("idCar"));
            bill.setStatusBill(rs.getInt("statusBill"));
            bills.add(bill);
        }
        return bills;
    }
    public int numberBillsByUser(int userId) throws SQLException {
        int i=0;
        String query= "SELECT * FROM bill WHERE idClient=? AND statusBill=0";
        PreparedStatement stmt=conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs=stmt.executeQuery();
        while(rs.next()){
            i++;
        }
        return i;
    }
    @Override
    public void update(Bill bill) throws SQLException {
        String query="UPDATE bill SET dateBill=?,totalAmountBill=?,idCar=?,statusBill=? WHERE idBill=?";
        PreparedStatement prstmt=conn.prepareStatement(query);
        prstmt.setDate(1, java.sql.Date.valueOf(bill.getDateBill()));
        prstmt.setFloat(2, bill.getTotalAmountBill());
        prstmt.setInt(3, bill.getIdCar());
        prstmt.setInt(4, bill.getStatusBill());
        prstmt.setInt(5, bill.getIdBill());
        prstmt.executeUpdate();
        System.out.println("Bill updated successfully");
    }

    @Override
    public void delete(int i) throws SQLException {
        String query="DELETE FROM bill WHERE idBill=?";
        PreparedStatement prstmt=conn.prepareStatement(query);
        prstmt.setInt(1, i);
        prstmt.executeUpdate();
        System.out.println("Bill deleted successfully");
    }
    public String getUserNameByBillId(int idBill) throws SQLException {
        String query="SELECT nameClient FROM client u JOIN bill b ON u.idClient = b.idClient WHERE b.idBill = ?";
        PreparedStatement prstmt=conn.prepareStatement(query);
        prstmt.setInt(1, idBill);
        ResultSet rs=prstmt.executeQuery();
        if(rs.next()){
            return rs.getString("nameClient");
        }
        return "";
    }
    public Car getCarByBillId(int idBill) throws SQLException {
        Car car=new Car();
        String query="SELECT * FROM car c, bill b WHERE c.idCar=b.idCar and idBill=?";
        PreparedStatement prstmt=conn.prepareStatement(query);
        prstmt.setInt(1, idBill);
        ResultSet rs=prstmt.executeQuery();
        while(rs.next()){
            car.setIdCar(rs.getInt("idCar"));
            car.setBrandCar(rs.getString("brandCar"));
            car.setModelCar(rs.getString("modelCar"));
            car.setYearCar(rs.getInt("yearCar"));
            car.setKilometrageCar(rs.getInt("kilometrageCar"));
            car.setStatusCar(rs.getString("statusCar"));
            car.setPriceCar(rs.getFloat("priceCar"));
            car.setImgCar(rs.getString("imgCar"));
            car.setIdWarehouse(rs.getInt("idWarehouse"));
        }
        return car;
    }
    public void payBill(int idBill)throws SQLException{
        String query="UPDATE bill SET statusBill=1 WHERE idBill=?";
        PreparedStatement prstmt=conn.prepareStatement(query);
        prstmt.setInt(1, idBill);
        prstmt.executeUpdate();
    }
}
