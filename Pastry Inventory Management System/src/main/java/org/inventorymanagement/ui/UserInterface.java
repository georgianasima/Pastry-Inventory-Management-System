package org.inventorymanagement.ui;

import org.inventorymanagement.entities.Ingredient;
import org.inventorymanagement.entities.Order;
import org.inventorymanagement.entities.Pastry;
import org.inventorymanagement.ingridients.IngredientManager;
import org.inventorymanagement.inventory.PastryManager;
import org.inventorymanagement.orders.OrderProcessor;
import org.inventorymanagement.persistance.DatabaseConnection;
import org.inventorymanagement.persistance.DatabaseManager;
import org.inventorymanagement.pricecalculator.PriceCalculator;
import java.time.LocalDateTime;

import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.List;

public class  UserInterface {
    IngredientManager ingredientManager = new IngredientManager();
    private Connection connection;
    private JFrame frame;
    private JLabel titleLabel;
    private JButton pastryButton;
    private JButton ingredientButton;
    private JButton orderButton;
    private JTextArea displayArea;
    private JLabel hoursLabel;
    private JLabel ingredientCostLabel;
    private JTextField hoursField;
    private JTextField ingredientCostField;
    private JButton calculatePriceButton;
    private JLabel ingredientNameLabel;
    private JLabel ingredientQuantityLabel;
    private JTextField ingredientNameField;
    private JTextField ingredientQuantityField;
    private JButton addIngredientButton;
    private JButton removeIngredientButton;
    private JLabel pastryNameLabel;
    private JLabel pastryQuantityLabel;
    private JLabel pastryPriceLabel;
    private JTextField pastryNameField;
    private JTextField pastryQuantityField;
    private JTextField pastryPriceField;
    private JButton addPastryButton;
    private JButton removePastryButton;
    private JPanel headerPanel;
    private OrderProcessor orderProcessor;
    private PriceCalculator priceCalculator;
    private JButton removeOrderButton;
    private DatabaseManager databaseManager;
    private PastryManager pastryManager;

