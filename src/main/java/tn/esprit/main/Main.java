package tn.esprit.main;

import tn.esprit.entities.Bill;
import tn.esprit.entities.Car;
import tn.esprit.entities.Warehouse;
import tn.esprit.services.BillServices;
import tn.esprit.services.CarServices;
import tn.esprit.services.WarehouseServices;
import tn.esprit.utils.Wattway;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //WAREHOUSE
        Warehouse w = new Warehouse("Nabeul","Rue Ghana",3010,50);
        WarehouseServices ws = new WarehouseServices();
        //CREATE
//        try {
//            ws.add(w);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
        //RETRIEVE
//        try {
//            List<Warehouse> warehouses = ws.retrieve();
//            System.out.println(warehouses);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
        //UPDATE

//        try {
//
//            w.setIdWarehouse(6);
//            ws.update(w);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
        //DELETE
//        try {
//
//            w.setIdWarehouse(3);
//            ws.delete(w.getIdWarehouse());
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }


        //CAR
        Car c = new Car("Golf EV","Volkswagen",2022,85000,"available",0);
        CarServices cs = new CarServices();
        //CREATE
//        try {
//            cs.add(c);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }

        //RETRIEVE

//        try {
//            List<Car> cars= new ArrayList<>();
//            cars= cs.retrieve();
//            System.out.println(cars);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }

        // UPDATE

//        try {
//            c.setIdCar(2);
//            cs.update(c);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//
        // DELETE
//
//        try {
//            c.setIdCar(2);
//            cs.delete(c.getIdCar());
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
        //BILL
        //CREATE
        c.setIdCar(2);
        Bill b = new Bill(c);

        BillServices bs = new BillServices();
//        try {
//            bs.add(b);
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
        //RETRIEVE
        try {
            List<Bill> bills=bs.retrieve();
            System.out.println(bills);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //UPDATE

//        try {
//            b.setIdBill(4);
//            bs.update(b);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

        //DELETE
//        try {
//            b.setIdBill(1);
//            bs.delete(b.getIdBill());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

}
