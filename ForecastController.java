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
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Forecast Controller
 * Handles consumption forecasting and analysis functionality
 */
public class ForecastController {
    private static final Logger LOGGER = Logger.getLogger(ForecastController.class.getName());

    @FXML
    private ComboBox<String> periodComboBox;
    
    @FXML
    private ComboBox<String> categoryComboBox;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private Button generateButton;
    
    @FXML
    private TabPane chartTabPane;
    
    @FXML
    private LineChart<String, Number> trendChart;
    
    @FXML
    private PieChart categoryPieChart;
    
    @FXML
    private TableView<ForecastItem> forecastTable;
    
    @FXML
    private TableColumn<ForecastItem, String> dateColumn;
    
    @FXML
    private TableColumn<ForecastItem, String> categoryColumn;
    
    @FXML
    private TableColumn<ForecastItem, Double> amountColumn;
    
    @FXML
    private TableColumn<ForecastItem, Double> confidenceColumn;
    
    @FXML
    private TableColumn<ForecastItem, String> notesColumn;
    
    @FXML
    private Text totalExpenseText;
    
    @FXML
    private Text topCategoryText;
    
    @FXML
    private Text peakDayText;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label confidenceLabel;
    
    private User currentUser;
    private DataService dataService;
    private List<Transaction> historicalTransactions;
    private List<ForecastItem> predictions;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
    
