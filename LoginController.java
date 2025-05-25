package com.finance.manager.controller;

import com.finance.manager.model.User;
import com.finance.manager.utils.DataService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.finance.manager.utils.UILoader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Login Interface Controller
 * Handles user login, registration, and password recovery functions
 */
public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button forgotPasswordButton;

    @FXML
    private Button registerButton;

    @FXML
    private Text errorText;

    private DataService dataService;
    private User authenticatedUser;

    /**
     * Initialization method
     * Automatically called by JavaFX after loading FXML
     */
    @FXML
    public void initialize() {
        // Get data service instance
        dataService = DataService.getInstance();
        
        // Set enter key binding for login button
        passwordField.setOnAction(event -> handleLogin(event));
        
        // Hide error message on initialization
        errorText.setVisible(false);
        errorText.setManaged(false);
    }

    /**
     * Handle login button click event
     * @param event Event object
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        // Get username and password
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        System.out.println("Login attempt: Username=" + username + ", Password=" + password); // Debug info
        
        // Validate username and password are not empty
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        // Validate user login
        boolean isAuthenticated = dataService.authenticateUser(username, password);
        System.out.println("Authentication result: " + isAuthenticated); // Debug info
        
        if (isAuthenticated) {
            try {
                // Login successful, load main interface
                authenticatedUser = dataService.getUser(username);
                System.out.println("Login successful, user info: " + authenticatedUser.getNickname()); // Debug info
                loadMainView();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Failed to load main interface: " + e.getMessage());
            }
        } else {
            // Login failed
            showError("Incorrect username or password");
        }
    }

    /**
     * Handle forgot password button click event
     * @param event Event object
     */
    @FXML
    public void handleForgotPassword(ActionEvent event) {
        try {
            // Load forgot password interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forgot_password.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            Stage stage = (Stage) forgotPasswordButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load forgot password interface");
        }
    }

    /**
     * Handle register button click event
     * @param event Event object
     */
    @FXML
    public void handleRegister(ActionEvent event) {
        try {
            // Load registration interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load registration interface");
        }
    }

    /**
     * Show error message
     * @param errorMsg Error message
     */
    private void showError(String errorMsg) {
        errorText.setText(errorMsg);
        errorText.setVisible(true);
        errorText.setManaged(true);
        System.out.println("Showing error: " + errorMsg); // Debug info
    }

    /**
     * Show error message with title
     * @param title Error title
     * @param errorMsg Error message
     */
    private void showError(String title, String errorMsg) {
        UILoader.showErrorAlert("Login Error", title, errorMsg);
        errorText.setText(title + ": " + errorMsg);
        errorText.setVisible(true);
        errorText.setManaged(true);
        System.out.println("Showing error: " + title + ": " + errorMsg); // Debug info
    }

    /**
     * Load main interface
     */
    private void loadMainView() {
        LOGGER.info("Starting to load main interface");
        try {
            // Use UILoader to load main interface
            Stage stage = (Stage) loginButton.getScene().getWindow();
            UILoader.switchSceneWithUser(stage, "/fxml/main.fxml", authenticatedUser);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load main interface: " + e.getMessage(), e);
            showError("Failed to load main interface", e.getMessage());
        }
    }
} 