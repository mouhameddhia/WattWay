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
        String query="INSERT INTO bill (dateBill,totalAmountBill,idCar) VALUES ('"+ java.sql.Date.valueOf(bill.getDateBill())+"'," +
                "'"+bill.getTotalAmountBill()+"','"+bill.getIdCar()+"')";
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
            bills.add(bill);
        }
        return bills;
    }

    @Override
    public void update(Bill bill) throws SQLException {
        String query="UPDATE bill SET dateBill=?,totalAmountBill=?,idCar=? WHERE idBill=?";
        PreparedStatement prstmt=conn.prepareStatement(query);
        prstmt.setDate(1, java.sql.Date.valueOf(bill.getDateBill()));
        prstmt.setFloat(2, bill.getTotalAmountBill());
        prstmt.setInt(3, bill.getIdCar());
        prstmt.setInt(4, bill.getIdBill());
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
}
