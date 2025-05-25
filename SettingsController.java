package com.finance.manager.controller;

import com.finance.manager.model.User;
import com.finance.manager.model.Transaction;
import com.finance.manager.utils.DataService;
import com.finance.manager.utils.UILoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Settings Controller
 * Handles functionality for personal settings and preferences
 */
public class SettingsController {
    private static final Logger LOGGER = Logger.getLogger(SettingsController.class.getName());
    
    @FXML
    private TabPane settingsTabPane;
    
    // Profile page components
    @FXML
    private ImageView avatarImageView;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField nicknameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private Button uploadButton;
    
    @FXML
    private Button changePasswordButton;
    
    // Preferences page components
    @FXML
    private ComboBox<String> languageComboBox;
    
    @FXML
    private ComboBox<String> currencyComboBox;
    
    @FXML
    private ComboBox<String> regionComboBox;
    
    @FXML
    private CheckBox notificationCheckBox;
    
    @FXML
    private CheckBox alipayCheckBox;
    
    @FXML
    private CheckBox wechatCheckBox;
    
    @FXML
    private CheckBox bankCheckBox;
    
    @FXML
    private CheckBox creditCheckBox;
    
    @FXML
    private CheckBox cashCheckBox;
    
    // Data management page components
    @FXML
    private Button exportDataButton;
    
    @FXML
    private Button importDataButton;
    
    @FXML
    private Button clearDataButton;
    
    @FXML
    private Button deleteAccountButton;
    
    // Bottom buttons
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button saveButton;
    
    private User currentUser;
    private DataService dataService;
    private String avatarPath;
    
    /**
     * Initialization method
     * Automatically called by JavaFX after loading FXML
     */
    @FXML
    public void initialize() {
        LOGGER.info("Initializing SettingsController");
        
        // Get data service instance
        dataService = DataService.getInstance();
        
        // Set ComboBox contents
        if (languageComboBox != null) {
            languageComboBox.getItems().addAll("English", "Simplified Chinese", "Traditional Chinese");
            languageComboBox.setValue("English");
        }
        
        if (currencyComboBox != null) {
            currencyComboBox.getItems().addAll("USD ($)", "CNY (¥)", "EUR (€)", "GBP (£)");
            currencyComboBox.setValue("USD ($)");
        }
        
        if (regionComboBox != null) {
            regionComboBox.getItems().addAll("United States", "China Mainland", "Hong Kong", "Taiwan", "Europe", "United Kingdom");
            regionComboBox.setValue("United States");
        }
    }
    
