package org.inventorymanagement.orders;

import org.inventorymanagement.entities.Order;
import org.inventorymanagement.persistance.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderProcessorTest {
    private OrderProcessor orderProcessor;
    private Connection connection;

    @BeforeEach
    void setUp() {
        orderProcessor = new OrderProcessor();
    }

    @Test
    void testAddOrder() {
        Order order = new Order(1, "John Doe", "123 Main St", "Croissant", 5, "Pending", LocalDateTime.now(), LocalDateTime.now());
        orderProcessor.addOrder(order);
        List<Order> orders = orderProcessor.getOrders();
        assertEquals(1, orders.size());
        assertEquals(order, orders.get(0));
    }

    @Test
    void testDeleteOrder() {
        Order order1 = new Order(1, "John Doe", "123 Main St", "Croissant", 5, "Pending", LocalDateTime.now(), LocalDateTime.now());
        Order order2 = new Order(2, "Jane Smith", "456 Elm St", "Blueberry Muffin", 3, "Pending", LocalDateTime.now(), LocalDateTime.now());

        orderProcessor.addOrder(order1);
        orderProcessor.addOrder(order2);

        assertTrue(orderProcessor.deleteOrder(1));
        assertFalse(orderProcessor.deleteOrder(3));

        List<Order> orders = orderProcessor.getOrders();
        assertEquals(1, orders.size());
        assertEquals(order2, orders.get(0));
    }

    @Test
    void testAddOrderToDatabase() {
        Order order = new Order(1, "John Doe", "123 Main St", "Croissant", 5, "Pending", LocalDateTime.now(), LocalDateTime.now());
        connection = DatabaseConnection.getConnection();
        boolean added = orderProcessor.addOrderToDatabase(order, connection);
        assertTrue(added);
    }

    @Test
    void testUpdateOrderStatusInDatabase() {
        Order order = new Order(1, "John Doe", "123 Main St", "Croissant", 5, "Pending", LocalDateTime.now(), LocalDateTime.now());

        Connection connection = DatabaseConnection.getConnection();
        assertNotNull(connection, "Database connection is null");

        OrderProcessor orderProcessor = new OrderProcessor();
        assertNotNull(orderProcessor, "OrderProcessor is null");

        boolean updated = orderProcessor.updateOrderStatusInDatabase(order, connection);
        assertTrue(updated);
    }

    @Test
    void testDeleteOrderFromDatabase() {
        Connection connection = DatabaseConnection.getConnection();
        assertNotNull(connection, "Database connection is null");

        Order order = new Order(1, "John Doe", "123 Main St", "Croissant", 5, "Pending", LocalDateTime.now(), LocalDateTime.now());
        boolean added = orderProcessor.addOrderToDatabase(order, connection);
        assertTrue(added, "Failed to add order to the database");


        boolean deleted = orderProcessor.deleteOrderFromDatabase(order.getOrderId(), connection);
        assertTrue(deleted, "Failed to delete order from the database");

        List<Order> orders = orderProcessor.getOrders();
        assertFalse(orders.contains(order), "Order still exists in the list after deletion");
    }

}