package tn.esprit.entities;

public class Car {
    private int idCar;
    private String modelCar;
    private String brandCar;
    private int yearCar;
    private float priceCar;
    private String statusCar;
    private float kilometrageCar;
    private int idWarehouse;
    private String imgCar;


    public Car() {}

    public Car(int idCar, String modelCar, String brandCar, int yearCar, float priceCar,
               String statusCar, float kilometrageCar, int idWarehouse, String imgCar) {
        this.idCar = idCar;
        this.modelCar = modelCar;
        this.brandCar = brandCar;
        this.yearCar = yearCar;
        this.priceCar = priceCar;
        this.statusCar = statusCar;
        this.kilometrageCar = kilometrageCar;
        this.idWarehouse = idWarehouse;
        this.imgCar = imgCar;
    }

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

    public void setStatusCar(String statusCar) {
        this.statusCar = statusCar;
    }

    public float getKilometrageCar() {
        return kilometrageCar;
    }

    public void setKilometrageCar(float kilometrageCar) {
        this.kilometrageCar = kilometrageCar;
    }

    public int getIdWarehouse() {
        return idWarehouse;
    }

    public void setIdWarehouse(int idWarehouse) {
        this.idWarehouse = idWarehouse;
    }

    public String getImgCar() {
        return imgCar;
    }

    public void setImgCar(String imgCar) {
        this.imgCar = imgCar;
    }

    public String getCarDisplayName() {
        return modelCar + " (" + brandCar + ", " + yearCar + ")";
    }

    @Override
    public String toString() {
        return getCarDisplayName();
    }
}
