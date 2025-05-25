package com.finance.manager.controller;

import com.finance.manager.model.Transaction;
import com.finance.manager.model.User;
import com.finance.manager.utils.DataService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Home Controller
 * Handles home page display and functionality
 */
public class HomeController implements Initializable {

    @FXML
    private Label totalExpenseLabel;

    @FXML
    private Label transactionCountLabel;

    @FXML
    private Label avgExpenseLabel;

    @FXML
    private VBox notificationArea;

    @FXML
    private TableView<Transaction> recentTransactionsTable;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> categoryColumn;

    @FXML
    private LineChart<String, Number> expenseTrendChart;

    @FXML
    private BarChart<String, Number> expenseBarChart;

    @FXML
    private PieChart categoryPieChart;
    
    @FXML
    private Button nextButton;

    @FXML
    private VBox homeContainer;
    
    @FXML
    private Label welcomeLabel;

    private User currentUser;
    private DataService dataService;
    private List<Transaction> transactions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get data service instance
        dataService = DataService.getInstance();
        
        // Initialize table columns
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();
            String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return new SimpleStringProperty(formattedDate);
        });
        
        typeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getType()));
        
        amountColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
        
        categoryColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategory()));
    }

    /**
     * Initialize user data
     * @param user Currently logged in user
     */
    public void initData(User user) {
        this.currentUser = user;
        
        // Load user transaction data
        transactions = dataService.getUserTransactions(user.getUsername());
        
        // If there are no transactions, generate some sample data
        if (transactions == null || transactions.isEmpty()) {
            generateSampleData();
        }
        
        // Update UI
        updateUI();
        
        // Update welcome message
        updateWelcomeMessage();
    }
    
    /**
     * Generate sample transaction data for demonstration
     */
    private void generateSampleData() {
        List<Transaction> sampleTransactions = new ArrayList<>();
        Random random = new Random();
        LocalDate today = LocalDate.now();
        
        // Sample categories
        String[] categories = {"Food", "Transportation", "Shopping", "Housing", "Entertainment", "Healthcare"};
        String[] types = {"Food", "Transportation", "Shopping", "Housing", "Entertainment", "Healthcare", "Salary", "Investment"};
        String[] paymentMethods = {"Cash", "Credit Card", "Debit Card", "Bank Transfer", "Mobile Payment"};
        
        // Generate 6 months of data
        for (int i = 180; i >= 0; i--) {
            // Generate 0-3 transactions per day
            int transactionsPerDay = random.nextInt(4);
            
            for (int j = 0; j < transactionsPerDay; j++) {
                LocalDate date = today.minusDays(i);
                String type = types[random.nextInt(types.length)];
                
                // Determine if income or expense
                boolean isIncome = "Salary".equals(type) || "Investment".equals(type);
                String category = isIncome ? "Income" : categories[random.nextInt(categories.length)];
                
                // Generate amount (larger for income)
                double amount = isIncome ? 
                    500 + random.nextDouble() * 3000 : // Income between 500 and 3500
                    10 + random.nextDouble() * 190;    // Expenses between 10 and 200
                
                // Round to 2 decimal places
                amount = Math.round(amount * 100) / 100.0;
                
                // Generate note
                String note = isIncome ? 
                    ("Salary".equals(type) ? "Monthly salary" : "Investment return") :
                    type + " expense";
                
                // Create transaction
                Transaction transaction = new Transaction(
                    date,
                    type,
                    amount,
                    paymentMethods[random.nextInt(paymentMethods.length)],
                    note,
                    category,
                    false
                );
                
                transaction.setUserId(currentUser.getUsername());
                sampleTransactions.add(transaction);
            }
        }
        
        // Save the sample transactions
        for (Transaction transaction : sampleTransactions) {
            dataService.addTransaction(currentUser.getUsername(), transaction);
        }
        
        // Reload the transactions
        transactions = dataService.getUserTransactions(currentUser.getUsername());
    }
    
    /**
     * Handle next page button event
     * @param event Event object
     */
    @FXML
    public void handleNextPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/expense.fxml"));
            Parent root = loader.load();
            
            ExpenseController controller = loader.getController();
            controller.initData(currentUser);
            
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update UI display
     */
    private void updateUI() {
        // Show default values when no transaction data exists
        if (transactions == null || transactions.isEmpty()) {
            totalExpenseLabel.setText("0.00");
            transactionCountLabel.setText("0");
            avgExpenseLabel.setText("0.00");
            
            // Clear table
            recentTransactionsTable.setItems(FXCollections.observableArrayList());
            
            // Clear charts
            categoryPieChart.setData(FXCollections.observableArrayList());
            expenseTrendChart.getData().clear();
            expenseBarChart.getData().clear();
            
            return;
        }
        
        // Calculate statistics
        DoubleSummaryStatistics stats = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .summaryStatistics();
        
        // Set statistics data
        totalExpenseLabel.setText(String.format("%.2f", stats.getSum()));
        transactionCountLabel.setText(String.valueOf(stats.getCount()));
        avgExpenseLabel.setText(String.format("%.2f", stats.getAverage()));
        
        // Update recent transactions table
        updateRecentTransactionsTable();
        
        // Update charts
        updateCharts();
    }
    
    /**
     * Update recent transactions table
     */
    private void updateRecentTransactionsTable() {
        // Get the latest 10 transactions
        List<Transaction> recentTransactions = transactions.stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .limit(10)
                .collect(Collectors.toList());
        
        // Update table
        recentTransactionsTable.setItems(FXCollections.observableArrayList(recentTransactions));
    }
    
    /**
     * Update charts
     */
    private void updateCharts() {
        // Update category pie chart
        updateCategoryPieChart();
        
        // Update trend chart
        updateTrendChart();
        
        // Update bar chart
        updateBarChart();
    }
    
    /**
     * Update category pie chart
     */
    private void updateCategoryPieChart() {
        // Group and calculate by category
        Map<String, Double> categoryTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        
        // Prepare pie chart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        categoryTotals.forEach((category, total) -> {
            pieChartData.add(new PieChart.Data(category + " $" + String.format("%.2f", total), total));
        });
        
        // Update pie chart
        categoryPieChart.setData(pieChartData);
    }
    
    /**
     * Update trend chart
     */
    private void updateTrendChart() {
        // Clear existing data
        expenseTrendChart.getData().clear();
        
        // Group by month and calculate totals
        Map<String, Double> monthlyTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        
        // Create data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Expense");
        
        // Add data to series
        monthlyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });
        
        // Add series to chart
        expenseTrendChart.getData().add(series);
    }
    
    /**
     * Update bar chart
     */
    private void updateBarChart() {
        // Clear existing data
        expenseBarChart.getData().clear();
        
        // Group by type and calculate totals
        Map<String, Double> typeTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getType,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        
        // Create data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expense by Type");
        
        // Add data to series
        typeTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((a, b) -> Double.compare(b, a)))
                .limit(5)
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        });
        
        // Add series to chart
        expenseBarChart.getData().add(series);
    }

    /**
     * Update welcome message
     */
    private void updateWelcomeMessage() {
        if (currentUser != null && welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
        }
    }
} 
