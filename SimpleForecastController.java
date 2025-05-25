package com.finance.manager.controller;

import com.finance.manager.model.Transaction;
import com.finance.manager.model.User;
import com.finance.manager.utils.DataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple Forecast Controller
 * A simplified version of the forecast controller with minimal functionality
 */
public class SimpleForecastController {
    private static final Logger LOGGER = Logger.getLogger(SimpleForecastController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private ComboBox<String> categoryComboBox;
    
    @FXML
    private Button generateButton;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private LineChart<String, Number> forecastChart;
    
    @FXML
    private TableView<ForecastItem> forecastTable;
    
    @FXML
    private TableColumn<ForecastItem, String> dateColumn;
    
    @FXML
    private TableColumn<ForecastItem, String> categoryColumn;
    
    @FXML
    private TableColumn<ForecastItem, Double> amountColumn;
    
    @FXML
    private TableColumn<ForecastItem, String> notesColumn;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Button nextButton;
    
    private User currentUser;
    private DataService dataService;
    private List<Transaction> historicalTransactions;
    
    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        LOGGER.info("Initializing SimpleForecastController");
        
        try {
            // Get data service instance
            dataService = DataService.getInstance();
            
            // Set default values for date pickers
            startDatePicker.setValue(LocalDate.now());
            endDatePicker.setValue(LocalDate.now().plusMonths(1));
            
            // Set up category ComboBox
            categoryComboBox.getItems().addAll(
                "All Categories", "Food", "Transportation", "Housing", 
                "Entertainment", "Shopping"
            );
            categoryComboBox.setValue("All Categories");
            
            // Set up table columns
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateString"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
            
            // Initialize the table with sample data
            ObservableList<ForecastItem> sampleData = createSampleData();
            forecastTable.setItems(sampleData);
            
            // Initialize the chart with sample data
            updateChart(sampleData);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing SimpleForecastController", e);
            statusLabel.setText("Error initializing forecast view");
        }
    }
    
    /**
     * Initialize user data
     * @param user Current user
     */
    public void initData(User user) {
        LOGGER.info("Setting user data in SimpleForecastController");
        this.currentUser = user;
        
        if (user != null) {
            // Load user's historical transactions
            historicalTransactions = dataService.getUserTransactions(user.getUsername());
        }
    }
    
    /**
     * Handle forecast generation button click
     */
    @FXML
    public void handleGenerateForecast() {
        try {
            statusLabel.setText("Generating forecast...");
            
            // Validate date input
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            
            if (startDate == null || endDate == null) {
                statusLabel.setText("Please select start and end dates");
                return;
            }
            
            if (startDate.isAfter(endDate)) {
                statusLabel.setText("Start date must be before end date");
                return;
            }
            
            // Generate forecast data
            ObservableList<ForecastItem> forecastData;
            if (historicalTransactions != null && !historicalTransactions.isEmpty()) {
                forecastData = generateForecast(startDate, endDate);
            } else {
                forecastData = createSampleData(startDate, endDate);
            }
            
            // Update the UI with the generated data
            forecastTable.setItems(forecastData);
            updateChart(forecastData);
            
            statusLabel.setText("Forecast generated successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating forecast", e);
            statusLabel.setText("Error generating forecast: " + e.getMessage());
        }
    }
    
    /**
     * Generate forecast data based on historical transactions
     * @param startDate Start date
     * @param endDate End date
     * @return Forecast data
     */
    private ObservableList<ForecastItem> generateForecast(LocalDate startDate, LocalDate endDate) {
        ObservableList<ForecastItem> result = FXCollections.observableArrayList();
        String selectedCategory = categoryComboBox.getValue();
        
        try {
            // Simple forecasting logic - calculate average spending by category
            Map<String, Double> categoryAverages = new HashMap<>();
            
            // Calculate average daily spending by category
            for (Transaction t : historicalTransactions) {
                String category = t.getType();
                if (category == null || category.isEmpty()) {
                    category = "Other";
                }
                
                double amount = t.getAmount();
                categoryAverages.put(category, categoryAverages.getOrDefault(category, 0.0) + amount);
            }
            
            // Divide by number of days to get daily average
            int totalDays = historicalTransactions.isEmpty() ? 1 : 30; // Assume one month of data
            for (String category : categoryAverages.keySet()) {
                categoryAverages.put(category, categoryAverages.get(category) / totalDays);
            }
            
            // Generate daily forecast
            Random random = new Random();
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                // If filtered by category, only show that category
                if ("All Categories".equals(selectedCategory)) {
                    for (Map.Entry<String, Double> entry : categoryAverages.entrySet()) {
                        String category = entry.getKey();
                        double avgAmount = entry.getValue();
                        
                        // Add some randomness (±20%)
                        double randomFactor = 0.8 + (random.nextDouble() * 0.4);
                        double forecastAmount = avgAmount * randomFactor;
                        
                        if (forecastAmount >= 1.0) { // Only show significant amounts
                            result.add(new ForecastItem(
                                currentDate,
                                category,
                                Math.round(forecastAmount * 100) / 100.0,
                                "Based on historical spending patterns"
                            ));
                        }
                    }
                } else if (categoryAverages.containsKey(selectedCategory)) {
                    double avgAmount = categoryAverages.get(selectedCategory);
                    double randomFactor = 0.8 + (random.nextDouble() * 0.4);
                    double forecastAmount = avgAmount * randomFactor;
                    
                    result.add(new ForecastItem(
                        currentDate,
                        selectedCategory,
                        Math.round(forecastAmount * 100) / 100.0,
                        "Based on historical spending patterns"
                    ));
                }
                
                currentDate = currentDate.plusDays(1);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in forecast generation", e);
        }
        
        return result;
    }
    