    public UserInterface() {
        databaseManager = new DatabaseManager();
        frame = new JFrame("Pastry Inventory Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        Connection connection = DatabaseConnection.getConnection();
        pastryManager = new PastryManager(connection, frame);
        PastryManager pastryManagerInstance = new PastryManager(DatabaseConnection.getConnection(), frame);
        pastryManagerInstance.updatePastryQuantityAndCheck(DatabaseConnection.getConnection(), "pastryName", 10);

        titleLabel = new JLabel("Inventory Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        pastryButton = new JButton("View Pastry Inventory");
        ingredientButton = new JButton("View Ingredient Inventory");
        orderButton = new JButton("View Order Status");
        displayArea = new JTextArea();
        displayArea.setEditable(false);

        hoursLabel = new JLabel("   Hours Worked:");
        ingredientCostLabel = new JLabel("Ingredient Cost:");
        hoursField = new JTextField();
        ingredientCostField = new JTextField();
        calculatePriceButton = new JButton("Calculate Price");

        ingredientNameLabel = new JLabel("     Ingredient Name:");
        ingredientQuantityLabel = new JLabel("     Ingredient Quantity:");
        ingredientNameField = new JTextField();
        ingredientQuantityField = new JTextField();
        addIngredientButton = new JButton("Add Ingredient");
        removeIngredientButton = new JButton("Remove Ingredient");

        pastryNameLabel = new JLabel("     Pastry Name:");
        pastryQuantityLabel = new JLabel("     Pastry Quantity:");
        pastryPriceLabel = new JLabel("     Pastry Price:");
        pastryNameField = new JTextField();
        pastryQuantityField = new JTextField();
        pastryPriceField = new JTextField();
        addPastryButton = new JButton("Add Pastry");
        removePastryButton = new JButton("Remove Pastry");

        JLabel customerNameLabel = new JLabel("     Customer Name:");
        JTextField customerNameField = new JTextField(20);

        JLabel deliveryDetailsLabel = new JLabel("     Delivery Details:");
        JTextField deliveryDetailsField = new JTextField(20);
        JLabel pastryOrderLabel = new JLabel("     Pastry Name:");
        JTextField pastryOrderField = new JTextField(20);
        JLabel pastryQuantityOrderLabel = new JLabel("     Pastry Quantity:");
        JTextField pastryQuantityOrderField = new JTextField(20);

        JLabel statusLabel = new JLabel("     Order Status:");
        String[] statusOptions = {"Pending", "In Progress", "Done"};
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);

        JButton addOrderButton = new JButton("Add Order");
        JButton deleteOrderButton = new JButton("Delete Order");

        removeOrderButton = new JButton("Remove Order");

        pastryManager = new PastryManager(DatabaseConnection.getConnection(), frame);
        orderProcessor = new OrderProcessor();
        priceCalculator = new PriceCalculator(0.2);
        pastryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayPastryInventory();
            }
        });

        ingredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               displayIngredientInventory();
            }
        });
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayOrderStatus();
            }
        });

        PriceCalculator priceCalculator = new PriceCalculator(0.2);
        calculatePriceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String hoursString = hoursField.getText().trim();
                String ingredientCostString = ingredientCostField.getText().trim();

                if (hoursString.isEmpty() || ingredientCostString.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter the hours worked and ingredient cost.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double hours;
                double ingredientCost;

                try {
                    hours = Double.parseDouble(hoursString);
                    ingredientCost = Double.parseDouble(ingredientCostString);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter numerical values.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double cost = priceCalculator.calculateCost(ingredientCost, hours);
                double sellingPrice = priceCalculator.calculateSellingPrice(cost);

                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                String result = "Cost: $" + decimalFormat.format(cost) + "\nSelling Price: $" + decimalFormat.format(sellingPrice);

                displayArea.setText(result);
            }
        });
        addIngredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ingredientName = ingredientNameField.getText().trim();
                String ingredientQuantityString = ingredientQuantityField.getText().trim();

                if (ingredientName.isEmpty() || ingredientQuantityString.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter the ingredient name and quantity.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int ingredientQuantity;

                try {
                    ingredientQuantity = Integer.parseInt(ingredientQuantityString);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid quantity. Please enter a whole number.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Connection connection = DatabaseConnection.getConnection();

                IngredientManager.addIngredient(ingredientName, ingredientQuantity, connection);

                ingredientNameField.setText("");
                ingredientQuantityField.setText("");
                displayIngredientInventory();
            }
        });
        removeIngredientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ingredientName = ingredientNameField.getText().trim();
                String quantityToRemoveString = ingredientQuantityField.getText().trim();

                if (ingredientName.isEmpty() || quantityToRemoveString.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter the ingredient name and quantity to remove.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int quantityToRemove;

                try {
                    quantityToRemove = Integer.parseInt(quantityToRemoveString);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid quantity. Please enter a whole number.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection connection = DatabaseConnection.getConnection();

                ingredientManager.removeIngredient(ingredientName, quantityToRemove, connection);

                ingredientNameField.setText("");
                ingredientQuantityField.setText("");
                displayIngredientInventory();
            }
        });
        addPastryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pastryName = pastryNameField.getText().trim();
                String pastryQuantityString = pastryQuantityField.getText().trim();
                String pastryPriceString = pastryPriceField.getText().trim();

                if (pastryName.isEmpty() || pastryQuantityString.isEmpty() || pastryPriceString.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter the pastry name, quantity, and price.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int pastryQuantity;
                double pastryPrice;

                try {
                    pastryQuantity = Integer.parseInt(pastryQuantityString);
                    pastryPrice = Double.parseDouble(pastryPriceString);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter valid values.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean added = pastryManager.addPastry(pastryName, pastryQuantity, pastryPrice);

                if (added) {
                    JOptionPane.showMessageDialog(frame, "Pastry added successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    populateInventoryData();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to add pastry.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                pastryNameField.setText("");
                pastryQuantityField.setText("");
                pastryPriceField.setText("");
                displayPastryInventory();
            }
        });
        removePastryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pastryName = pastryNameField.getText().trim();
                String quantityToRemoveString = pastryQuantityField.getText().trim();

                if (pastryName.isEmpty() || quantityToRemoveString.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter the pastry name and quantity to remove.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int quantityToRemove;

                try {
                    quantityToRemove = Integer.parseInt(quantityToRemoveString);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid quantity. Please enter a whole number.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean removed = pastryManager.removePastry(pastryName, quantityToRemove);
                if (removed) {
                    JOptionPane.showMessageDialog(frame, "Pastry removed successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    populateInventoryData();
                } else {
                    JOptionPane.showMessageDialog(frame, "Pastry not found or insufficient quantity.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                pastryNameField.setText("");
                pastryQuantityField.setText("");
                displayPastryInventory();
            }
        });
        addOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String customerName = customerNameField.getText().trim();
                String deliveryDetails = deliveryDetailsField.getText().trim();
                String pastryName = pastryOrderField.getText().trim();
                String pastryQuantityString = pastryQuantityOrderField.getText().trim();

                if (pastryQuantityString.isEmpty() || customerName.isEmpty() || deliveryDetails.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all the order details.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int pastryQuantity;
                try {
                    pastryQuantity = Integer.parseInt(pastryQuantityString);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid pastry quantity.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String orderStatus = (String) statusComboBox.getSelectedItem();

                try (Connection connection = DatabaseConnection.getConnection()) {
                    if (connection != null) {
                        Order existingOrderToUpdate = null;

                        for (Order existingOrder : orderProcessor.getOrders()) {
                            if (existingOrder.matches(customerName, deliveryDetails, pastryName, pastryQuantity)) {
                                existingOrderToUpdate = existingOrder;
                                break;
                            }
                        }
                        if (existingOrderToUpdate != null) {
                            if (!existingOrderToUpdate.getStatus().equals(orderStatus)) {
                                existingOrderToUpdate.setStatus(orderStatus);
                                if (orderProcessor.updateOrderStatusInDatabase(existingOrderToUpdate, connection)) {
                                    JOptionPane.showMessageDialog(frame, "Order status updated successfully.",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                                    populateOrderData();
                                    populateInventoryData();
                                } else {
                                    JOptionPane.showMessageDialog(frame, "Failed to update order status in the database.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(frame, "Order with same details already exists.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            boolean pastryExistsInInventory = PastryManager.pastryExists(connection, pastryName);
                            if (!pastryExistsInInventory) {
                                JOptionPane.showMessageDialog(frame, "Pastry not found in inventory.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            int newOrderId = orderProcessor.getOrders().isEmpty() ? 1 : orderProcessor.getOrders().get(orderProcessor.getOrders().size() - 1).getOrderId() + 1;
                            Order newOrder = new Order(newOrderId, customerName, deliveryDetails, pastryName, pastryQuantity, orderStatus,
                                    LocalDateTime.now(), LocalDateTime.now());

                            boolean addedToDatabase = orderProcessor.addOrderToDatabase(newOrder, connection);

                            if (addedToDatabase) {
                                boolean quantitiesUpdated = PastryManager.updatePastryQuantityAndCheck(connection, pastryName, pastryQuantity);
                                if (quantitiesUpdated) {
                                    JOptionPane.showMessageDialog(frame, "Order added successfully.",
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                                    populateOrderData();
                                    populateInventoryData();
                                } else {
                                    JOptionPane.showMessageDialog(frame, "Insufficient pastry quantities to fulfill the order.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(frame, "Failed to add order to the database.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error while connecting to the database.",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
                displayOrderStatus();
            }
        });
        deleteOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String orderIdString = JOptionPane.showInputDialog(frame, "Enter the Order ID:", "Delete Order", JOptionPane.QUESTION_MESSAGE);

                if (orderIdString == null || orderIdString.isEmpty()) {
                    return;
                }
                try {
                    int orderId = Integer.parseInt(orderIdString);

                    try (Connection connection = DatabaseConnection.getConnection()) {
                        if (connection != null) {
                            boolean deleted = orderProcessor.deleteOrderFromDatabase(orderId, connection);

                            if (deleted) {
                                JOptionPane.showMessageDialog(frame, "Order deleted successfully.",
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
                                populateOrderData();
                                populateInventoryData();
                            } else {
                                JOptionPane.showMessageDialog(frame, "Failed to delete order from the database.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error while connecting to the database.",
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid Order ID. Please enter a valid number.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
                displayOrderStatus();
            }
        });

        setButtonStyle(pastryButton, Color.decode("#2ecc71"), Color.WHITE);
        setButtonStyle(ingredientButton, Color.decode("#2ecc71"), Color.WHITE);
        setButtonStyle(orderButton, Color.decode("#2ecc71"), Color.WHITE);
        setButtonStyle(calculatePriceButton, Color.decode("#e67e22"), Color.WHITE);
        setButtonStyle(addIngredientButton, Color.decode("#2ecc71"), Color.WHITE);
        setButtonStyle(removeIngredientButton, Color.decode("#2ecc71"), Color.WHITE);
        setButtonStyle(addPastryButton, Color.decode("#2ecc71"), Color.WHITE);
        setButtonStyle(removePastryButton, Color.decode("#2ecc71"), Color.WHITE);
        setButtonStyle(addOrderButton, Color.decode("#2ecc71"), Color.WHITE);
        setButtonStyle(removeOrderButton, Color.decode("#e74c3c"), Color.WHITE);

        headerPanel = new JPanel();
        headerPanel.setBackground(Color.ORANGE);
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 30));
        headerPanel.add(titleLabel);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.add(pastryButton);
        buttonPanel.add(ingredientButton);
        buttonPanel.add(orderButton);

        JPanel inputPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        inputPanel.add(hoursLabel);
        inputPanel.add(hoursField);
        inputPanel.add(new JLabel());
        inputPanel.add(ingredientCostLabel);
        inputPanel.add(ingredientCostField);
        inputPanel.add(new JLabel());
        inputPanel.add(calculatePriceButton);

        JPanel ingredientPanel = new JPanel(new GridLayout(3, 1, 1, 1));
        ingredientPanel.add(ingredientNameLabel);
        ingredientPanel.add(ingredientNameField);
        ingredientPanel.add(ingredientQuantityLabel);
        ingredientPanel.add(ingredientQuantityField);
        ingredientPanel.add(addIngredientButton);
        ingredientPanel.add(removeIngredientButton);

        JPanel pastryPanel = new JPanel(new GridLayout(4, 3, 2, 2));
        pastryPanel.add(pastryNameLabel);
        pastryPanel.add(pastryNameField);
        pastryPanel.add(pastryQuantityLabel);
        pastryPanel.add(pastryQuantityField);
        pastryPanel.add(pastryPriceLabel);
        pastryPanel.add(pastryPriceField);
        pastryPanel.add(addPastryButton);
        pastryPanel.add(removePastryButton);

        JPanel orderPanel = new JPanel(new GridLayout(3, 2, 1, 2));
        orderPanel.add(customerNameLabel);
        orderPanel.add(customerNameField);
        orderPanel.add(pastryOrderLabel);
        orderPanel.add(pastryOrderField);
        orderPanel.add(deliveryDetailsLabel);
        orderPanel.add(deliveryDetailsField);
        orderPanel.add(pastryQuantityOrderLabel);
        orderPanel.add(pastryQuantityOrderField);

        orderPanel.add(statusLabel);
        orderPanel.add(statusComboBox);
        orderPanel.add(addOrderButton);
        orderPanel.add(deleteOrderButton);

        JPanel inputContainerPanel = new JPanel(new GridLayout(4, 3, 1, 1));
        inputContainerPanel.add(ingredientPanel);
        inputContainerPanel.add(pastryPanel);

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        displayPanel.setPreferredSize(new Dimension(frame.getWidth(),frame.getHeight()));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(inputContainerPanel, BorderLayout.CENTER);
        contentPanel.add(displayPanel, BorderLayout.EAST);

        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.BEFORE_LINE_BEGINS);

        Font textFieldFont = new Font("Arial", Font.PLAIN, 14);
        Color textFieldBackground = Color.WHITE;
        Color textFieldForeground = Color.BLACK;

        ingredientNameField.setFont(textFieldFont);
        ingredientNameField.setBackground(textFieldBackground);
        ingredientNameField.setForeground(textFieldForeground);

        ingredientQuantityField.setFont(textFieldFont);
        ingredientQuantityField.setBackground(textFieldBackground);
        ingredientQuantityField.setForeground(textFieldForeground);

        pastryNameField.setFont(textFieldFont);
        pastryNameField.setBackground(textFieldBackground);
        pastryNameField.setForeground(textFieldForeground);

        pastryQuantityField.setFont(textFieldFont);
        pastryQuantityField.setBackground(textFieldBackground);
        pastryQuantityField.setForeground(textFieldForeground);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pastryButton.setBorder(new LineBorder(Color.decode("#2ecc71"), 2, true));
                ingredientButton.setBorder(new LineBorder(Color.decode("#2ecc71"), 2, true));
                orderButton.setBorder(new LineBorder(Color.decode("#2ecc71"), 2, true));
                calculatePriceButton.setBorder(new LineBorder(Color.decode("#e67e22"), 2, true));
                addIngredientButton.setBorder(new LineBorder(Color.decode("#2ecc71"), 2, true));
                removeIngredientButton.setBorder(new LineBorder(Color.decode("#2ecc71"), 2, true));
                addPastryButton.setBorder(new LineBorder(Color.decode("#2ecc71"), 2, true));
                removePastryButton.setBorder(new LineBorder(Color.decode("#2ecc71"), 2, true));

                frame.setVisible(true);
            }
        });

        populateInventoryData();
        populateOrderData();
        inputContainerPanel.add(ingredientPanel);
        inputContainerPanel.add(pastryPanel);
        inputContainerPanel.add(orderPanel);
        populateInventoryData();
        populateOrderData();
    }
    void setButtonStyle(JButton button, Color bgColor, Color fgColor) {
        button.setUI(new BasicButtonUI());
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(new RoundedBorder(4, bgColor));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setButtonSize(button, 150, 30);
    }
    private void setButtonSize(JButton button, int width, int height) {
        Dimension buttonSize = new Dimension(width, height);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setMinimumSize(buttonSize);
    }
    void displayPastryInventory() {
        System.out.println("Displaying pastry inventory");

        DatabaseManager databaseManager = new DatabaseManager();
        List<Pastry> pastryInventory = databaseManager.retrievePastryData();

        StringBuilder inventoryBuilder = new StringBuilder();
        for (Pastry pastry : pastryInventory) {
            inventoryBuilder.append("Name: ").append(pastry.getName())
                    .append(", Quantity: ").append(pastry.getQuantity())
                    .append(", Price: ").append(pastry.getPrice())
                    .append("\n");
        }
        displayArea.setText(inventoryBuilder.toString());
    }

    void displayIngredientInventory() {
        System.out.println("Displaying ingredient inventory");

        DatabaseManager databaseManager = new DatabaseManager();
        List<Ingredient> ingredientInventory = databaseManager.retrieveIngredientData();

        StringBuilder inventoryBuilder = new StringBuilder();
        for (Ingredient ingredient : ingredientInventory) {
            inventoryBuilder.append("Name: ").append(ingredient.getName())
                    .append(", Quantity: ").append(ingredient.getQuantity())
                    .append(", Expiration Date: ").append(ingredient.getExpirationDate())
                    .append("\n");
        }
        displayArea.setText(inventoryBuilder.toString());
    }
    private void displayOrderStatus() {
        System.out.println("Displaying order status");

        DatabaseManager databaseManager = new DatabaseManager();
        List<Order> orders = databaseManager.retrieveOrderData();

        StringBuilder orderBuilder = new StringBuilder();
        for (Order order : orders) {
            orderBuilder.append("Order ID: ").append(order.getOrderId())
                    .append(", Customer Name: ").append(order.getCustomerName())
                    .append(", Delivery Details: ").append(order.getdeliveryDetails())
                    .append(", Pastry Name: ").append(order.getPastryName())
                    .append(", Quantity: ").append(order.getQuantity())
                    .append(", Status: ").append(order.getStatus())
                    .append(", Created At: ").append(order.getCreatedAt())
                    .append(", Updated At: ").append(order.getUpdatedAt())
                    .append("\n");
        }
        displayArea.setText(orderBuilder.toString());
    }
    void populateInventoryData() {
        pastryManager.addMissingPastries();
        IngredientManager.addMissingIngredients();
    }
    private void populateOrderData() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        Order order1 = new Order(1, "Georgiana Sima", "402 Marino Cir", "Croissant", 5, "Completed",LocalDateTime.now(), LocalDateTime.now());
        Order order2 = new Order(2, "Horatiu Bogzoiu", "5th avenue", "Chocolate Cake", 3, "Pending",LocalDateTime.now(), LocalDateTime.now());

        orderProcessor.addOrder(order1);
        orderProcessor.addOrder(order2);
    }
    class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }
        public boolean isBorderOpaque() {
            return true;
        }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(color);
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PastryManager pastryManagerInstance = new PastryManager(DatabaseConnection.getConnection(), new JFrame());
        new UserInterface();
    }
}
