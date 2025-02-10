package tn.esprit.entities;
import java.util.*;
public class Warehouse {
    private int idWarehouse;
    private String city;
    private String street;
    private int postalCode;
    private int capacityWarehouse;
    private List<Car> cars = new ArrayList<Car>();

    // CONSTRUCTORS

    public Warehouse() {}

    public Warehouse(String city, String street, int postalCode, int capacityWarehouse) {
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
        this.capacityWarehouse = capacityWarehouse;
    }


    //GETTERS & SETTERS

    public int getIdWarehouse() {
        return idWarehouse;
    }

    public void setIdWarehouse(int idWarehouse) {
        this.idWarehouse = idWarehouse;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getCapacityWarehouse() {
        return capacityWarehouse;
    }

    public void setCapacityWarehouse(int capacityWarehouse) {
        this.capacityWarehouse = capacityWarehouse;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }
    public List<Car> getCars() {
        return cars;
    }
    //OVERRIDES


    @Override
    public String toString() {
        return "Warehouse : " + idWarehouse + "\n" +
                " City : " + city + "\n" +
                " Street : " + street + "\n" +
                " Postal code : " + postalCode + "\n" +
                " Capacity : " + capacityWarehouse + "\n";
    }
}
