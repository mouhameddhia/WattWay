package tn.esprit.entities;
import java.time.LocalDate;
public class Bill {
    private int idBill;
    private LocalDate dateBill;
    private float totalAmountBill;
    private int idCar;
    private int statusBill=0;
    private int idClient;

    //CONSTRUCTORS
    public Bill(){
        dateBill = LocalDate.now();
        statusBill=0;
        idClient=1;
    }
    public Bill(Car car) {
        this.idCar = car.getIdCar();
        dateBill = LocalDate.now();
        totalAmountBill = car.getPriceCar();
    }

    public Bill(int statusBill, int idCar, float totalAmountBill, LocalDate dateBill, int idBill) {
        this.statusBill = statusBill;
        this.idCar = idCar;
        this.totalAmountBill = totalAmountBill;
        this.dateBill = dateBill;
        this.idBill = idBill;
    }
    //GETTERS & SETTERS

    public int getIdCar() {
        return idCar;
    }

    public void setIdCar(int idCar) {
        this.idCar = idCar;
    }

    public float getTotalAmountBill() {
        return totalAmountBill;
    }

    public void setTotalAmountBill(float totalAmount) {
        this.totalAmountBill = totalAmount;
    }

    public LocalDate getDateBill() {
        return dateBill;
    }

    public void setDateBill(LocalDate dateBill) {
        this.dateBill = dateBill;
    }

    public int getIdBill() {
        return idBill;
    }

    public void setIdBill(int idBill) {
        this.idBill = idBill;
    }

    @Override
    public String toString() {
        return "Bill " + idBill + "\n" +
                " Date Issued : " + dateBill + "\n" +
                " Total Amount : " + totalAmountBill +
                " DT" +"\n" +
                " For Car with id " + idCar +"\n";
    }

    public int getStatusBill() {
        return statusBill;
    }

    public void setStatusBill(int statusBill) {
        this.statusBill = statusBill;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }
}
