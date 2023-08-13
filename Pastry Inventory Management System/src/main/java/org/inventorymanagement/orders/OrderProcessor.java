package org.inventorymanagement.orders;

import org.inventorymanagement.entities.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderProcessor {
    private List<Order> orders;

    public OrderProcessor() {
        this.orders = new ArrayList<>();
    }
    public List<Order> getOrders() {
        return orders;
    }
    public void addOrder(Order order) {
        orders.add(order);
    }
    public boolean deleteOrder(int orderId) {
        Order foundOrder = null;
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                foundOrder = order;
                break;
            }
        }
        if (foundOrder != null) {
            orders.remove(foundOrder);
            return true;
        }
        return false;
    }
    public boolean addOrderToDatabase(Order order, Connection connection) {
        String insertQuery = "INSERT INTO orders (customer_name, delivery_details, pastry_name, quantity, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, order.getCustomerName());
            statement.setString(2, order.getdeliveryDetails());
            statement.setString(3, order.getPastryName());
            statement.setInt(4, order.getQuantity());
            statement.setString(5, order.getStatus());
            statement.setTimestamp(6, Timestamp.valueOf(order.getCreatedAt()));
            statement.setTimestamp(7, Timestamp.valueOf(order.getUpdatedAt()));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        order.setOrderId(orderId);
                        orders.add(order);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateOrderStatusInDatabase(Order order, Connection connection) {
        String sql = "UPDATE orders SET status = ? WHERE customer_name = ? AND delivery_details = ? AND pastry_name = ? AND quantity = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, order.getStatus());
            statement.setString(2, order.getCustomerName());
            statement.setString(3, order.getdeliveryDetails());
            statement.setString(4, order.getPastryName());
            statement.setInt(5, order.getQuantity());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteOrderFromDatabase(int orderId, Connection connection) {
        String deleteQuery = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, orderId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                Order deletedOrder = orders.stream()
                        .filter(order -> order.getOrderId() == orderId)
                        .findFirst()
                        .orElse(null);

                if (deletedOrder != null) {
                    orders.remove(deletedOrder);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

