package com.finance.manager.controller;

import com.finance.manager.model.Transaction;
import com.finance.manager.model.User;
import com.finance.manager.utils.DataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Transaction Controller
 * Handles transaction management and display functionality
 */
public class TransactionController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(TransactionController.class.getName());
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private TableView<Transaction> transactionsTable;
    
    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;
    
    @FXML
    private TableColumn<Transaction, String> typeColumn;
    
    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    
    @FXML
    private TableColumn<Transaction, String> paymentMethodColumn;
    
    @FXML
    private TableColumn<Transaction, String> notesColumn;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private ComboBox<String> typeComboBox;
    
    @FXML
    private TextField amountField;
    
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private TextArea notesArea;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private DatePicker endDatePicker;
    
    @FXML
    private Button filterButton;
    
    @FXML
    private Text errorText;
    
    @FXML
    private Text successText;
    
    @FXML
    private ListView<String> categoryListView;
    
    @FXML
    private TextField categoryNameField;
    
    @FXML
    private ComboBox<String> iconComboBox;
    
    @FXML
    private TextArea categoryDescField;
    
    @FXML
    private TableView<Transaction> unclassifiedTable;
    
    @FXML
    private TableColumn<Transaction, LocalDate> ucDateColumn;
    
    @FXML
    private TableColumn<Transaction, String> ucDescriptionColumn;
    
    @FXML
    private TableColumn<Transaction, Double> ucAmountColumn;
    
    @FXML
    private TableColumn<Transaction, String> ucPaymentColumn;
    
    @FXML
    private ComboBox<String> classifyCategoryComboBox;
    
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private User currentUser;
    private DataService dataService;
    
    // Constants for dropdown options
    private final String[] TRANSACTION_TYPES = {
            "Food", "Transportation", "Shopping", "Housing", "Entertainment",
            "Education", "Healthcare", "Travel", "Salary", "Investment", "Other"
    };
    
    private final String[] PAYMENT_METHODS = {
            "Cash", "Credit Card", "Debit Card", "Bank Transfer", "Mobile Payment", "Other"
    };
    
    private final String[] CATEGORIES = {
            "Essentials", "Discretionary", "Savings", "Income", "Investment", "Debt"
    };
    
    /**
     * Initialize the controller
     * @param url The location used to resolve relative paths
     * @param rb The resources used by this controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.info("Initializing TransactionController");
        
        // Initialize data service
        dataService = DataService.getInstance();
        
        // Setup table columns
        setupTable();
        
        // Setup dropdown menus
        setupDropdowns();
        
        // Initialize date pickers
        setupDatePickers();
        
        // Setup error and success messages
        if (errorText != null) errorText.setVisible(false);
        if (successText != null) successText.setVisible(false);
        
        // Initialize tab event handling if tabPane exists
        if (tabPane != null) {
            tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab != null) {
                    LOGGER.info("Switched to tab: " + newTab.getText());
                }
            });
        }
    }
    
    /**
     * Setup table columns
     */
    private void setupTable() {
        // Configure table columns
        if (dateColumn != null) dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        if (typeColumn != null) typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        if (amountColumn != null) amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        if (paymentMethodColumn != null) paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        if (notesColumn != null) notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        
        // Configure amount column formatting
        if (amountColumn != null) {
            amountColumn.setCellFactory(column -> new TableCell<Transaction, Double>() {
                @Override
                protected void updateItem(Double amount, boolean empty) {
                    super.updateItem(amount, empty);
                    if (empty || amount == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f", amount));
                        // Expenses shown in red, income shown in green
                        Transaction transaction = getTableView().getItems().get(getIndex());
                        if (transaction != null) {
                            if ("Expense".equals(transaction.getType())) {
                                setTextFill(Color.RED);
                            } else {
                                setTextFill(Color.GREEN);
                            }
                        }
                    }
                }
            });
    }
    
        // Configure table row double-click event
        if (transactionsTable != null) {
            transactionsTable.setRowFactory(tv -> {
                TableRow<Transaction> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Transaction transaction = row.getItem();
                        populateFormWithTransaction(transaction);
                    }
                });
                return row;
            });
        }
        
        // Setup unclassified transactions table if exists
        if (unclassifiedTable != null) {
            if (ucDateColumn != null) ucDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            if (ucDescriptionColumn != null) ucDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
            if (ucAmountColumn != null) ucAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            if (ucPaymentColumn != null) ucPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        }
        }
        
    /**
     * Setup dropdown menus
     */
    private void setupDropdowns() {
        // Setup transaction type dropdown
        if (typeComboBox != null) {
            typeComboBox.setItems(FXCollections.observableArrayList(TRANSACTION_TYPES));
        }
        
        // Setup payment method dropdown
        if (paymentMethodComboBox != null) {
            paymentMethodComboBox.setItems(FXCollections.observableArrayList(PAYMENT_METHODS));
        }
        
        // Setup filter dropdown
        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList(TRANSACTION_TYPES));
        }
        
        // Setup category list view
        if (categoryListView != null) {
            categoryListView.setItems(FXCollections.observableArrayList(CATEGORIES));
        }
        
        // Setup icon combo box
        if (iconComboBox != null) {
            iconComboBox.setItems(FXCollections.observableArrayList("Icon 1", "Icon 2", "Icon 3"));
        }
        
        // Setup classification category combo box
        if (classifyCategoryComboBox != null) {
            classifyCategoryComboBox.setItems(FXCollections.observableArrayList(CATEGORIES));
        }
    }
    
    /**
     * Setup date pickers
     */
    private void setupDatePickers() {
        // Set default values
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }
        
        // Setup filter date pickers
        LocalDate now = LocalDate.now();
        if (startDatePicker != null) {
            startDatePicker.setValue(now.minusMonths(1));
        }
        if (endDatePicker != null) {
            endDatePicker.setValue(now);
        }
    }
    
    /**
     * Initialize user data
     * @param user Current user
     */
    public void initData(User user) {
        if (user != null) {
            LOGGER.info("Initializing user data for transactions: " + user.getUsername());
            this.currentUser = user;
            
            // Load user transactions
            loadTransactions();
            
            // If no transactions are found, add some sample data
            if (transactions.isEmpty()) {
                addSampleTransactions();
            }
        } else {
            LOGGER.warning("Attempted to initialize with null user");
        }
    }
    
    /**
     * Add sample transactions for demonstration purposes
     */
    private void addSampleTransactions() {
        if (currentUser == null) {
            LOGGER.warning("Cannot add sample transactions: current user is null");
            return;
        }
        
        LOGGER.info("Adding sample transactions for user: " + currentUser.getUsername());
        
        // Create a list of sample transactions
        List<Transaction> sampleTransactions = new ArrayList<>();
        
        // Add sample expense transactions
        sampleTransactions.add(new Transaction(
            LocalDate.now().minusDays(2),
            "Food",
            35.75,
            "Credit Card",
            "Dinner at Italian restaurant",
            "Essentials",
            false
        ));
        
        sampleTransactions.add(new Transaction(
            LocalDate.now().minusDays(5),
            "Transportation",
            28.50,
            "Cash",
            "Taxi fare",
            "Essentials",
            false
        ));
        
        sampleTransactions.add(new Transaction(
            LocalDate.now().minusDays(7),
            "Shopping",
            189.99,
            "Credit Card",
            "New headphones",
            "Discretionary",
            false
        ));
        
        sampleTransactions.add(new Transaction(
            LocalDate.now().minusDays(10),
            "Healthcare",
            75.00,
            "Insurance",
            "Doctor visit copay",
            "Essentials",
            false
        ));
        
        sampleTransactions.add(new Transaction(
            LocalDate.now().minusDays(12),
            "Housing",
            1200.00,
            "Bank Transfer",
            "Monthly rent payment",
            "Essentials",
            true
        ));
        
        // Add sample income transactions
        sampleTransactions.add(new Transaction(
            LocalDate.now().minusDays(1),
            "Salary",
            3500.00,
            "Bank Transfer",
            "Monthly salary",
            "Income",
            true
        ));
        
        sampleTransactions.add(new Transaction(
            LocalDate.now().minusDays(8),
            "Investment",
            250.75,
            "Bank Transfer",
            "Stock dividend",
            "Income",
            false
        ));
        
        // Add all sample transactions to the database
        for (Transaction transaction : sampleTransactions) {
            transaction.setUserId(currentUser.getUsername());
            dataService.addTransaction(currentUser.getUsername(), transaction);
        }
        
        // Reload transactions to display the newly added samples
        loadTransactions();
    }
    
    /**
     * Load user transactions
     */
    private void loadTransactions() {
        if (currentUser == null) {
            LOGGER.warning("Cannot load transactions: current user is null");
            return;
        }
        
        try {
            List<Transaction> userTransactions = dataService.getUserTransactions(currentUser.getUsername());
            
            // Clear existing transactions
            transactions.clear();
            if (userTransactions != null) {
                transactions.addAll(userTransactions);
        }
        
            // Update TableView if it exists
        if (transactionsTable != null) {
                transactionsTable.setItems(transactions);
            }
            
            // Also update unclassified table if it exists
            if (unclassifiedTable != null) {
                List<Transaction> unclassified = userTransactions != null ? 
                    userTransactions.stream()
                        .filter(t -> t.getCategory() == null || t.getCategory().isEmpty())
                        .collect(Collectors.toList()) : 
                    new ArrayList<>();
                unclassifiedTable.setItems(FXCollections.observableArrayList(unclassified));
            }
        } catch (Exception e) {
            LOGGER.severe("Error loading transactions: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Loading Transactions Failed", e.getMessage());
        }
    }
    
    /**
     * Handle save button click
     * @param event The action event
     */
    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create transaction from form
            Transaction transaction = new Transaction(
                    datePicker.getValue(),
                    typeComboBox.getValue(),
                    Double.parseDouble(amountField.getText().trim()),
                    paymentMethodComboBox.getValue(),
                    notesArea.getText(),
                "Uncategorized", // Default category, can be changed later
                false
            );
            
            // Set user ID
            transaction.setUserId(currentUser.getUsername());
            
            // Add to data service
            dataService.addTransaction(currentUser.getUsername(), transaction);
            
            // Show success message
            displayMessage("Transaction successfully saved", true);
            
            // Clear form
            clearForm();
            
            // Reload transactions
            loadTransactions();
            
        } catch (NumberFormatException e) {
            displayMessage("Invalid amount format - please enter a valid number", false);
        } catch (Exception e) {
            displayMessage("Error saving transaction: " + e.getMessage(), false);
        }
    }
    
    /**
     * Handle clear button click
     * @param event The action event
     */
    @FXML
    private void handleClear(ActionEvent event) {
        clearForm();
        if (errorText != null) errorText.setVisible(false);
        if (successText != null) successText.setVisible(false);
    }
    
    /**
     * Handle delete button click
     * @param event The action event
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        if (transactionsTable == null) {
            displayMessage("Transaction table not initialized", false);
            return;
        }
        
        Transaction selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
        
        if (selectedTransaction == null) {
            displayMessage("Please select a transaction to delete", false);
            return;
        }
        
        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Transaction");
        confirmation.setContentText("Are you sure you want to delete this transaction?");
        
        // Process result
        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Delete from data service
                dataService.deleteTransaction(currentUser.getUsername(), selectedTransaction.getId());
                
                // Remove from list
                transactions.remove(selectedTransaction);
                
                // Show success message
                displayMessage("Transaction successfully deleted", true);
                
                // Reload data
                loadTransactions();
        }
        });
    }
    
    /**
     * Handle filter button click
     * @param event The action event
     */
    @FXML
    private void handleFilter(ActionEvent event) {
        if (startDatePicker == null || endDatePicker == null) {
            displayMessage("Date pickers not initialized", false);
            return;
        }
        
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        
        if (start == null || end == null) {
            displayMessage("Please select both start and end dates", false);
            return;
        }
        
        if (start.isAfter(end)) {
            displayMessage("Start date must be before end date", false);
            return;
        }
        
        // Filter transactions by date range
        List<Transaction> filteredTransactions = dataService.getUserTransactions(currentUser.getUsername())
                .stream()
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                    .collect(Collectors.toList());
        
        // Update table
        transactions.clear();
        transactions.addAll(filteredTransactions);
        
        displayMessage("Showing transactions from " + 
                start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " to " + 
                end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), true);
    }
    
    /**
     * Refresh the transaction list
     */
    @FXML
    private void handleRefresh() {
        LOGGER.info("Refreshing transaction list");
        loadTransactions();
        displayMessage("Transactions refreshed", true);
    }
    
    /**
     * Edit category
     */
    @FXML
    private void handleEditCategory() {
        LOGGER.info("Editing category");
        if (categoryListView == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Category Management", "Category list not initialized");
            return;
        }
        
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Category Selection", "Please select a category to edit");
            return;
        }
        
        // Populate category edit form
        if (categoryNameField != null) categoryNameField.setText(selectedCategory);
        if (categoryDescField != null) categoryDescField.setText("Description for " + selectedCategory);
        if (iconComboBox != null) iconComboBox.setValue("Icon 1");
    }
    
    /**
     * Delete category
     */
    @FXML
    private void handleDeleteCategory() {
        LOGGER.info("Deleting category");
        if (categoryListView == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Category Management", "Category list not initialized");
            return;
        }
        
        String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Category Selection", "Please select a category to delete");
            return;
        }
        
        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Category");
        confirmation.setContentText("Are you sure you want to delete the category '" + selectedCategory + "'?");
        
        // Process result
        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // In a real app, we would delete the category from the database
                // and update associated transactions
                // For now, just show a message
                showAlert(Alert.AlertType.INFORMATION, "Information", "Category Deleted", 
                        "Category '" + selectedCategory + "' has been deleted");
                
                // Clear selection
                categoryListView.getSelectionModel().clearSelection();
                if (categoryNameField != null) categoryNameField.clear();
                if (categoryDescField != null) categoryDescField.clear();
                if (iconComboBox != null) iconComboBox.setValue(null);
            }
        });
    }
    
    /**
     * Save category
     */
    @FXML
    private void handleSaveCategory() {
        LOGGER.info("Saving category");
        if (categoryNameField == null || categoryNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Category Name Required", 
                    "Please enter a name for the category");
            return;
        }
        
        String categoryName = categoryNameField.getText().trim();
        String description = categoryDescField != null ? categoryDescField.getText() : "";
        String icon = iconComboBox != null ? iconComboBox.getValue() : "Icon 1";
        
        // In a real app, we would save the category to the database
        // For now, just show a message
        showAlert(Alert.AlertType.INFORMATION, "Information", "Category Saved", 
                "Category '" + categoryName + "' has been saved");
        
        // Clear form
        if (categoryNameField != null) categoryNameField.clear();
        if (categoryDescField != null) categoryDescField.clear();
        if (iconComboBox != null) iconComboBox.setValue(null);
    }
    
    /**
     * Classify transactions manually
     */
    @FXML
    private void handleClassify() {
        LOGGER.info("Classifying transactions manually");
        if (unclassifiedTable == null || classifyCategoryComboBox == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Classification", "Classification components not initialized");
            return;
        }
        
        Transaction selectedTransaction = unclassifiedTable.getSelectionModel().getSelectedItem();
        String selectedCategory = classifyCategoryComboBox.getValue();
        
        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Transaction Selection", 
                    "Please select a transaction to classify");
            return;
        }
        
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Category Selection", 
                    "Please select a category to apply");
            return;
        }
        
        // In a real app, we would update the transaction in the database
        // For now, just show a message
        showAlert(Alert.AlertType.INFORMATION, "Information", "Transaction Classified", 
                "Transaction has been classified as '" + selectedCategory + "'");
        
        // Reload transactions
        loadTransactions();
    }
    
    /**
     * Auto-classify transactions
     */
    @FXML
    private void handleAutoClassify() {
        LOGGER.info("Auto-classifying transactions");
        
        // In a real app, we would implement an algorithm to classify transactions
        // based on their description, amount, etc.
        // For now, just show a message
        showAlert(Alert.AlertType.INFORMATION, "Information", "Auto-Classification", 
                "Auto-classification has been performed. All unclassified transactions have been categorized.");
        
        // Reload transactions
        loadTransactions();
    }
    
    /**
     * Validate the transaction form
     * @return True if the form is valid, false otherwise
     */
    private boolean validateForm() {
        // Validate date
        if (datePicker == null || datePicker.getValue() == null) {
            displayMessage("Please select a date", false);
            return false;
        }
        
        // Validate type
        if (typeComboBox == null || typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty()) {
            displayMessage("Please select a transaction type", false);
            return false;
        }
        
        // Validate amount
        if (amountField == null) {
            displayMessage("Amount field not initialized", false);
            return false;
        }
        
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            displayMessage("Please enter an amount", false);
            return false;
        }
        
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                displayMessage("Amount must be greater than zero", false);
                return false;
            }
        } catch (NumberFormatException e) {
            displayMessage("Invalid amount format - please enter a valid number", false);
            return false;
        }
        
        // Validate payment method
        if (paymentMethodComboBox == null || 
            paymentMethodComboBox.getValue() == null || 
            paymentMethodComboBox.getValue().isEmpty()) {
            displayMessage("Please select a payment method", false);
            return false;
        }
        
        return true;
    }
    
    /**
     * Display a message to the user
     * @param message The message to display
     * @param isSuccess Whether this is a success message
     */
    private void displayMessage(String message, boolean isSuccess) {
        // If we have success/error text fields, use them
        if (isSuccess && successText != null) {
            successText.setText(message);
            successText.setVisible(true);
            if (errorText != null) errorText.setVisible(false);
        } else if (!isSuccess && errorText != null) {
            errorText.setText(message);
            errorText.setVisible(true);
            if (successText != null) successText.setVisible(false);
        } 
        // Otherwise, fall back to status label if available
        else if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: " + (isSuccess ? "green" : "red") + ";");
        } 
        // Last resort: show an alert
        else {
            showAlert(
                isSuccess ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                isSuccess ? "Success" : "Error",
                isSuccess ? "Operation Successful" : "Operation Failed",
                message
            );
        }
    }
    
    /**
     * Clear the transaction form
     */
    private void clearForm() {
        if (datePicker != null) datePicker.setValue(LocalDate.now());
        if (typeComboBox != null) typeComboBox.setValue(null);
        if (amountField != null) amountField.clear();
        if (paymentMethodComboBox != null) paymentMethodComboBox.setValue(null);
        if (notesArea != null) notesArea.clear();
        if (transactionsTable != null) transactionsTable.getSelectionModel().clearSelection();
    }
    
    /**
     * Populate the form with transaction data
     * @param transaction The transaction to populate the form with
     */
    private void populateFormWithTransaction(Transaction transaction) {
        if (transaction == null) {
            LOGGER.warning("Attempt to populate form with null transaction");
            return;
        }
        
        if (datePicker != null) datePicker.setValue(transaction.getDate());
        if (typeComboBox != null) typeComboBox.setValue(transaction.getType());
        if (amountField != null) amountField.setText(String.valueOf(transaction.getAmount()));
        if (paymentMethodComboBox != null) paymentMethodComboBox.setValue(transaction.getPaymentMethod());
        if (notesArea != null) notesArea.setText(transaction.getNotes());
    }
    
    /**
     * Show an alert dialog
     * @param alertType The type of alert
     * @param title The title of the alert
     * @param headerText The header text of the alert
     * @param contentText The content text of the alert
     */
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
} 
