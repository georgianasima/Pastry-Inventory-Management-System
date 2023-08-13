package org.inventorymanagement.ingridients;

import org.inventorymanagement.entities.Ingredient;
import org.inventorymanagement.persistance.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class IngredientManager {
    private static Map<String, Integer> ingredientStock;
    public IngredientManager() {
        this.ingredientStock = new HashMap<>();
    }
    public static void addIngredient(String ingredientName, int ingredientQuantity, Connection connection) {
        if (ingredientName.isEmpty() || ingredientQuantity <= 0) {
            showMessage("Please enter a valid ingredient name and quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Ingredient ingredient = new Ingredient(ingredientName, ingredientQuantity, null);

        addToIngredientStock(ingredientName, ingredientQuantity);

        try {
            addIngredientToDatabase(connection, ingredientName, ingredientQuantity);
            showMessage("Ingredient added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Failed to add ingredient to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void addToIngredientStock(String ingredientName, int ingredient) {
        ingredientStock.put(ingredientName, ingredient);
    }
    public static Map<String, Integer> getIngredientStock() {
        return ingredientStock;
    }

    public static void addIngredientToDatabase(Connection connection, String ingredientName, int ingredientQuantity) throws SQLException {
        if (connection != null) {
            if (ingredientExists(connection, ingredientName)) {
                addIngredientQuantity(connection, ingredientName, ingredientQuantity);
            } else {
                insertIngredient(connection, ingredientName, ingredientQuantity);
            }
        }
    }
    public void removeIngredient(String ingredientName, int quantityToRemove, Connection connection) {
        if (validateInput(ingredientName, quantityToRemove)) {
            try {
                if (removeIngredientFromDatabase(connection, ingredientName, quantityToRemove)) {
                    showMessage("Ingredient removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showMessage("Failed to remove ingredient.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static boolean ingredientExists(Connection connection, String name) throws SQLException {
        String query = "SELECT COUNT(*) FROM ingredients WHERE name = ?";
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
    private static void addIngredientQuantity(Connection connection, String ingredientName, int quantityToAdd) throws SQLException {
        String updateQuery = "UPDATE ingredients SET quantity = quantity + ? WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, quantityToAdd);
            statement.setString(2, ingredientName);
            statement.executeUpdate();
        }
    }
    private static void insertIngredient(Connection connection, String name, int quantity) throws SQLException {
        String insertQuery = "INSERT INTO ingredients (name, quantity) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, name);
            statement.setInt(2, quantity);
            statement.executeUpdate();
        }
    }
    private int getIngredientQuantity(Connection connection, String ingredientName) throws SQLException {
        String query = "SELECT quantity FROM ingredients WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ingredientName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("quantity");
                }
            }
        }
        return 0;
    }
    private void updateIngredientQuantity(Connection connection, String ingredientName, int newQuantity) throws SQLException {
        String updateQuery = "UPDATE ingredients SET quantity = ? WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, newQuantity);
            statement.setString(2, ingredientName);
            statement.executeUpdate();
        }
    }
    private void addOrUpdateIngredient(Connection connection, Ingredient ingredient) throws SQLException {
        if (connection != null) {
            if (ingredientExists(connection, ingredient.getName())) {
                addIngredientQuantity(connection, ingredient.getName(), ingredient.getQuantity());
            } else {
                insertIngredient(connection, ingredient.getName(), ingredient.getQuantity());
            }
        }
    }
    boolean removeIngredientFromDatabase(Connection connection, String ingredientName, int quantityToRemove) throws SQLException {
        if (connection != null && ingredientExists(connection, ingredientName)) {
            int currentQuantity = getIngredientQuantity(connection, ingredientName);
            int newQuantity = currentQuantity - quantityToRemove;

            if (newQuantity >= 0) {
                updateIngredientQuantity(connection, ingredientName, newQuantity);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean validateInput(String ingredientName, int quantity) {
        return !ingredientName.isEmpty() && quantity > 0;
    }

    private static void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }

    public static void addMissingIngredients() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                IngredientManager ingredientManager = new IngredientManager();
                if (!ingredientManager.ingredientExists(connection, "Flour")) {
                    ingredientManager.insertIngredient(connection, "Flour", 100);
                }
                if (!ingredientManager.ingredientExists(connection, "Butter")) {
                    ingredientManager.insertIngredient(connection, "Butter", 50);
                }
                if (!ingredientManager.ingredientExists(connection, "Sugar")) {
                    ingredientManager.insertIngredient(connection, "Sugar", 20);
                }
                if (!ingredientManager.ingredientExists(connection, "Blueberries")) {
                    ingredientManager.insertIngredient(connection, "Blueberries", 30);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
