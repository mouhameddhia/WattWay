package tn.esprit.entities;
import java.time.LocalDate;
public class Bill {
    private int idBill;
    private LocalDate dateBill;
    private float totalAmountBill;
    private int idCar;

    //CONSTRUCTORS
    public Bill(){}
    public Bill(Car car) {
        this.idCar = car.getIdCar();
        dateBill = LocalDate.now();
        totalAmountBill = car.getPriceCar();
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
                " Total Amount : " + totalAmountBill +"\n" +
                " For Car with id " + idCar +"\n";
    }
}
