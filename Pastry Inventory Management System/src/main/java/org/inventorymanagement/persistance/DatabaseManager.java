package org.inventorymanagement.persistance;

import org.inventorymanagement.entities.Ingredient;
import org.inventorymanagement.entities.Order;
import org.inventorymanagement.entities.Pastry;
import org.inventorymanagement.ingridients.IngredientManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private IngredientManager ingredientManager;
    private Connection connection;
    public DatabaseManager() {
        this.connection = connection;
        ingredientManager = new IngredientManager();
    }
    public List<Pastry> retrievePastryData() {
        List<Pastry> pastryInventory = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String selectQuery = "SELECT name, quantity, price FROM pastries";
                try (PreparedStatement statement = connection.prepareStatement(selectQuery);
                     ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        int quantity = resultSet.getInt("quantity");
                        double price = resultSet.getDouble("price");
                        Pastry pastry = new Pastry(name, quantity, price, new ArrayList<>());
                        pastryInventory.add(pastry);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pastryInventory;
    }
    public List<Ingredient> retrieveIngredientData() {
        List<Ingredient> ingredientInventory = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String selectQuery = "SELECT name, quantity, expiration_date FROM ingredients";
                try (PreparedStatement statement = connection.prepareStatement(selectQuery);
                     ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        int quantity = resultSet.getInt("quantity");
                        String expirationDate = resultSet.getString("expiration_date");
                        Ingredient ingredient = new Ingredient(name, quantity, expirationDate);
                        ingredientInventory.add(ingredient);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredientInventory;
    }
    public List<Order> retrieveOrderData() {
        List<Order> orders = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String selectQuery = "SELECT * FROM orders";
                try (PreparedStatement statement = connection.prepareStatement(selectQuery);
                     ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {
                        int orderId = resultSet.getInt("id");
                        String customerName = resultSet.getString("customer_name");
                        String deliveryDetails = resultSet.getString("delivery_details");
                        String pastryName = resultSet.getString("pastry_name");
                        int quantity = resultSet.getInt("quantity");
                        String status = resultSet.getString("status");
                        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                        LocalDateTime updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime();

                        Order order = new Order(orderId, customerName, deliveryDetails, pastryName, quantity, status, createdAt, updatedAt);
                        orders.add(order);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

}