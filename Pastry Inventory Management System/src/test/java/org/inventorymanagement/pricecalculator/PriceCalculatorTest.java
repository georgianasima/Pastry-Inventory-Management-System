package org.inventorymanagement.pricecalculator;
import org.junit.Test;
import java.text.DecimalFormat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PriceCalculatorTest {
    @Test
    public void testCalculatePrice_validInput() {
        double hours = 5.0;
        double ingredientCost = 30.0;

        PriceCalculator priceCalculator = new PriceCalculator(0.2);
        String result = priceCalculator.calculatePrice(hours, ingredientCost);

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String expected = "Cost: $" + decimalFormat.format(80.0) + "\nSelling Price: $" + decimalFormat.format(96.0);
        assertEquals(expected, result);
    }
    @Test
    public void testCalculatePrice_invalidInput() {
        double hours = -2.0;
        double ingredientCost = 25.0;

        PriceCalculator priceCalculator = new PriceCalculator(0.2);
        String result = priceCalculator.calculatePrice(hours, ingredientCost);

        assertTrue(result.contains("Please enter valid hours and ingredient cost."));
    }
    @Test
    public void testCalculateCost() {
        double ingredientCost = 50.0;
        double laborHours = 6.0;

        PriceCalculator priceCalculator = new PriceCalculator(0.2);
        double result = priceCalculator.calculateCost(ingredientCost, laborHours);

        assertEquals(110.0, result, 0.01);
    }

    @Test
    public void testCalculateSellingPrice() {
        double cost = 80.0;

        PriceCalculator priceCalculator = new PriceCalculator(0.2);
        double result = priceCalculator.calculateSellingPrice(cost);

        assertEquals(96.0, result, 0.01);
    }
}
