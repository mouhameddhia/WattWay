package tn.esprit.entities;
import java.util.*;
public class Car {
    private int idCar;
    private String modelCar;
    private String brandCar;
    private int yearCar;
    private float priceCar;
    private String statusCar;
    private int kilometrageCar;
    private int idWarehouse;

    //CONSTRUCTORS

    public Car() {}
    public Car(String modelCar, String brandCar, int yearCar, float priceCar, String statusCar, int kilometrageCar) {

        this.modelCar = modelCar;
        this.brandCar = brandCar;
        this.yearCar = yearCar;
        this.priceCar = priceCar;
        setStatusCar(statusCar);
        this.kilometrageCar = kilometrageCar;
    }

    //GETTERS & SETTERS

    public int getIdCar() {
        return idCar;
    }

    public void setIdCar(int idCar) {
        this.idCar = idCar;
    }

    public String getModelCar() {
        return modelCar;
    }

    public void setModelCar(String modelCar) {
        this.modelCar = modelCar;
    }

    public String getBrandCar() {
        return brandCar;
    }

    public void setBrandCar(String brandCar) {
        this.brandCar = brandCar;
    }

    public int getYearCar() {
        return yearCar;
    }

    public void setYearCar(int yearCar) {
        this.yearCar = yearCar;
    }

    public float getPriceCar() {
        return priceCar;
    }

    public void setPriceCar(float priceCar) {
        this.priceCar = priceCar;
    }

    public String getStatusCar() {
        return statusCar;
    }

    public int getKilometrageCar() {
        return kilometrageCar;
    }

    public void setKilometrageCar(int kilometrageCar) {
        this.kilometrageCar = kilometrageCar;
    }

    public int getIdWarehouse() {
        return idWarehouse;
    }
    public void setIdWarehouse(int idWarehouse) {
        this.idWarehouse = idWarehouse;
    }
    public void setStatusCar(String statusCar) {
        if (statusCar.equals("available") || statusCar.equals("under repair") || statusCar.equals("rented")) {
            this.statusCar = statusCar;
        } else {
            throw new IllegalArgumentException("Status must be one of these (available, under repair, rented)");
        }
    }
    // OVERRIDES

    @Override
    public String toString() {
        return " Car " +
                idCar + "\n" +
                " Model : " + modelCar + "\n" +
                " Brand : " + brandCar + "\n" +
                " Year : " + yearCar + "\n" +
                " Price : " + priceCar + "\n" +
                " Car status : " + statusCar + "\n" +
                " Car kilometrage : " + kilometrageCar + "\n" +
                " Inside warehouse : " + idWarehouse + "\n" ;
    }
}
