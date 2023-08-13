package org.inventorymanagement.ingridients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IngredientManagerTest {

    @Mock
    private Connection mockConnection;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddIngredient() throws SQLException {
        IngredientManager ingredientManager = new IngredientManager();

        assertFalse(ingredientManager.getIngredientStock().containsKey("Test Ingredient"));

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        ingredientManager.addIngredient("Test Ingredient", 10, mockConnection);

        assertTrue(ingredientManager.getIngredientStock().containsKey("Test Ingredient"));
        assertEquals(Integer.valueOf(10), ingredientManager.getIngredientStock().get("Test Ingredient"));
    }

    @Test
    public void testRemoveIngredient() throws SQLException {
        IngredientManager ingredientManager = new IngredientManager();

        ingredientManager.addToIngredientStock("Test Ingredient", 20);
        assertTrue(ingredientManager.getIngredientStock().containsKey("Test Ingredient"));

        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        when(mockConnection.prepareStatement(anyString()).executeUpdate()).thenReturn(1);

        ingredientManager.removeIngredient("Test Ingredient", 5, mockConnection);

        assertEquals(Integer.valueOf(15), ingredientManager.getIngredientStock().get("Test Ingredient"));
    }

    @Test
    public void testIngredientExists() throws SQLException {
        IngredientManager ingredientManager = new IngredientManager();

        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        when(mockConnection.prepareStatement(anyString()).executeQuery()).thenReturn(null);
        when(mockConnection.prepareStatement(anyString()).executeQuery().next()).thenReturn(true, false);

        assertTrue(ingredientManager.ingredientExists(mockConnection, "Existing Ingredient"));
        assertFalse(ingredientManager.ingredientExists(mockConnection, "Nonexistent Ingredient"));
    }

    @Test
    public void testAddMissingIngredients() throws SQLException {
        IngredientManager ingredientManager = new IngredientManager();

        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        when(mockConnection.prepareStatement(anyString()).executeQuery()).thenReturn(null);
        when(mockConnection.prepareStatement(anyString()).executeQuery().next()).thenReturn(false, true);

        ingredientManager.addMissingIngredients();

        Map<String, Integer> ingredientStock = ingredientManager.getIngredientStock();
        assertTrue(ingredientStock.containsKey("Flour"));
        assertTrue(ingredientStock.containsKey("Butter"));
        assertTrue(ingredientStock.containsKey("Sugar"));
        assertTrue(ingredientStock.containsKey("Blueberries"));
    }
}
