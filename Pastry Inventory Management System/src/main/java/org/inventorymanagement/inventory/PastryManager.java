package org.inventorymanagement.inventory;

import org.inventorymanagement.entities.Pastry;
import org.inventorymanagement.persistance.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class PastryManager {
    private static Set<String> processedOrders = new HashSet<>();
    private Connection connection;
    private JFrame frame;

    public PastryManager(Connection connection, JFrame frame) {
        this.connection = connection;
        this.frame = frame;
    }
    public static class InsufficientQuantityException extends Exception {
        public InsufficientQuantityException(String message) {
            super(message);
        }
    }
    public static boolean updatePastryQuantityAndCheck(Connection connection, String pastryName, int quantityToUpdate) {
        try {  processedOrders.clear();

            PastryManager pastryManager = new PastryManager(connection, null);
            if (!processedOrders.contains(pastryName) && pastryManager.pastryExists(connection, pastryName)) {
                int currentQuantity = pastryManager.getPastryQuantity(connection, pastryName);
                int newQuantity = currentQuantity - quantityToUpdate;

                if (newQuantity >= 0) {
                    pastryManager.updatePastryQuantity(connection, pastryName, newQuantity);
                    processedOrders.add(pastryName);
                    return true;
                } else {
                    throw new InsufficientQuantityException("Insufficient pastry quantity to fulfill the order.");
                }
            }
        } catch (InsufficientQuantityException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean addPastry(String name, int quantity, double price) {
        if (name.isEmpty() || quantity <= 0 || price <= 0) {
            return false;
        }

        try {
            if (pastryExists(connection, name)) {
                addPastryQuantity(connection, name, quantity);
            } else {
                insertPastry(connection, name, quantity, price);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void addPastryToDatabase(Pastry pastry) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                if (pastryExists(connection, pastry.getName())) {
                    addPastryQuantity(connection, pastry.getName(), pastry.getQuantity());
                } else {
                    insertPastry(connection, pastry.getName(), pastry.getQuantity(), pastry.getPrice());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean removePastry(String pastryName, int quantityToRemove) {
        if (pastryName.isEmpty() || quantityToRemove <= 0) {
            return false;
        }
        try {
            int currentQuantity = getPastryQuantity(connection, pastryName);
            if (currentQuantity >= quantityToRemove) {
                updatePastryQuantity(connection, pastryName, currentQuantity - quantityToRemove);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private int getPastryQuantity(Connection connection, String pastryName) throws SQLException {
        String query = "SELECT quantity FROM pastries WHERE name = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, pastryName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("quantity");
                }
            }
        }
        return 0;
    }
    public static boolean pastryExists(Connection connection, String name) throws SQLException {
        String query = "SELECT COUNT(*) FROM pastries WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }
    private void insertPastry(Connection connection, String name, int quantity, double price) throws SQLException {
        String insertQuery = "INSERT INTO pastries (name, quantity, price) VALUES (?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(insertQuery)) {
            statement.setString(1, name);
            statement.setInt(2, quantity);
            statement.setDouble(3, price);
            statement.executeUpdate();
        }
    }
    private void addPastryQuantity(Connection connection, String pastryName, int quantityToAdd) throws SQLException {
        String updateQuery = "UPDATE pastries SET quantity = quantity + ? WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, quantityToAdd);
            statement.setString(2, pastryName);
            statement.executeUpdate();
        }
    }
    private void updatePastryQuantity(Connection connection, String pastryName, int newQuantity) throws SQLException {
        String updateQuery = "UPDATE pastries SET quantity = ? WHERE name = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(updateQuery)) {
            statement.setInt(1, newQuantity);
            statement.setString(2, pastryName);
            statement.executeUpdate();
        }
    }
    public void addMissingPastries() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                if (!pastryExists(connection, "Croissant")) {
                    insertPastry(connection, "Croissant", 20, 2.5);
                }
                if (!pastryExists(connection, "Blueberry Muffin")) {
                    insertPastry(connection, "Blueberry Muffin", 15, 1.75);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
