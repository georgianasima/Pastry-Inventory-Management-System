package org.inventorymanagement.pricecalculator;

import java.text.DecimalFormat;
public class PriceCalculator {
    private double profitMargin;
    public PriceCalculator(double profitMargin) {
        this.profitMargin = profitMargin;
    }
    public String calculatePrice(double hours, double ingredientCost) {
        if (hours <= 0 || ingredientCost <= 0) {
            return "Please enter valid hours and ingredient cost.";
        }
        double cost = calculateCost(ingredientCost, hours);
        double sellingPrice = calculateSellingPrice(cost);

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        return "Cost: $" + decimalFormat.format(cost) + "\nSelling Price: $" + decimalFormat.format(sellingPrice);
    }
    public double calculateCost(double ingredientCost, double laborHours) {
        double laborCostPerHour = 10.0;
        double totalCost = ingredientCost + (laborHours * laborCostPerHour);
        return totalCost;
    }

    public double calculateSellingPrice(double cost) {
        double sellingPrice = cost + (cost * profitMargin);
        return sellingPrice;
    }
}
