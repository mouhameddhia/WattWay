package services;

import entities.Item;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemService implements IService<Item> {

    private final Connection conn;

    // Constructor to initialize the connection
    public ItemService() {
        conn = MyDatabase.getInstance().getConn();
    }

    @Override
    public void add(Item item) {
        String query = "INSERT INTO `Item`(`nameItem`, `quantityItem`, `pricePerUnitItem`, `categoryItem`, `orderId`) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setString(1, item.getNameItem());
            stm.setInt(2, item.getQuantityItem());
            stm.setDouble(3, item.getPricePerUnitItem());
            stm.setString(4, item.getCategoryItem());
            stm.setInt(5, item.getOrderId());
            stm.executeUpdate();
            System.out.println("Item added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding item: " + e.getMessage());
        }
    }

    @Override
    public List<Item> returnList() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM `Item`";
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                items.add(new Item(
                        rs.getInt("idItem"),
                        rs.getString("nameItem"),
                        rs.getInt("quantityItem"),
                        rs.getDouble("pricePerUnitItem"),
                        rs.getString("categoryItem"),
                        rs.getInt("orderId")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving items: " + e.getMessage());
        }
        return items;
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM `Item` WHERE idItem = ?";
        try (PreparedStatement stm = conn.prepareStatement(query)) {
            stm.setInt(1, id);
            int rowsDeleted = stm.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Item deleted successfully.");
            } else {
                System.out.println("Item not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting item: " + e.getMessage());
        }
    }

    @Override
    public void update(Item item) {
        String updateQuery = "UPDATE `Item` SET nameItem = ?, quantityItem = ?, pricePerUnitItem = ?, categoryItem = ?, orderId = ? WHERE idItem = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setString(1, item.getNameItem());
            updateStmt.setInt(2, item.getQuantityItem());
            updateStmt.setDouble(3, item.getPricePerUnitItem());
            updateStmt.setString(4, item.getCategoryItem());
            updateStmt.setInt(5, item.getOrderId());
            updateStmt.setInt(6, item.getIdItem()); // Set the ID at the end

            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Item updated successfully.");
            } else {
                System.out.println("Failed to update the item.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating item: " + e.getMessage());
        }
    }
}
