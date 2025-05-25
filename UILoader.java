package com.finance.manager.utils;

import com.finance.manager.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UI Loading Utility Class
 * Unified management of interface loading logic, handling exception cases
 */
public class UILoader {
    private static final Logger LOGGER = Logger.getLogger(UILoader.class.getName());
    
    /**
     * Load FXML interface
     * @param fxmlPath FXML file path
     * @return Loaded parent node
     * @throws IOException Exception thrown when loading fails
     */
    public static Parent loadFXML(String fxmlPath) throws IOException {
        try {
            LOGGER.info("Loading FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(UILoader.class.getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML: " + fxmlPath, e);
            throw e;
        }
    }
    
    /**
     * Load FXML interface and get its controller
     * @param <T> Controller type
     * @param fxmlPath FXML file path
     * @return FXMLLoader object, can get controller via getController()
     * @throws IOException Exception thrown when loading fails
     */
    public static <T> FXMLLoader loadFXMLWithController(String fxmlPath) throws IOException {
        try {
            LOGGER.info("Loading FXML and controller: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(UILoader.class.getResource(fxmlPath));
            loader.load();
            return loader;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load FXML and controller: " + fxmlPath, e);
            throw e;
        }
    }
    
    /**
     * Switch scene
     * @param stage Stage to switch scene
     * @param fxmlPath FXML file path
     * @throws IOException Exception thrown when loading fails
     */
    public static void switchScene(Stage stage, String fxmlPath) throws IOException {
        Parent root = loadFXML(fxmlPath);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Switch scene and set user data
     * @param <T> Controller type
     * @param stage Stage to switch scene
     * @param fxmlPath FXML file path
     * @param user Current user
     * @throws IOException Exception thrown when loading fails
     */
    public static <T> void switchSceneWithUser(Stage stage, String fxmlPath, User user) throws IOException {
        try {
            FXMLLoader loader = loadFXMLWithController(fxmlPath);
            
            // Get controller and set user data
            Object controller = loader.getController();
            if (controller != null && controller.getClass().getMethod("initData", User.class) != null) {
                controller.getClass().getMethod("initData", User.class).invoke(controller, user);
            }
            
            Scene scene = new Scene(loader.getRoot());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to switch scene: " + fxmlPath, e);
            
            // Show error alert
            showErrorAlert("Interface Loading Failed", "Cannot load interface: " + fxmlPath, e.getMessage());
            throw new IOException("Failed to switch scene: " + e.getMessage(), e);
        }
    }
    
    /**
     * Show error alert dialog
     * @param title Title
     * @param headerText Header text
     * @param contentText Content text
     */
    public static void showErrorAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    
    /**
     * Show information alert dialog
     * @param title Title
     * @param headerText Header text
     * @param contentText Content text
     */
    public static void showInfoAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
} 