    /**
     * Initialization method
     * Automatically called by JavaFX after loading FXML
     */
    @FXML
    public void initialize() {
        LOGGER.info("Initializing ForecastController");
        
        try {
            // Get data service instance
            dataService = DataService.getInstance();
            
            // Set ComboBox contents
            if (periodComboBox != null) {
                periodComboBox.getItems().addAll("Next Week", "Next Month", "Next 3 Months", "Next 6 Months", "Next Year");
                periodComboBox.setValue("Next Month");
            } else {
                LOGGER.warning("periodComboBox is null in initialize()");
            }
            
            if (categoryComboBox != null) {
                categoryComboBox.getItems().addAll("All Categories", "Food", "Transportation", "Shopping", "Entertainment", "Housing");
                categoryComboBox.setValue("All Categories");
            } else {
                LOGGER.warning("categoryComboBox is null in initialize()");
            }
            
            // Set table column cell value factories
            if (forecastTable != null) {
                if (dateColumn != null) dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateString"));
                if (categoryColumn != null) categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
                if (amountColumn != null) amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
                if (confidenceColumn != null) confidenceColumn.setCellValueFactory(new PropertyValueFactory<>("confidence"));
                if (notesColumn != null) notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
            } else {
                LOGGER.warning("forecastTable is null in initialize()");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing ForecastController", e);
        }
    }
    
    /**
     * Initialize user data
     * @param user Current user
     */
    public void initData(User user) {
        LOGGER.info("Initializing data for ForecastController");
        
        try {
            currentUser = user;
            
            if (currentUser == null) {
                LOGGER.warning("User is null in initData()");
                return;
            }
            
            // Load historical data
            loadHistoricalData();
            
            // Generate sample forecast
            generateSampleForecast();
            
            // Update chart
            updateTrendChart();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in initData", e);
        }
    }
    
    /**
     * Generate sample forecast data for demonstration
     */
    private void generateSampleForecast() {
        if (historicalTransactions == null || historicalTransactions.isEmpty()) {
            // Create sample forecast data
            List<ForecastItem> sampleForecast = new ArrayList<>();
            
            // Generate 30 days of sample forecast data
            LocalDate today = LocalDate.now();
            String[] categories = {"Food", "Transportation", "Shopping", "Entertainment", "Housing"};
            Random random = new Random();
            
            for (int i = 1; i <= 30; i++) {
                LocalDate forecastDate = today.plusDays(i);
                String category = categories[random.nextInt(categories.length)];
                double amount = 20 + random.nextDouble() * 180; // Random amount between 20 and 200
                double confidence = 0.4 + random.nextDouble() * 0.5; // Random confidence between 0.4 and 0.9
                String notes = "Based on historical spending patterns";
                
                sampleForecast.add(new ForecastItem(forecastDate, category, amount, confidence, notes));
            }
            
            // Update forecast display
            if (forecastTable != null) {
                forecastTable.setItems(FXCollections.observableArrayList(sampleForecast));
            }
            
            // Update charts and summary
            updateTrendChart(sampleForecast);
            updateCategoryPieChart(sampleForecast);
            updatePredictionSummary(sampleForecast);
        }
    }
    
    /**
     * Load user's historical transaction data
     */
    private void loadHistoricalData() {
        historicalTransactions = dataService.getUserTransactions(currentUser.getUsername());
        
        // Set category dropdown
        updateCategoryComboBox();
    }
    
    /**
     * Update category dropdown
     */
    private void updateCategoryComboBox() {
        if (historicalTransactions == null || historicalTransactions.isEmpty()) {
            return;
        }
        
        // Get all user's expense categories
        Set<String> categories = new HashSet<>();
        categories.add("All Categories");
        
        for (Transaction transaction : historicalTransactions) {
            if (transaction.getType() != null && !transaction.getType().isEmpty()) {
                categories.add(transaction.getType());
            }
        }
        
        // Update category dropdown
        categoryComboBox.getItems().clear();
        categoryComboBox.getItems().addAll(categories);
        categoryComboBox.setValue("All Categories");
    }
    
    /**
     * Handle forecast generation button click
     */
    @FXML
    public void handleGenerateForecast() {
        try {
            statusLabel.setText("Generating forecast...");
            
            // Get selected dates from date pickers
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
            
            // Calculate days between start and end dates
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            
            if (days > 90) {
                statusLabel.setText("Forecast period too long (max 90 days)");
                return;
            }
            
            // Generate forecast data
            predictions = generateForecastData(startDate, endDate);
            
            if (predictions.isEmpty()) {
                statusLabel.setText("Not enough historical data to generate forecast");
                return;
            }
            
            // Update UI with forecast results
            updateTrendChart(predictions);
            updateCategoryPieChart(predictions);
            updatePredictionSummary(predictions);
            
            // Calculate confidence level based on amount of historical data
            int historyPoints = historicalTransactions.size();
            int confidencePercentage = Math.min(95, Math.max(50, historyPoints * 5));
            confidenceLabel.setText("Confidence Level: " + confidencePercentage + "%");
            
            statusLabel.setText("Forecast generated successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating forecast", e);
            statusLabel.setText("Error generating forecast");
        }
    }
    
    /**
     * Calculate daily average expense by category
     * @param transactions Historical transactions
     * @return Map of category to daily average amount
     */
    private Map<String, Double> calculateDailyAverageByCategory(List<Transaction> transactions) {
        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, Integer> categoryDays = new HashMap<>();
        
        if (transactions.isEmpty()) {
            return categoryTotals;
        }
        
        // Find min and max dates to calculate date range
        LocalDate minDate = LocalDate.now();
        LocalDate maxDate = LocalDate.now();
        
        for (Transaction t : transactions) {
            if (t.getDate().isBefore(minDate)) {
                minDate = t.getDate();
            }
            if (t.getDate().isAfter(maxDate)) {
                maxDate = t.getDate();
            }
        }
        
        // Calculate total days in date range
        long totalDays = ChronoUnit.DAYS.between(minDate, maxDate) + 1;
        if (totalDays < 1) totalDays = 1; // Avoid division by zero
        
        // Sum expenses by category
        for (Transaction t : transactions) {
            String category = t.getType();
            if (category == null || category.isEmpty()) {
                category = "Uncategorized";
            }
            
            double amount = t.getAmount();
            
            // Add to category total
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            
            // Count days with transactions in this category
            LocalDate transactionDate = t.getDate();
            String dateKey = category + "_" + transactionDate;
            if (!categoryDays.containsKey(dateKey)) {
                categoryDays.put(dateKey, 1);
                categoryDays.put(category, categoryDays.getOrDefault(category, 0) + 1);
            }
        }
        
        // Calculate daily averages
        Map<String, Double> dailyAverages = new HashMap<>();
        
        for (String category : categoryTotals.keySet()) {
            int daysWithCategory = categoryDays.getOrDefault(category, 1);
            double totalAmount = categoryTotals.get(category);
            double dailyAverage = totalAmount / daysWithCategory;
            
            dailyAverages.put(category, dailyAverage);
        }
        
        return dailyAverages;
    }
    
    /**
     * Calculate spending pattern by day of month
     * @param transactions Historical transactions
     * @return Map of day of month to spending factor
     */
    private Map<Integer, Double> getDayOfMonthPattern(List<Transaction> transactions) {
        Map<Integer, Double> dayTotals = new HashMap<>();
        Map<Integer, Integer> dayCounts = new HashMap<>();
        
        // Sum expenses by day of month
        for (Transaction t : transactions) {
            int dayOfMonth = t.getDate().getDayOfMonth();
            double amount = t.getAmount();
            
            dayTotals.put(dayOfMonth, dayTotals.getOrDefault(dayOfMonth, 0.0) + amount);
            dayCounts.put(dayOfMonth, dayCounts.getOrDefault(dayOfMonth, 0) + 1);
        }
        
        // Calculate average amount per day
        Map<Integer, Double> dayAverages = new HashMap<>();
        
        for (int day = 1; day <= 31; day++) {
            if (dayCounts.containsKey(day)) {
                double total = dayTotals.get(day);
                int count = dayCounts.get(day);
                dayAverages.put(day, total / count);
            }
        }
        
        // Calculate overall average
        double overallAverage = dayAverages.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(1.0);
        
        if (overallAverage == 0.0) overallAverage = 1.0; // Avoid division by zero
        
        // Calculate day factors relative to overall average
        Map<Integer, Double> dayFactors = new HashMap<>();
        
        for (int day = 1; day <= 31; day++) {
            if (dayAverages.containsKey(day)) {
                dayFactors.put(day, dayAverages.get(day) / overallAverage);
            } else {
                dayFactors.put(day, 1.0); // Default to average
            }
        }
        
        return dayFactors;
    }
    
    /**
     * Calculate spending pattern by day of week
     * @param transactions Historical transactions
     * @return Map of day of week to spending factor
     */
    private Map<Integer, Double> getDayOfWeekPattern(List<Transaction> transactions) {
        Map<Integer, Double> dayTotals = new HashMap<>();
        Map<Integer, Integer> dayCounts = new HashMap<>();
        
        // Sum expenses by day of week
        for (Transaction t : transactions) {
            int dayOfWeek = t.getDate().getDayOfWeek().getValue(); // 1-7 (Monday-Sunday)
            double amount = t.getAmount();
            
            dayTotals.put(dayOfWeek, dayTotals.getOrDefault(dayOfWeek, 0.0) + amount);
            dayCounts.put(dayOfWeek, dayCounts.getOrDefault(dayOfWeek, 0) + 1);
        }
        
        // Calculate average amount per day of week
        Map<Integer, Double> dayAverages = new HashMap<>();
        
        for (int day = 1; day <= 7; day++) {
            if (dayCounts.containsKey(day)) {
                double total = dayTotals.get(day);
                int count = dayCounts.get(day);
                dayAverages.put(day, total / count);
            }
        }
        
        // Calculate overall average
        double overallAverage = dayAverages.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(1.0);
        
        if (overallAverage == 0.0) overallAverage = 1.0; // Avoid division by zero
        
        // Calculate day factors relative to overall average
        Map<Integer, Double> dayFactors = new HashMap<>();
        
        for (int day = 1; day <= 7; day++) {
            if (dayAverages.containsKey(day)) {
                dayFactors.put(day, dayAverages.get(day) / overallAverage);
            } else {
                dayFactors.put(day, 1.0); // Default to average
            }
        }
        
        return dayFactors;
    }
    
    /**
     * Calculate confidence level for predictions
     * @param category Category being predicted
     * @param dataPoints Number of historical data points available
     * @return Confidence level between 0 and 1
     */
    private double calculateConfidence(String category, int dataPoints) {
        // Base confidence depends on amount of historical data
        double baseConfidence = Math.min(0.8, 0.3 + (dataPoints / 100.0) * 0.5);
        
        // Adjust based on category predictability
        double categoryAdjustment = 0.0;
        if ("Rent".equals(category) || "Housing".equals(category) || "Utilities".equals(category)) {
            categoryAdjustment = 0.15; // More predictable categories
        } else if ("Entertainment".equals(category) || "Shopping".equals(category)) {
            categoryAdjustment = -0.1; // Less predictable categories
        }
        
        return Math.min(0.95, Math.max(0.3, baseConfidence + categoryAdjustment));
    }
    
    /**
     * Update trend chart with forecast data
     */
    private void updateTrendChart() {
        if (predictions != null) {
            updateTrendChart(predictions);
        }
    }
    
    /**
     * Update trend chart with forecast data
     * @param predictions List of forecast items to display
     */
    private void updateTrendChart(List<ForecastItem> forecastItems) {
        if (trendChart == null || forecastItems == null || forecastItems.isEmpty()) {
            LOGGER.warning("Cannot update trend chart: chart or data is null/empty");
            return;
        }

        try {
            // Clear existing data
            trendChart.getData().clear();

            // Create a new series for the data
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Predicted Expenses");

            // Group by date and sum amounts
            Map<LocalDate, Double> dailyTotals = new TreeMap<>();
            
            for (ForecastItem item : forecastItems) {
                LocalDate date = item.getDate();
                double amount = item.getAmount();
                
                if (dailyTotals.containsKey(date)) {
                    dailyTotals.put(date, dailyTotals.get(date) + amount);
                } else {
                    dailyTotals.put(date, amount);
                }
            }
            
            // Add data points to the series
            for (Map.Entry<LocalDate, Double> entry : dailyTotals.entrySet()) {
                String dateStr = entry.getKey().format(FORMATTER);
                series.getData().add(new XYChart.Data<>(dateStr, entry.getValue()));
            }
            
            // Add the series to the chart
            trendChart.getData().add(series);
            
            // Style the chart
            trendChart.setTitle("Expense Trend");
            trendChart.setAnimated(false); // Disable animation for better performance
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating trend chart", e);
        }
    }
    
    /**
     * Update category pie chart with forecast data
     * @param predictions Forecast predictions
     */
    private void updateCategoryPieChart(List<ForecastItem> predictions) {
        if (categoryPieChart == null || predictions == null || predictions.isEmpty()) {
            return;
        }
        
        categoryPieChart.getData().clear();
        
        // Sum amounts by category
        Map<String, Double> categoryTotals = new HashMap<>();
        
        for (ForecastItem item : predictions) {
            String category = item.getCategory();
            double amount = item.getAmount();
            
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }
        
        // Create pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(
                    entry.getKey() + " ($" + String.format("%.2f", entry.getValue()) + ")",
                    entry.getValue()
            ));
        }
        
        categoryPieChart.setData(pieChartData);
    }
    
    /**
     * Update prediction summary
     * @param predictions Forecast predictions
     */
    private void updatePredictionSummary(List<ForecastItem> predictions) {
        if (predictions == null || predictions.isEmpty()) {
            return;
        }
        
        // Calculate total predicted expense
        double totalExpense = predictions.stream()
                .mapToDouble(ForecastItem::getAmount)
                .sum();
        
        // Find top spending category
        Map<String, Double> categoryTotals = new HashMap<>();
        
        for (ForecastItem item : predictions) {
            String category = item.getCategory();
            double amount = item.getAmount();
            
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }
        
        String topCategory = categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
        
        double topCategoryAmount = categoryTotals.getOrDefault(topCategory, 0.0);
        
        // Find peak spending day
        Map<String, Double> dailyTotals = new HashMap<>();
        
        for (ForecastItem item : predictions) {
            String dateString = item.getDateString();
            double amount = item.getAmount();
            
            dailyTotals.put(dateString, dailyTotals.getOrDefault(dateString, 0.0) + amount);
        }
        
        String peakDay = dailyTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
        
        double peakDayAmount = dailyTotals.getOrDefault(peakDay, 0.0);
        
        // Update UI
        if (totalExpenseText != null) {
            totalExpenseText.setText(String.format("$%.2f", totalExpense));
        }
        
        if (topCategoryText != null) {
            topCategoryText.setText(topCategory + " ($" + String.format("%.2f", topCategoryAmount) + ")");
        }
        
        if (peakDayText != null) {
            peakDayText.setText(peakDay + " ($" + String.format("%.2f", peakDayAmount) + ")");
        }
    }
    
    /**
     * Handle previous page button click
     * @param event Event object
     */
    @FXML
    public void handlePrevPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transaction.fxml"));
            Parent root = loader.load();
            
            TransactionController controller = loader.getController();
            controller.initData(currentUser);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to navigate to Transaction page", e);
        }
    }
    
    /**
     * Handle next page button click
     * @param event Event object
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
     * Calculate average amount for a given category based on historical data
     * @param category Transaction category
     * @return Average amount for the category
     */
    private double calculateAverageAmount(String category) {
        if (historicalTransactions == null || historicalTransactions.isEmpty()) {
            return 50.0; // Default amount if no historical data
        }
        
        // Filter transactions by category if not "All Categories"
        List<Transaction> filteredTransactions = historicalTransactions.stream()
                .filter(t -> "All Categories".equals(category) || category.equals(t.getType()))
                .collect(Collectors.toList());
        
        if (filteredTransactions.isEmpty()) {
            return 50.0; // Default amount if no matching transactions
        }
        
        // Calculate average amount
        double totalAmount = filteredTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        return totalAmount / filteredTransactions.size();
    }
    
    /**
     * Generate forecast data based on historical transactions
     * @param startDate Start date for forecast
     * @param endDate End date for forecast
     * @return List of forecast items
     */
    private List<ForecastItem> generateForecastData(LocalDate startDate, LocalDate endDate) {
        List<ForecastItem> forecastItems = new ArrayList<>();
        
        if (historicalTransactions == null || historicalTransactions.isEmpty()) {
            return forecastItems;
        }
        
        try {
            // Get selected category
            String selectedCategory = categoryComboBox.getValue();
            
            // Calculate daily averages by category
            Map<String, Double> dailyAverages = calculateDailyAverageByCategory(historicalTransactions);
            
            // Get day of week and day of month patterns
            Map<Integer, Double> dayOfWeekPatterns = getDayOfWeekPattern(historicalTransactions);
            Map<Integer, Double> dayOfMonthPatterns = getDayOfMonthPattern(historicalTransactions);
            
            // Generate forecast for each day in the range
            LocalDate currentDate = startDate;
            Random random = new Random();
            
            while (!currentDate.isAfter(endDate)) {
                // For each relevant category
                for (String category : dailyAverages.keySet()) {
                    // Skip if filtering by category and this isn't the selected one
                    if (!"All Categories".equals(selectedCategory) && !category.equals(selectedCategory)) {
                        continue;
                    }
                    
                    // Get base average amount
                    double baseAmount = dailyAverages.getOrDefault(category, 50.0);
                    
                    // Apply day of week pattern
                    int dayOfWeek = currentDate.getDayOfWeek().getValue();
                    double dayOfWeekFactor = dayOfWeekPatterns.getOrDefault(dayOfWeek, 1.0);
                    
                    // Apply day of month pattern
                    int dayOfMonth = currentDate.getDayOfMonth();
                    double dayOfMonthFactor = dayOfMonthPatterns.getOrDefault(dayOfMonth, 1.0);
                    
                    // Apply some randomness
                    double randomFactor = 0.8 + (random.nextDouble() * 0.4); // Between 0.8 and 1.2
                    
                    // Calculate predicted amount
                    double predictedAmount = baseAmount * dayOfWeekFactor * dayOfMonthFactor * randomFactor;
                    
                    // Calculate confidence level
                    double confidence = calculateConfidence(category, historicalTransactions.size());
                    
                    // Skip very small predictions
                    if (predictedAmount >= 5.0) {
                        // Add to forecast
                        forecastItems.add(new ForecastItem(
                                currentDate,
                                category,
                                Math.round(predictedAmount * 100) / 100.0, // Round to 2 decimal places
                                confidence,
                                "Based on historical patterns"
                        ));
                    }
                }
                
                // Move to next day
                currentDate = currentDate.plusDays(1);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating forecast data", e);
        }
        
        return forecastItems;
    }
    
    /**
     * Forecast item for displaying in the forecast table
     */
    public static class ForecastItem {
        private final LocalDate date;
        private final String category;
        private final double amount;
        private final double confidence;
        private final String notes;
        
        public ForecastItem(LocalDate date, String category, double amount, double confidence, String notes) {
            this.date = date;
            this.category = category;
            this.amount = amount;
            this.confidence = confidence;
            this.notes = notes;
        }
        
        public LocalDate getDate() {
            return date;
        }
        
        public String getDateString() {
            return date.format(FORMATTER);
        }
        
        public String getCategory() {
            return category;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public double getConfidence() {
            return confidence;
        }
        
        public String getNotes() {  
            return notes;
        }
    }
}
