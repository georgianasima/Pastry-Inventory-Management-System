package org.inventorymanagement.inventory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.inventorymanagement.persistance.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class PastryManagerTest {

    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {

        connection = DatabaseConnection.getConnection();
    }

    @Test
    public void testAddPastry() {
        PastryManager pastryManager = new PastryManager(connection, null);
        assertTrue(pastryManager.addPastry("Test Pastry", 10, 3.0));
    }

    @Test
    public void testRemovePastry() {
        PastryManager pastryManager = new PastryManager(connection, null);
        assertTrue(pastryManager.removePastry("Test Pastry", 5));
    }

    @Test
    public void testPastryExists() {
        PastryManager pastryManager = new PastryManager(connection, null);
        try {
            assertTrue(pastryManager.pastryExists(connection, "Croissant"));
        } catch (SQLException e) {
            fail("Exception occurred: " + e.getMessage());
        }
        try {
            assertFalse(pastryManager.pastryExists(connection, "Nonexistent Pastry"));
        } catch (SQLException e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testAddMissingPastries() {
        PastryManager pastryManager = new PastryManager(connection, null);
        pastryManager.addMissingPastries();
        try {
            assertTrue(pastryManager.pastryExists(connection, "Croissant"));
        } catch (SQLException e) {
            fail("Exception occurred: " + e.getMessage());
        }
        try {
            assertTrue(pastryManager.pastryExists(connection, "Blueberry Muffin"));
        } catch (SQLException e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testUpdatePastryQuantityAndCheck() {
        assertTrue(PastryManager.updatePastryQuantityAndCheck(connection, "Croissant", 5));
    }

    @Test
    public void testUpdatePastryQuantityAndCheck_InsufficientQuantity() {
        assertFalse(PastryManager.updatePastryQuantityAndCheck(connection, "Croissant", 100));
    }

    @Test
    public void testUpdatePastryQuantityAndCheck_NonexistentPastry() {
        assertFalse(PastryManager.updatePastryQuantityAndCheck(connection, "Nonexistent Pastry", 5));
    }
}