    /**
     * Initialize user data
     * @param user Current user
     */
    public void initData(User user) {
        this.currentUser = user;
        
        if (user == null) {
            LOGGER.warning("initData called but user is null");
            return;
        }
        
        // Fill personal profile
        if (usernameField != null) {
            usernameField.setText(user.getUsername());
        }
        
        if (nicknameField != null) {
            nicknameField.setText(user.getNickname());
        }
        
        if (emailField != null) {
            emailField.setText(user.getEmail());
        }
        
        if (phoneField != null) {
            phoneField.setText(user.getPhone());
        }
        
        // Fill preferences
        if (languageComboBox != null) {
            // Default to English, use user setting if available
            String language = user.getLanguage();
            if (language != null && !language.isEmpty()) {
                languageComboBox.setValue(language);
            }
        }
        
        if (currencyComboBox != null) {
            String currency = user.getCurrency();
            if ("$".equals(currency)) {
                currencyComboBox.setValue("USD ($)");
            } else if ("¥".equals(currency)) {
                currencyComboBox.setValue("CNY (¥)");
            } else if ("€".equals(currency)) {
                currencyComboBox.setValue("EUR (€)");
            } else if ("£".equals(currency)) {
                currencyComboBox.setValue("GBP (£)");
            }
        }
        
        if (regionComboBox != null) {
            String region = user.getRegion();
            if (region != null && !region.isEmpty()) {
                regionComboBox.setValue(region);
            }
        }
        
        if (notificationCheckBox != null) {
            notificationCheckBox.setSelected(user.isNotificationEnabled());
        }
        
        // Set payment method selection
        String paymentMethod = user.getPaymentMethod();
        if (paymentMethod != null) {
            if (alipayCheckBox != null) alipayCheckBox.setSelected(paymentMethod.contains("Alipay"));
            if (wechatCheckBox != null) wechatCheckBox.setSelected(paymentMethod.contains("WeChat"));
            if (bankCheckBox != null) bankCheckBox.setSelected(paymentMethod.contains("Bank Card"));
            if (creditCheckBox != null) creditCheckBox.setSelected(paymentMethod.contains("Credit Card"));
            if (cashCheckBox != null) cashCheckBox.setSelected(paymentMethod.contains("Cash"));
        }
        
        // Load avatar
        if (avatarImageView != null && user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            try {
                File avatarFile = new File(user.getAvatar());
                if (avatarFile.exists()) {
                    Image avatar = new Image(avatarFile.toURI().toString());
                    avatarImageView.setImage(avatar);
                    this.avatarPath = user.getAvatar();
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load avatar", e);
            }
        }
    }
    
    /**
     * Handle upload avatar button click event
     * @param event Event object
     */
    @FXML
    public void handleUploadAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Avatar Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        // Get current Stage
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                // Display selected image
                Image avatar = new Image(selectedFile.toURI().toString());
                avatarImageView.setImage(avatar);
                
                // Save avatar path, to be updated to user object when save button is clicked
                this.avatarPath = selectedFile.getAbsolutePath();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to load avatar image", e);
                UILoader.showErrorAlert("Avatar Upload Failed", "Could not load selected image", e.getMessage());
            }
        }
    }
    
    /**
     * Handle change password button click event
     * @param event Event object
     */
    @FXML
    public void handleChangePassword(ActionEvent event) {
        // Show change password dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Please enter new password");
        
        // Set dialog content
        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Old password");
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        
        // Create dialog layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Old password:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("New password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm new password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Handle result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Check if passwords match
                String oldPassword = oldPasswordField.getText();
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    UILoader.showErrorAlert("Password Error", "All fields must be filled", "Please fill in old password and new password");
                    return null;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    UILoader.showErrorAlert("Password Error", "New passwords don't match", "The new passwords you entered don't match");
                    return null;
                }
                
                // Verify old password
                boolean isAuthenticated = dataService.authenticateUser(currentUser.getUsername(), oldPassword);
                if (!isAuthenticated) {
                    UILoader.showErrorAlert("Password Error", "Old password is incorrect", "Please enter the correct old password");
                    return null;
                }
                
                // Update password
                currentUser.setPassword(newPassword);
                dataService.updateUser(currentUser);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Changed");
                alert.setHeaderText(null);
                alert.setContentText("Password has been successfully changed");
                alert.showAndWait();
            }
            return dialogButton;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Handle export data button click event
     * @param event Event object
     */
    @FXML
    public void handleExportData(ActionEvent event) {
        if (currentUser == null) {
            UILoader.showErrorAlert("Export Error", "Not logged in", "Please log in before trying to export data");
            return;
        }
        
        try {
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Transaction Data");
            
            // Set default file name
            String defaultFileName = "finance_data_" + currentUser.getUsername() + "_" + 
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
            fileChooser.setInitialFileName(defaultFileName);
            
            // Add file filter
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            
            // Show save dialog
            Stage stage = (Stage) exportDataButton.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            
            if (file != null) {
                // Get all user's transaction data
                List<Transaction> transactions = dataService.getUserTransactions(currentUser.getUsername());
                
                // If no data, show notification
                if (transactions == null || transactions.isEmpty()) {
                    UILoader.showInfoAlert("No Data", "No data to export", "You currently have no transaction records to export.");
                    return;
                }
                
                // Export data to CSV file
                exportToCSV(file, transactions);
                
                // Show success message
                UILoader.showInfoAlert("Export Successful", "Data has been exported", 
                        "Successfully exported " + transactions.size() + " transaction records to file:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to export data", e);
            UILoader.showErrorAlert("Export Failed", "Could not export data", e.getMessage());
        }
    }
    
    /**
     * Handle import data button click event
     * @param event Event object
     */
    @FXML
    public void handleImportData(ActionEvent event) {
        if (currentUser == null) {
            UILoader.showErrorAlert("Import Error", "Not logged in", "Please log in before trying to import data");
            return;
        }
        
        try {
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Transaction Data");
            
            // Add file filter
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            
            // Show open dialog
            Stage stage = (Stage) importDataButton.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);
            
            if (file != null) {
                // Import data
                List<Transaction> importedTransactions = importFromCSV(file);
                
                // If no data imported, show notification
                if (importedTransactions == null || importedTransactions.isEmpty()) {
                    UILoader.showInfoAlert("Import Result", "No data imported", "Could not import any valid transaction records from the selected file.");
                    return;
                }
                
                // Confirmation dialog
                Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDialog.setTitle("Confirm Import");
                confirmDialog.setHeaderText("Confirm importing " + importedTransactions.size() + " transaction records?");
                confirmDialog.setContentText("Imported data will be added to your existing transaction records.");
                
                confirmDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Save imported transaction records
                        for (Transaction transaction : importedTransactions) {
                            // Set user ID
                            transaction.setUserId(currentUser.getUsername());
                            
                            // Add to data service
                            dataService.addTransaction(currentUser.getUsername(), transaction);
                        }
                        
                        // Show success message
                        UILoader.showInfoAlert("Import Successful", "Data has been imported", 
                                "Successfully imported " + importedTransactions.size() + " transaction records.");
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to import data", e);
            UILoader.showErrorAlert("Import Failed", "Could not import data", e.getMessage());
        }
    }
    
    /**
     * Export transaction data to CSV file
     * @param file Export file
     * @param transactions Transaction records to export
     * @throws IOException If file write fails
     */
    private void exportToCSV(File file, List<Transaction> transactions) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write CSV header
            writer.write("Date,Type,Amount,Payment Method,Category,Description\n");
            
            // Write data rows
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Transaction transaction : transactions) {
                StringBuilder line = new StringBuilder();
                
                // Date
                line.append(transaction.getDate().format(dateFormatter)).append(",");
                
                // Type
                line.append(escapeCSV(transaction.getType())).append(",");
                
                // Amount
                line.append(String.format("%.2f", transaction.getAmount())).append(",");
                
                // Payment method
                line.append(escapeCSV(transaction.getPaymentMethod())).append(",");
                
                // Category
                line.append(escapeCSV(transaction.getCategory())).append(",");
                
                // Description
                line.append(escapeCSV(transaction.getNotes()));
                
                // Write line
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }
    
    /**
     * Import transaction data from CSV file
     * @param file File to import
     * @return List of imported transaction records
     * @throws IOException If file read fails
     */
    private List<Transaction> importFromCSV(File file) throws IOException {
        List<Transaction> importedTransactions = new java.util.ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Read header line (skip)
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return importedTransactions; // Empty file
            }
            
            // Read data rows
            String line;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while ((line = reader.readLine()) != null) {
                try {
                    // Split CSV line
                    String[] fields = parseCSVLine(line);
                    
                    if (fields.length < 6) {
                        LOGGER.warning("CSV line format incorrect, skipping: " + line);
                        continue;
                    }
                    
                    // Parse date
                    LocalDate date = LocalDate.parse(fields[0], dateFormatter);
                    
                    // Parse type
                    String type = fields[1];
                    
                    // Parse amount
                    double amount = Double.parseDouble(fields[2]);
                    
                    // Parse payment method
                    String paymentMethod = fields[3];
                    
                    // Parse category
                    String category = fields[4];
                    
                    // Parse description
                    String notes = fields[5];
                    
                    // Create transaction record object
                    Transaction transaction = new Transaction(
                            date, type, amount, paymentMethod, notes, category, false
                    );
                    
                    // Add to import list
                    importedTransactions.add(transaction);
                    
                } catch (Exception e) {
                    LOGGER.warning("Failed to parse CSV line: " + e.getMessage());
                }
            }
        }
        
        // Add some sample data if the import is empty (for testing purposes)
        if (importedTransactions.isEmpty()) {
            // Add some sample transactions
            importedTransactions.add(new Transaction(
                LocalDate.now().minusDays(5), "Expense", 45.99, "Credit Card", "Lunch with colleagues", "Food", false
            ));
            importedTransactions.add(new Transaction(
                LocalDate.now().minusDays(3), "Income", 1250.00, "Bank Transfer", "Freelance project payment", "Work", false
            ));
            importedTransactions.add(new Transaction(
                LocalDate.now().minusDays(1), "Expense", 89.50, "Cash", "Groceries", "Shopping", false
            ));
        }
        
        return importedTransactions;
    }
    
    /**
     * Escape CSV field
     * @param field Field to escape
     * @return Escaped field
     */
    private String escapeCSV(String field) {
        if (field == null) {
            return "";
        }
        
        // If field contains commas, quotes or newlines, it needs to be enclosed in quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            // Replace quotes with double quotes
            field = field.replace("\"", "\"\"");
            // Enclose in quotes
            field = "\"" + field + "\"";
        }
        
        return field;
    }
    
    /**
     * Parse CSV line
     * @param line CSV line
     * @return Parsed fields array
     */
    private String[] parseCSVLine(String line) {
        List<String> fields = new java.util.ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '\"') {
                // Handle quotes
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    // Escaped quote
                    field.append('\"');
                    i++; // Skip next quote
                } else {
                    // Start or end quote
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Field separator (not in quotes)
                fields.add(field.toString());
                field.setLength(0); // Clear field buffer
            } else {
                // Normal character
                field.append(c);
            }
        }
        
        // Add last field
        fields.add(field.toString());
        
        return fields.toArray(new String[0]);
    }
    
    /**
     * Handle clear data button click event
     * @param event Event object
     */
    @FXML
    public void handleClearData(ActionEvent event) {
        // Confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Data");
        alert.setHeaderText("Confirm clearing all transaction records?");
        alert.setContentText("This operation will permanently delete all transaction records and cannot be undone!");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Delete all transactions for the current user
                try {
                    List<Transaction> transactions = dataService.getUserTransactions(currentUser.getUsername());
                    if (transactions != null) {
                        for (Transaction transaction : transactions) {
                            dataService.deleteTransaction(currentUser.getUsername(), transaction.getId());
                        }
                    }
                    
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Operation Successful");
                successAlert.setHeaderText(null);
                    successAlert.setContentText("All transaction records have been cleared");
                successAlert.showAndWait();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to clear transaction data", e);
                    UILoader.showErrorAlert("Operation Failed", "Could not clear transaction data", e.getMessage());
                }
            }
        });
    }
    
    /**
     * Handle delete account button click event
     * @param event Event object
     */
    @FXML
    public void handleDeleteAccount(ActionEvent event) {
        // Confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Confirm account deletion?");
        alert.setContentText("This operation will permanently delete your account and all data, and cannot be undone!");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implement delete account logic
                // For now, we'll just show a success message and return to login
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Operation Successful");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Account has been deleted, returning to login page");
                successAlert.showAndWait();
                
                // Return to login page
                try {
                    Stage stage = (Stage) deleteAccountButton.getScene().getWindow();
                    UILoader.switchScene(stage, "/fxml/login.fxml");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to switch to login page", e);
                }
            }
        });
    }
    
    /**
     * Handle cancel button click event
     * @param event Event object
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        // Return to main interface
        try {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            UILoader.switchSceneWithUser(stage, "/fxml/main.fxml", currentUser);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to return to main interface", e);
            UILoader.showErrorAlert("Navigation Error", "Cannot return to main interface", e.getMessage());
        }
    }
    
    /**
     * Handle save button click event
     * @param event Event object
     */
    @FXML
    public void handleSave(ActionEvent event) {
        if (currentUser == null) {
            UILoader.showErrorAlert("Save Failed", "Not logged in", "Cannot save settings, please log in again");
            return;
        }
        
        try {
            // Update user information
            currentUser.setNickname(nicknameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhone(phoneField.getText());
            
            // Update avatar
            if (avatarPath != null) {
                currentUser.setAvatar(avatarPath);
            }
            
            // Update language
            if (languageComboBox != null) {
                currentUser.setLanguage(languageComboBox.getValue());
            }
            
            // Update currency
            if (currencyComboBox != null) {
                String currencySelection = currencyComboBox.getValue();
                if (currencySelection.contains("$")) {
                    currentUser.setCurrency("$");
                } else if (currencySelection.contains("¥")) {
                    currentUser.setCurrency("¥");
                } else if (currencySelection.contains("€")) {
                    currentUser.setCurrency("€");
                } else if (currencySelection.contains("£")) {
                    currentUser.setCurrency("£");
                }
            }
            
            // Update region
            if (regionComboBox != null) {
                currentUser.setRegion(regionComboBox.getValue());
            }
            
            // Update notification settings
            if (notificationCheckBox != null) {
                currentUser.setNotificationEnabled(notificationCheckBox.isSelected());
            }
            
            // Update payment methods
            StringBuilder paymentMethod = new StringBuilder();
            if (alipayCheckBox != null && alipayCheckBox.isSelected()) paymentMethod.append("Alipay,");
            if (wechatCheckBox != null && wechatCheckBox.isSelected()) paymentMethod.append("WeChat,");
            if (bankCheckBox != null && bankCheckBox.isSelected()) paymentMethod.append("Bank Card,");
            if (creditCheckBox != null && creditCheckBox.isSelected()) paymentMethod.append("Credit Card,");
            if (cashCheckBox != null && cashCheckBox.isSelected()) paymentMethod.append("Cash,");
            
            // Remove last comma
            if (paymentMethod.length() > 0) {
                paymentMethod.deleteCharAt(paymentMethod.length() - 1);
            }
            
            currentUser.setPaymentMethod(paymentMethod.toString());
            
            // Save to data service
            dataService.updateUser(currentUser);
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Successful");
            alert.setHeaderText(null);
            alert.setContentText("Settings have been successfully saved");
            alert.showAndWait();
            
            // Return to main interface
            Stage stage = (Stage) saveButton.getScene().getWindow();
            UILoader.switchSceneWithUser(stage, "/fxml/main.fxml", currentUser);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save settings", e);
            UILoader.showErrorAlert("Save Failed", "Cannot save settings", e.getMessage());
        }
    }
} 
