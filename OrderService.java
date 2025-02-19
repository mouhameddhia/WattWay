package services;

import entities.Item;
import entities.Order;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService implements IService<Order> {

    private final Connection conn;

    public OrderService() {
        conn = MyDatabase.getInstance().getConn(); // Get the database connection
    }

    @Override
    public void add(Order order) {
        // Insert order details into the 'Order' table
        String query = "INSERT INTO `Order`(`supplierOrder`, `dateOrder`, `totalAmountOrder`, `statusOrder`) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stm.setString(1, order.getSupplierOrder());
            stm.setString(2, order.getDateOrder());
            stm.setDouble(3, order.getTotalAmountOrder());
            stm.setString(4, order.getStatusOrder());
            stm.executeUpdate();

            // Retrieve the generated Order ID
            ResultSet rs = stm.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);

                // Insert each item related to this order
                for (Item item : order.getItems()) {
                    String itemQuery = "INSERT INTO `Item`(`nameItem`, `quantityItem`, `pricePerUnitItem`, `categoryItem`, `orderId`) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement itemStmt = conn.prepareStatement(itemQuery)) {
                        itemStmt.setString(1, item.getNameItem());
                        itemStmt.setInt(2, item.getQuantityItem());
                        itemStmt.setDouble(3, item.getPricePerUnitItem());
                        itemStmt.setString(4, item.getCategoryItem());
                        itemStmt.setInt(5, orderId);
                        itemStmt.executeUpdate();
                    }
                }
                System.out.println("Order and items added successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding order and items: " + e.getMessage());
        }
    }

    @Override
    public List<Order> returnList() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM `Order`";
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                int orderId = rs.getInt("idOrder");
                List<Item> items = getItemsByOrderId(orderId); // Fetch items related to the order

                orders.add(new Order(
                        rs.getInt("idOrder"),
                        rs.getString("supplierOrder"),
                        rs.getString("dateOrder"),
                        rs.getString("statusOrder"),
                        items
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving orders: " + e.getMessage());
        }
        return orders;
    }

    // Fetch items associated with a given orderId
    public List<Item> getItemsByOrderId(int orderId) throws SQLException {
        List<Item> items = new ArrayList<>();
        String itemQuery = "SELECT * FROM `Item` WHERE orderId = ?";
        try (PreparedStatement itemStmt = conn.prepareStatement(itemQuery)) {
            itemStmt.setInt(1, orderId);
            try (ResultSet itemRs = itemStmt.executeQuery()) {
                while (itemRs.next()) {
                    items.add(new Item(
                            itemRs.getInt("idItem"),
                            itemRs.getString("nameItem"),
                            itemRs.getInt("quantityItem"),
                            itemRs.getDouble("pricePerUnitItem"),
                            itemRs.getString("categoryItem"),
                            itemRs.getInt("orderId")
                    ));
                }
            }
        }
        return items;
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM `Order` WHERE idOrder = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, id);
            int rowsDeleted = stm.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Order deleted successfully.");
            } else {
                System.out.println("Order not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
        }
    }

    @Override
    public void update(Order order) {
        String updateOrderQuery = "UPDATE `Order` SET supplierOrder = ?, dateOrder = ?, statusOrder = ? WHERE idOrder = ?";

        try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderQuery)) {
            updateOrderStmt.setString(1, order.getSupplierOrder());
            updateOrderStmt.setString(2, order.getDateOrder());
            updateOrderStmt.setString(3, order.getStatusOrder());
            updateOrderStmt.setInt(4, order.getIdOrder());

            int rowsUpdated = updateOrderStmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Order updated successfully.");

                // Update items list
                for (Item item : order.getItems()) {
                    if (item.getIdItem() == 0) {
                        // New item: Insert it
                        addItemToOrder(order.getIdOrder(), item);
                    } else {
                        // Existing item: Update it
                        updateItem(item);
                    }
                }

                // Recalculate total amount
                double newTotalAmount = calculateTotalAmount(order.getIdOrder());

                // Update total amount in the order
                updateTotalAmount(order.getIdOrder(), newTotalAmount);
            } else {
                System.out.println("Failed to update the order.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating order: " + e.getMessage());
        }
    }

    // Insert a new item into the database
    public void addItemToOrder(int orderId, Item item) throws SQLException {
        String itemQuery = "INSERT INTO `Item`(`nameItem`, `quantityItem`, `pricePerUnitItem`, `categoryItem`, `orderId`) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement itemStmt = conn.prepareStatement(itemQuery)) {
            itemStmt.setString(1, item.getNameItem());
            itemStmt.setInt(2, item.getQuantityItem());
            itemStmt.setDouble(3, item.getPricePerUnitItem());
            itemStmt.setString(4, item.getCategoryItem());
            itemStmt.setInt(5, orderId);
            itemStmt.executeUpdate();
        }
    }

    // Update an existing item
    public void updateItem(Item item) throws SQLException {
        String updateItemQuery = "UPDATE `Item` SET nameItem = ?, quantityItem = ?, pricePerUnitItem = ?, categoryItem = ? WHERE idItem = ?";
        try (PreparedStatement itemStmt = conn.prepareStatement(updateItemQuery)) {
            itemStmt.setString(1, item.getNameItem());
            itemStmt.setInt(2, item.getQuantityItem());
            itemStmt.setDouble(3, item.getPricePerUnitItem());
            itemStmt.setString(4, item.getCategoryItem());
            itemStmt.setInt(5, item.getIdItem());
            itemStmt.executeUpdate();
        }
    }

    // Delete an item by its ID
    public void deleteItem(int itemId) {
        String deleteItemQuery = "DELETE FROM `Item` WHERE idItem = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteItemQuery)) {
            stmt.setInt(1, itemId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Item deleted successfully.");
            } else {
                System.out.println("Item not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting item: " + e.getMessage());
        }
    }

    // Calculate the total amount for an order
    private double calculateTotalAmount(int orderId) throws SQLException {
        String query = "SELECT SUM(quantityItem * pricePerUnitItem) AS totalAmount FROM `Item` WHERE orderId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("totalAmount");
                }
            }
        }
        return 0.0;
    }

    // Update the total amount in the order
    private void updateTotalAmount(int orderId, double newTotalAmount) throws SQLException {
        String updateQuery = "UPDATE `Order` SET totalAmountOrder = ? WHERE idOrder = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setDouble(1, newTotalAmount);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }

}
