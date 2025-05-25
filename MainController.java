package com.finance.manager.controller;

import com.finance.manager.model.Transaction;
import com.finance.manager.model.User;
import com.finance.manager.utils.DataService;
import com.finance.manager.utils.UILoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.finance.manager.controller.HomeController;
import com.finance.manager.controller.TransactionController;
import com.finance.manager.controller.AutoImportController;
import com.finance.manager.controller.SimpleForecastController;
import com.finance.manager.controller.SettingsController;

/**
 * Main Controller
 * Responsible for managing the application's main interface and navigation
 */
public class MainController {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML
    private BorderPane mainPane;
    
    @FXML
    private VBox sidebarPane;
    
    @FXML
    private Button homeButton;
    
    @FXML
    private Button transactionButton;  // New transaction management button, replaces three separate buttons
    
    @FXML
    private Button autoImportButton;
    
    @FXML
    private Button forecastButton;
    
    @FXML
    private Button settingsButton;
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private Label usernameLabel;
    
    private User currentUser;
    
    private HomeController homeController;
    private TransactionController transactionController;  // New transaction management controller
    private AutoImportController autoImportController;
    private SimpleForecastController forecastController;
    private SettingsController settingsController;
    
    /**
     * Initialization method
     * Automatically called by JavaFX after loading FXML
     */
    @FXML
    public void initialize() {
        LOGGER.info("Initializing MainController");
        
        // Set navigation button click events
        if (homeButton != null) {
            homeButton.setOnAction(e -> showHomeView());
        }
        
        if (transactionButton != null) {
            transactionButton.setOnAction(e -> showTransactionView());
        }
        
        if (autoImportButton != null) {
            autoImportButton.setOnAction(e -> showAutoImportView());
        }
        
        if (forecastButton != null) {
            forecastButton.setOnAction(e -> showForecastView());
        }
        
        if (settingsButton != null) {
            settingsButton.setOnAction(e -> showSettingsView());
        }
        
        // Set current date
        if (dateLabel != null) {
            dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        
        // Show home page by default
        showHomeView();
    }
    
    /**
     * Initialize user data
     * @param user Currently logged in user
     */
    public void initData(User user) {
        LOGGER.info("Initializing user data: " + user.getUsername());
        this.currentUser = user;
        
        // Update user information in UI
        if (usernameLabel != null) {
            usernameLabel.setText(user.getUsername());
        }
        
        if (welcomeLabel != null) {
            welcomeLabel.setText("Hello, " + user.getUsername() + ", have a great day!");
        }
        
        // Update current view's user data
        updateCurrentViewController();
    }
    
    /**
     * Update current view's user data
     */
    private void updateCurrentViewController() {
        if (currentUser == null) {
            return;
        }
        
        // After user is set, update all child controllers' user data
        if (homeController != null) {
            homeController.initData(currentUser);
        }
        
        if (transactionController != null) {
            transactionController.initData(currentUser);
        }
        
        if (autoImportController != null) {
            autoImportController.initData(currentUser);
        }
        
        if (forecastController != null) {
            forecastController.initData(currentUser);
        }
        
        if (settingsController != null) {
            settingsController.initData(currentUser);
        }
    }
    
    /**
     * Show home view
     */
    private void showHomeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent view = loader.load();
            homeController = loader.getController();
            
            if (currentUser != null) {
                homeController.initData(currentUser);
            }
            
            mainPane.setCenter(view);
            setActiveButton(homeButton);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load home view", e);
            UILoader.showErrorAlert("View Loading Failed", "Cannot load home view", e.getMessage());
        }
    }
    
    /**
     * Show transaction management view
     * Integrates expense categories, manual categorization, and manual entry functions
     */
    private void showTransactionView() {
        try {
            // Use new transaction management view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transaction.fxml"));
            Parent view = loader.load();
            transactionController = loader.getController();
            
            if (currentUser != null) {
                transactionController.initData(currentUser);
            }
            
            mainPane.setCenter(view);
            setActiveButton(transactionButton);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load transaction management view", e);
            UILoader.showErrorAlert("View Loading Failed", "Cannot load transaction management view", e.getMessage());
            
            // If loading fails, show a simple fallback view
            try {
                VBox fallbackView = new VBox();
                fallbackView.getChildren().add(new Label("Transaction management feature is under development..."));
                mainPane.setCenter(fallbackView);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to create fallback view", ex);
            }
        }
    }
    
    /**
     * Show auto import view
     */
    private void showAutoImportView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auto_import.fxml"));
            Parent view = loader.load();
            autoImportController = loader.getController();
            
            if (currentUser != null) {
                autoImportController.initData(currentUser);
            }
            
            mainPane.setCenter(view);
            setActiveButton(autoImportButton);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load auto import view", e);
            UILoader.showErrorAlert("View Loading Failed", "Cannot load auto import view", e.getMessage());
            
            // If loading fails, show a simple fallback view
            try {
                VBox fallbackView = new VBox();
                fallbackView.getChildren().add(new Label("Auto import feature is under development..."));
                mainPane.setCenter(fallbackView);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to create fallback view", ex);
            }
        }
    }
    
    /**
     * Show forecast analysis view
     */
    private void showForecastView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/simple_forecast.fxml"));
            Parent view = loader.load();
            forecastController = loader.getController();
            
            if (currentUser != null) {
                forecastController.initData(currentUser);
            }
            
            mainPane.setCenter(view);
            setActiveButton(forecastButton);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load forecast view", e);
            UILoader.showErrorAlert("View Loading Failed", "Cannot load forecast view", e.getMessage());
            
            // If loading fails, show a simple fallback view
            try {
                VBox fallbackView = new VBox();
                fallbackView.getChildren().add(new Label("Forecast feature is under development..."));
                mainPane.setCenter(fallbackView);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to create fallback view", ex);
            }
        }
    }
    
    /**
     * Show settings view
     */
    private void showSettingsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));
            Parent view = loader.load();
            settingsController = loader.getController();
            
            if (currentUser != null) {
                settingsController.initData(currentUser);
            }
            
            mainPane.setCenter(view);
            setActiveButton(settingsButton);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load settings view", e);
            UILoader.showErrorAlert("View Loading Failed", "Cannot load settings view", e.getMessage());
        }
    }
    
    /**
     * Set active button
     * @param activeButton Button to set as active
     */
    private void setActiveButton(Button activeButton) {
        // Reset all buttons to inactive style
        if (homeButton != null) homeButton.getStyleClass().remove("active");
        if (transactionButton != null) transactionButton.getStyleClass().remove("active");
        if (autoImportButton != null) autoImportButton.getStyleClass().remove("active");
        if (forecastButton != null) forecastButton.getStyleClass().remove("active");
        if (settingsButton != null) settingsButton.getStyleClass().remove("active");
        
        // Set the selected button to active style
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }
} 