    /**
     * Create sample forecast data
     * @return Sample forecast data
     */
    private ObservableList<ForecastItem> createSampleData() {
        return createSampleData(LocalDate.now(), LocalDate.now().plusMonths(1));
    }
    
    /**
     * Create sample forecast data for a date range
     * @param startDate Start date
     * @param endDate End date
     * @return Sample forecast data
     */
    private ObservableList<ForecastItem> createSampleData(LocalDate startDate, LocalDate endDate) {
        ObservableList<ForecastItem> sampleData = FXCollections.observableArrayList();
        String selectedCategory = categoryComboBox.getValue();
        
        String[] categories = {"Food", "Transportation", "Housing", "Entertainment", "Shopping"};
        Random random = new Random();
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if ("All Categories".equals(selectedCategory)) {
                // Add an entry for each category
                for (String category : categories) {
                    double amount = 10 + random.nextDouble() * 90; // Random amount between 10 and 100
                    sampleData.add(new ForecastItem(
                        currentDate,
                        category,
                        Math.round(amount * 100) / 100.0,
                        "Sample forecast data"
                    ));
                }
            } else {
                // Add an entry just for the selected category
                double amount = 10 + random.nextDouble() * 90;
                sampleData.add(new ForecastItem(
                    currentDate,
                    selectedCategory,
                    Math.round(amount * 100) / 100.0,
                    "Sample forecast data"
                ));
            }
            
            currentDate = currentDate.plusDays(1);
        }
        
        return sampleData;
    }
    
    /**
     * Update the chart with forecast data
     * @param data Forecast data
     */
    private void updateChart(ObservableList<ForecastItem> data) {
        forecastChart.getData().clear();
        
        try {
            // Group data by category
            Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();
            
            // Process each forecast item
            for (ForecastItem item : data) {
                String category = item.getCategory();
                String dateStr = item.getDateString();
                Double amount = item.getAmount();
                
                // Get or create series for this category
                if (!seriesMap.containsKey(category)) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(category);
                    seriesMap.put(category, series);
                }
                
                // Add data point to the series
                seriesMap.get(category).getData().add(new XYChart.Data<>(dateStr, amount));
            }
            
            // Add all series to the chart
            for (XYChart.Series<String, Number> series : seriesMap.values()) {
                forecastChart.getData().add(series);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating chart", e);
        }
    }
    
    /**
     * Handle previous page button click
     */
    @FXML
    public void handlePrevPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auto_import.fxml"));
            Parent root = loader.load();
            
            AutoImportController controller = loader.getController();
            controller.initData(currentUser);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to Auto Import page", e);
        }
    }
    
    /**
     * Handle next page button click
     */
    @FXML
    public void handleNextPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));
            Parent root = loader.load();
            
            SettingsController controller = loader.getController();
            controller.initData(currentUser);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to Settings page", e);
        }
    }
    
    /**
     * Forecast item class for the table
     */
    public static class ForecastItem {
        private final LocalDate date;
        private final String category;
        private final double amount;
        private final String notes;
        
        public ForecastItem(LocalDate date, String category, double amount, String notes) {
            this.date = date;
            this.category = category;
            this.amount = amount;
            this.notes = notes;
        }
        
        public LocalDate getDate() {
            return date;
        }
        
        public String getDateString() {
            return date.format(DATE_FORMATTER);
        }
        
        public String getCategory() {
            return category;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public String getNotes() {
            return notes;
        }
    }
} 
