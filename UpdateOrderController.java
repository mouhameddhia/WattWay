package Controllers;

import entities.Item;
import entities.Order;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import services.OrderService;
import javafx.stage.Stage;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.List;

public class UpdateOrderController {

    // FXML elements for order details and item management
    @FXML private TextField supplierField;
    @FXML private TextField dateField;
    @FXML private TextField statusField;
    @FXML private TextField totalAmountField;
    @FXML private TableView<Item> itemsTableView;
    @FXML private TableColumn<Item, String> itemNameColumn;
    @FXML private TableColumn<Item, String> itemCategoryColumn;
    @FXML private TableColumn<Item, Integer> itemQuantityColumn;
    @FXML private TableColumn<Item, Double> itemPriceColumn;
    @FXML private TableColumn<Item, Integer> itemIdColumn;
    @FXML private TextField itemNameField, itemCategoryField, itemQuantityField, itemPriceField;
    @FXML private Button deleteItemButton, updateItemButton;
    @FXML private TableColumn<Item, Void> actionColumn;

    private final ObservableList<Item> itemsList = FXCollections.observableArrayList();
    private final OrderService orderService = new OrderService();
    private Order currentOrder; // Holds the current order being updated
    private Item selectedItem; // Stores the selected item for editing

    /**
     * Initializes the controller with the provided order data.
     * @param order The order to initialize the data with.
     */
    public void initData(Order order) {
        this.currentOrder = order;
        supplierField.setText(order.getSupplierOrder());
        dateField.setText(order.getDateOrder());
        statusField.setText(order.getStatusOrder());
        totalAmountField.setText(String.valueOf(order.getTotalAmountOrder()));

        // Bind the table to itemsList and load items for the current order
        itemsTableView.setItems(itemsList);
        loadItemsForOrder(order);

        // Disable buttons initially
        deleteItemButton.setVisible(false);
        updateItemButton.setVisible(false);

        // Handle row selection for item actions
        itemsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedItem = newSelection;
                deleteItemButton.setVisible(true);
                updateItemButton.setVisible(true);
            } else {
                deleteItemButton.setVisible(false);
                updateItemButton.setVisible(false);
            }
        });
    }

    /**
     * Adds a new item to the order.
     */
    @FXML
    private void addItem() {
        try {
            String name = itemNameField.getText();
            String category = itemCategoryField.getText();
            int quantity = Integer.parseInt(itemQuantityField.getText());
            double price = Double.parseDouble(itemPriceField.getText());

            // Validate inputs
            if (name.isEmpty() || category.isEmpty()) {
                showAlert("Error", "Item name and category cannot be empty!");
                return;
            }

            // Create a new item (ID is 0 since it's not stored yet in the database)
            Item newItem = new Item(0, name, quantity, price, category, currentOrder.getIdOrder());

            // Add to the list and update the table
            itemsList.add(newItem);
            itemsTableView.refresh(); // Ensure UI updates

            // Clear input fields
            clearItemFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity and Price must be numbers!");
        }
    }

    /**
     * Deletes the selected item from the order.
     */
    @FXML
    private void deleteSelectedItem() {
        if (selectedItem != null) {
            // Remove the item from the list
            itemsList.remove(selectedItem);

            // If the item exists in the database, delete it from there
            if (selectedItem.getIdItem() != 0) {
                orderService.deleteItem(selectedItem.getIdItem());
            }

            // Hide the buttons after deletion
            deleteItemButton.setVisible(false);
            updateItemButton.setVisible(false);
        }
    }

    /**
     * Allows editing of the selected item.
     */
    @FXML
    private void editSelectedItem() {
        if (selectedItem != null) {
            // Populate input fields with the selected item's data
            itemNameField.setText(selectedItem.getNameItem());
            itemCategoryField.setText(selectedItem.getCategoryItem());
            itemQuantityField.setText(String.valueOf(selectedItem.getQuantityItem()));
            itemPriceField.setText(String.valueOf(selectedItem.getPricePerUnitItem()));

            // Change button text to "Save Changes" and set up the save action
            updateItemButton.setText("Save Changes");
            updateItemButton.setOnAction(e -> saveItemChanges());
        }
    }

    /**
     * Saves the changes made to the selected item.
     */
    private void saveItemChanges() {
        try {
            String name = itemNameField.getText();
            String category = itemCategoryField.getText();
            int quantity = Integer.parseInt(itemQuantityField.getText());
            double price = Double.parseDouble(itemPriceField.getText());

            // Validate inputs
            if (name.isEmpty() || category.isEmpty()) {
                showAlert("Error", "Item name and category cannot be empty!");
                return;
            }

            // Update the selected item with new values
            selectedItem.setNameItem(name);
            selectedItem.setCategoryItem(category);
            selectedItem.setQuantityItem(quantity);
            selectedItem.setPricePerUnitItem(price);

            // Update the item in the database if it exists
            if (selectedItem.getIdItem() != 0) {
                try {
                    orderService.updateItem(selectedItem);
                } catch (SQLException e) {
                    showAlert("Error", "Failed to update item in database.");
                    e.printStackTrace();
                }
            }

            // Refresh table and reset buttons
            itemsTableView.refresh();
            updateItemButton.setText("Edit Item");
            updateItemButton.setOnAction(e -> editSelectedItem());

            // Clear input fields
            clearItemFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity and Price must be numbers!");
        }
    }

    /**
     * Clears the item input fields.
     */
    private void clearItemFields() {
        itemNameField.clear();
        itemCategoryField.clear();
        itemQuantityField.clear();
        itemPriceField.clear();
    }

    /**
     * Displays an alert message.
     * @param title The title of the alert.
     * @param message The message content of the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Loads the items associated with the current order.
     * @param order The order whose items are to be loaded.
     */
    private void loadItemsForOrder(Order order) {
        itemsList.clear(); // Clear old items
        itemsList.addAll(order.getItems()); // Add new items

        // Set cell value factories for each column
        itemIdColumn.setCellValueFactory(cellData -> cellData.getValue().idItemProperty().asObject());
        itemNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameItemProperty());
        itemCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryItemProperty());
        itemQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityItemProperty().asObject());
        itemPriceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerUnitItemProperty().asObject());
    }

    /**
     * Updates the order details and the items associated with it.
     */
    @FXML
    private void updateOrder() {
        // Update order details
        currentOrder.setSupplierOrder(supplierField.getText());
        currentOrder.setDateOrder(dateField.getText());
        currentOrder.setStatusOrder(statusField.getText());
        currentOrder.setTotalAmountOrder(Double.parseDouble(totalAmountField.getText()));

        // Update order in database
        orderService.update(currentOrder);

        // Save new items to the database
        for (Item item : itemsTableView.getItems()) {
            if (item.getIdItem() == 0) { // New items have ID = 0 (not yet stored in DB)
                try {
                    orderService.addItemToOrder(currentOrder.getIdOrder(), item); // Insert into DB
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Close the window after saving
        Stage stage = (Stage) supplierField.getScene().getWindow();
        stage.close();
    }
}
