package entities;

import javafx.beans.property.*;

public class Item {
    private final IntegerProperty idItem;
    private final StringProperty nameItem;
    private final IntegerProperty quantityItem;
    private final DoubleProperty pricePerUnitItem;
    private final StringProperty categoryItem;
    private final IntegerProperty orderId;

    // Constructor
    public Item(int idItem, String nameItem, int quantityItem, double pricePerUnitItem, String categoryItem, int orderId) {
        this.idItem = new SimpleIntegerProperty(idItem);
        this.nameItem = new SimpleStringProperty(nameItem);
        this.quantityItem = new SimpleIntegerProperty(quantityItem);
        this.pricePerUnitItem = new SimpleDoubleProperty(pricePerUnitItem);
        this.categoryItem = new SimpleStringProperty(categoryItem);
        this.orderId = new SimpleIntegerProperty(orderId);
    }

    // Property Getters (Needed for TableView binding)
    public IntegerProperty idItemProperty() { return idItem; }
    public StringProperty nameItemProperty() { return nameItem; }
    public IntegerProperty quantityItemProperty() { return quantityItem; }
    public DoubleProperty pricePerUnitItemProperty() { return pricePerUnitItem; }
    public StringProperty categoryItemProperty() { return categoryItem; }
    public IntegerProperty orderIdProperty() { return orderId; }

    // Standard Getters
    public int getIdItem() { return idItem.get(); }
    public String getNameItem() { return nameItem.get(); }
    public int getQuantityItem() { return quantityItem.get(); }
    public double getPricePerUnitItem() { return pricePerUnitItem.get(); }
    public String getCategoryItem() { return categoryItem.get(); }
    public int getOrderId() { return orderId.get(); }

    // Standard Setters
    public void setIdItem(int idItem) { this.idItem.set(idItem); }
    public void setNameItem(String nameItem) { this.nameItem.set(nameItem); }
    public void setQuantityItem(int quantityItem) { this.quantityItem.set(quantityItem); }
    public void setPricePerUnitItem(double pricePerUnitItem) { this.pricePerUnitItem.set(pricePerUnitItem); }
    public void setCategoryItem(String categoryItem) { this.categoryItem.set(categoryItem); }
    public void setOrderId(int orderId) { this.orderId.set(orderId); }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + idItem.get() +
                ", name='" + nameItem.get() + '\'' +
                ", quantity=" + quantityItem.get() +
                ", pricePerUnit=" + pricePerUnitItem.get() +
                ", category='" + categoryItem.get() + '\'' +
                ", orderId=" + orderId.get() +
                '}';
    }
}
