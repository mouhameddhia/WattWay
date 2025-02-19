package Controllers;

import entities.Item;
import entities.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.OrderService;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OrderController {

    // Table for displaying orders
    @FXML
    private TableView<Order> ordersTableView;
    @FXML
    private TableColumn<Order, Integer> idColumn;
    @FXML
    private TableColumn<Order, String> supplierColumn;
    @FXML
    private TableColumn<Order, String> dateColumn;
    @FXML
    private TableColumn<Order, String> statusColumn;
    @FXML
    private TableColumn<Order, Double> totalAmountColumn;

    // Table for displaying items in the selected order
    @FXML
    private TableView<Item> itemsTableView;
    @FXML
    private TableColumn<Item, Integer> itemIdColumn;
    @FXML
    private TableColumn<Item, String> itemNameColumn;
    @FXML
    private TableColumn<Item, Integer> itemQuantityColumn;
    @FXML
    private TableColumn<Item, Double> itemPriceColumn;
    @FXML
    private TableColumn<Item, String> itemCategoryColumn;

    // Order service to handle database interactions
    private final OrderService orderService;

    // Action column for Update and Delete buttons
    @FXML
    private TableColumn<Order, Void> actionColumn;

    // Constructor to initialize the order service
    public OrderController() {
        this.orderService = new OrderService();
    }

    // Method to initialize the tables and listeners
    @FXML
    private void initialize() {
        // Initialize orders table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierOrder"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOrder"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusOrder"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmountOrder"));

        // Load orders from service and set them to the table
        ObservableList<Order> orders = FXCollections.observableArrayList(orderService.returnList());
        ordersTableView.setItems(orders);

        // Initialize items table
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("idItem"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("nameItem"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityItem"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnitItem"));
        itemCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryItem"));

        // Add listener to load items when an order is selected
        ordersTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadItemsForOrder(newSelection);
            }
        });

        // Add action buttons for updating and deleting orders
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");

            {
                // Handle update action
                updateButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    updateOrder(order);
                });

                // Handle delete action
                deleteButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    deleteOrder(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, updateButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        ordersTableView.getColumns().add(actionColumn);
    }

    // Method to load items for the selected order
    private void loadItemsForOrder(Order selectedOrder) {
        try {
            List<Item> items = orderService.getItemsByOrderId(selectedOrder.getIdOrder());
            itemsTableView.setItems(FXCollections.observableArrayList(items));
        } catch (SQLException e) {
            System.err.println("Error loading items: " + e.getMessage());
        }
    }

    // Method to open the Create Order window
    @FXML
    private void createOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/createOrder.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Create Order");
            stage.setScene(new Scene(root));

            // Refresh orders table after closing the Create Order window
            stage.setOnHiding(event -> refreshOrdersTable());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to refresh the orders table after creating, updating, or deleting an order
    private void refreshOrdersTable() {
        ObservableList<Order> updatedOrders = FXCollections.observableArrayList(orderService.returnList());
        ordersTableView.setItems(updatedOrders);
    }

    // Method to update an order
    private void updateOrder(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateOrder.fxml"));
            Parent root = loader.load();

            // Pass the selected order to the update controller
            UpdateOrderController updateOrderController = loader.getController();
            updateOrderController.initData(order);

            Stage stage = new Stage();
            stage.setTitle("Update Order");
            stage.setScene(new Scene(root));

            // Refresh orders table after closing the Update Order window
            stage.setOnHiding(event -> refreshOrdersTable());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to delete an order
    private void deleteOrder(Order order) {
        // Call service to delete order from the database
        orderService.delete(order.getIdOrder());
        // Remove order from the table view
        ordersTableView.getItems().remove(order);
        System.out.println("Deleted order with ID: " + order.getIdOrder());
    }
}
