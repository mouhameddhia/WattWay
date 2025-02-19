package Controllers;

import entities.Item;
import entities.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.OrderService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderController {

    @FXML
    private TextField supplierField, dateField, statusField;
    @FXML
    private TextField itemNameField, itemCategoryField, itemQuantityField, itemPriceField;
    @FXML
    private TableView<Item> itemsTableView;
    @FXML
    private TableColumn<Item, String> itemNameColumn, itemCategoryColumn;
    @FXML
    private TableColumn<Item, Integer> itemQuantityColumn;
    @FXML
    private TableColumn<Item, Double> itemPriceColumn;
    @FXML private Button deleteItemButton, updateItemButton;

    private final OrderService orderService = new OrderService();
    private final ObservableList<Item> itemsList = FXCollections.observableArrayList();
    private Item selectedItem; // Stores the selected item for editing

    @FXML
    private void initialize() {
        // Initialize TableView columns and data bindings
        itemNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameItemProperty());
        itemCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryItemProperty());
        itemQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityItemProperty().asObject());
        itemPriceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerUnitItemProperty().asObject());

        // Bind the list of items to the TableView
        itemsTableView.setItems(itemsList);

        // Initially hide action buttons
        deleteItemButton.setVisible(false);
        updateItemButton.setVisible(false);

        // Handle row selection
        itemsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedItem = newSelection;
            boolean isItemSelected = newSelection != null;
            deleteItemButton.setVisible(isItemSelected);
            updateItemButton.setVisible(isItemSelected);
        });
    }

    @FXML
    private void deleteSelectedItem() {
        if (selectedItem != null) {
            itemsList.remove(selectedItem);

            // If the item exists in the database, delete it
            if (selectedItem.getIdItem() != 0) {
                orderService.deleteItem(selectedItem.getIdItem());
            }

            // Hide buttons after deletion
            deleteItemButton.setVisible(false);
            updateItemButton.setVisible(false);
        }
    }

    @FXML
    private void editSelectedItem() {
        if (selectedItem != null) {
            // Populate input fields with selected item's data
            itemNameField.setText(selectedItem.getNameItem());
            itemCategoryField.setText(selectedItem.getCategoryItem());
            itemQuantityField.setText(String.valueOf(selectedItem.getQuantityItem()));
            itemPriceField.setText(String.valueOf(selectedItem.getPricePerUnitItem()));

            // Change "Add Item" button to "Save Changes"
            updateItemButton.setText("Save Changes");
            updateItemButton.setOnAction(e -> saveItemChanges());
        }
    }

    private void saveItemChanges() {
        try {
            // Retrieve the data from input fields
            String name = itemNameField.getText();
            String category = itemCategoryField.getText();
            int quantity = Integer.parseInt(itemQuantityField.getText());
            double price = Double.parseDouble(itemPriceField.getText());

            // Validate input data
            if (name.isEmpty() || category.isEmpty()) {
                showAlert("Error", "Item name and category cannot be empty!");
                return;
            }

            // Update selected item details
            selectedItem.setNameItem(name);
            selectedItem.setCategoryItem(category);
            selectedItem.setQuantityItem(quantity);
            selectedItem.setPricePerUnitItem(price);

            // Update item in database if it exists
            if (selectedItem.getIdItem() != 0) {
                try {
                    orderService.updateItem(selectedItem);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            // Refresh the table and reset buttons
            itemsTableView.refresh();
            updateItemButton.setText("Edit Item");
            updateItemButton.setOnAction(e -> editSelectedItem());

            // Clear input fields
            clearItemFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity and Price must be numbers!");
        }
    }

    private void clearItemFields() {
        // Clear the item input fields
        itemNameField.clear();
        itemCategoryField.clear();
        itemQuantityField.clear();
        itemPriceField.clear();
    }

    @FXML
    private void addItem() {
        try {
            // Retrieve item data from input fields
            String name = itemNameField.getText();
            String category = itemCategoryField.getText();
            int quantity = Integer.parseInt(itemQuantityField.getText());
            double price = Double.parseDouble(itemPriceField.getText());

            // Validate item data
            if (name.isEmpty() || category.isEmpty()) {
                showAlert("Error", "Item name and category cannot be empty!");
                return;
            }

            // Create a new item and add it to the list
            Item newItem = new Item(0, name, quantity, price, category, 0);
            itemsList.add(newItem);

            // Clear input fields for new item entry
            clearItemFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity and Price must be numbers!");
        }
    }

    @FXML
    private void saveOrder() {
        // Retrieve order data from input fields
        String supplier = supplierField.getText();
        String date = dateField.getText();
        String status = statusField.getText().isEmpty() ? "pending" : statusField.getText();

        // Validate order data
        if (supplier.isEmpty() || date.isEmpty()) {
            showAlert("Error", "Supplier and Date cannot be empty!");
            return;
        }

        // Create the new order and add it to the database
        List<Item> itemList = new ArrayList<>(itemsList);
        Order newOrder = new Order(supplier, date, status, itemList);
        orderService.add(newOrder);

        // Close the form after saving
        ((Stage) supplierField.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        // Display an error alert with the provided title and message
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
