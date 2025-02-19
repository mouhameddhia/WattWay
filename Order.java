package entities;

import java.util.List;

public class Order {

    private int idOrder;
    private String supplierOrder, dateOrder, statusOrder;
    private double totalAmountOrder;
    private List<Item> items; // Holds the list of items in the order

    // Constructor to initialize all attributes, including items and totalAmount
    public Order(int idOrder, String supplierOrder, String dateOrder, String statusOrder, List<Item> items) {
        this.idOrder = idOrder;
        this.supplierOrder = supplierOrder;
        this.dateOrder = dateOrder;
        this.statusOrder = statusOrder;
        this.items = items;
        this.totalAmountOrder = calculateTotalAmount(); // Calculates the total amount upon creation
    }

    // Constructor without idOrder, useful for creating new orders without setting ID manually
    public Order(String supplierOrder, String dateOrder, String statusOrder, List<Item> items) {
        this.supplierOrder = supplierOrder;
        this.dateOrder = dateOrder;
        this.statusOrder = statusOrder;
        this.items = items;
        this.totalAmountOrder = calculateTotalAmount(); // Calculates the total amount upon creation
    }

    // Default constructor for creating an empty order (if necessary)
    public Order() {}

    // Method to calculate the total amount based on item prices and quantities
    private double calculateTotalAmount() {
        double total = 0;
        for (Item item : items) {
            total += item.getPricePerUnitItem() * item.getQuantityItem(); // Price per unit multiplied by quantity
        }
        return total;
    }

    // Getter and setter methods for all attributes
    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public String getSupplierOrder() {
        return supplierOrder;
    }

    public void setSupplierOrder(String supplierOrder) {
        this.supplierOrder = supplierOrder;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(String statusOrder) {
        this.statusOrder = statusOrder;
    }

    public double getTotalAmountOrder() {
        return totalAmountOrder;
    }

    public void setTotalAmountOrder(double totalAmountOrder) {
        this.totalAmountOrder = totalAmountOrder;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        this.totalAmountOrder = calculateTotalAmount(); // Recalculate total if items change
    }

    // Override toString method to represent the Order as a string
    @Override
    public String toString() {
        return "Order{" +
                "id=" + idOrder +
                ", supplier='" + supplierOrder + '\'' +
                ", date='" + dateOrder + '\'' +
                ", status='" + statusOrder + '\'' +
                ", totalAmount=" + totalAmountOrder +
                ", items=" + items +
                '}';
    }
}
