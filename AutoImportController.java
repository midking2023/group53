package com.finance.manager.controller;

import com.finance.manager.model.Transaction;
import com.finance.manager.model.User;
import com.finance.manager.utils.CategoryClassifier;
import com.finance.manager.utils.DataService;
import com.finance.manager.utils.FileImportUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Auto Import Controller
 * Handles the functionality of automatically importing transaction data from files
 */
public class AutoImportController {

    @FXML
    private VBox dropArea;

    @FXML
    private Button selectFileButton;

    @FXML
    private Text statusText;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private HBox progressArea;

    @FXML
    private TableView<Transaction> importedDataTable;

    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> categoryColumn;

    @FXML
    private TableColumn<Transaction, String> notesColumn;

    @FXML
    private Button importButton;

    @FXML
    private Button clearButton;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Button nextButton;

    private User currentUser;
    private DataService dataService;
    private List<Transaction> importedTransactions;
    private File selectedFile;

    /**
     * Initialization method
     * Automatically called by JavaFX after loading the FXML
     */
    @FXML
    public void initialize() {
        // Get data service instance
        dataService = DataService.getInstance();
        
        // Set up table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        
        // Initialize UI state
        progressArea.setVisible(false);
        importButton.setDisable(true);
        clearButton.setDisable(true);
        statusText.setText("Please select a file or drag and drop a file here");
        
        // Set up drag and drop event handling
        setupDragAndDrop();
    }

    /**
     * Initialize user data
     * @param user The currently logged in user
     */
    public void initData(User user) {
        this.currentUser = user;
    }

    /**
     * Set up drag and drop event handling
     */
    private void setupDragAndDrop() {
        // Drag over event
        dropArea.setOnDragOver(event -> {
            if (event.getGestureSource() != dropArea &&
                    event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        
        // Drag entered effect
        dropArea.setOnDragEntered(event -> {
            if (event.getGestureSource() != dropArea &&
                    event.getDragboard().hasFiles()) {
                dropArea.getStyleClass().add("drag-over");
            }
            event.consume();
        });
        
        // Drag exited effect
        dropArea.setOnDragExited(event -> {
            dropArea.getStyleClass().remove("drag-over");
            event.consume();
        });
        
        // Drag dropped event
        dropArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                selectedFile = db.getFiles().get(0);
                statusText.setText("Selected file: " + selectedFile.getName());
                success = true;
                
                // Automatically start parsing the file
                parseFile(selectedFile);
            }
            
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Handle select file button click event
     * @param event The event object
     */
    @FXML
    public void handleSelectFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Transaction Record File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(selectFileButton.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            statusText.setText("Selected file: " + file.getName());
            
            // Parse the file
            parseFile(file);
        }
    }

    /**
     * Parse the import file
     * @param file The file to parse
     */
    private void parseFile(File file) {
        // Show progress area
        progressArea.setVisible(true);
        progressBar.setProgress(-1);  // Indeterminate progress
        statusText.setText("Parsing file...");
        
        // Create background task
        Task<List<Transaction>> task = new Task<List<Transaction>>() {
            @Override
            protected List<Transaction> call() throws Exception {
                // Parse the file
                return FileImportUtil.importFromCSV(file);
            }
        };
        
        // Set up handling after task completion
        task.setOnSucceeded(e -> {
            importedTransactions = task.getValue();
            
            // Auto-categorize imported transactions
            importedTransactions = importedTransactions.stream()
                    .map(transaction -> {
                        if (transaction.getCategory() == null || transaction.getCategory().isEmpty()) {
                            String category = CategoryClassifier.autoClassify(transaction).getCategory();
                            transaction.setCategory(category);
                        }
                        return transaction;
                    })
                    .collect(Collectors.toList());
            
            // Update UI
            Platform.runLater(() -> {
                importedDataTable.setItems(FXCollections.observableArrayList(importedTransactions));
                progressBar.setProgress(1);
                statusText.setText("File parsing complete, " + importedTransactions.size() + " transaction records found");
                importButton.setDisable(false);
                clearButton.setDisable(false);
            });
        });
        
        // Set up handling after task failure
        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            Platform.runLater(() -> {
                progressBar.setProgress(0);
                statusText.setText("File parsing failed: " + exception.getMessage());
                progressArea.setVisible(false);
            });
        });
        
        // Start the task
        new Thread(task).start();
    }

    /**
     * Handle import button click event
     * @param event The event object
     */
    @FXML
    public void handleImport(ActionEvent event) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Import");
        alert.setHeaderText("Import Confirmation");
        alert.setContentText("Are you sure you want to import " + importedTransactions.size() + " transaction records?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            // Create background task for importing
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Import transactions
                    for (Transaction transaction : importedTransactions) {
                        transaction.setUserId(currentUser.getUsername());
                        dataService.addTransaction(currentUser.getUsername(), transaction);
                    }
                    return null;
                }
            };
            
            // Set up handling after task completion
            task.setOnSucceeded(e -> {
                Platform.runLater(() -> {
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Import Complete");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Successfully imported " + importedTransactions.size() + " transaction records.");
                    successAlert.showAndWait();
                    
                    // Reset UI
                    importedDataTable.getItems().clear();
                    progressBar.setProgress(0);
                    statusText.setText("Please select a file or drag and drop a file here");
                    importButton.setDisable(true);
                    clearButton.setDisable(true);
                    progressArea.setVisible(false);
                });
            });
            
            // Start the task
            new Thread(task).start();
        }
    }

    /**
     * Handle clear button click event
     * @param event The event object
     */
    @FXML
    public void handleClear(ActionEvent event) {
        // Clear imported data and reset UI
        importedTransactions.clear();
        importedDataTable.getItems().clear();
        progressBar.setProgress(0);
        statusText.setText("Please select a file or drag and drop a file here");
        importButton.setDisable(true);
        clearButton.setDisable(true);
        progressArea.setVisible(false);
    }

    /**
     * Handle previous page button click event
     * @param event The event object
     */
    @FXML
    public void handlePrevPage(ActionEvent event) {
        try {
            // Load main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            // Pass user data to controller
            MainController controller = loader.getController();
            controller.initData(currentUser);
            
            // Switch scene
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle next page button click event
     * @param event The event object
     */
    @FXML
    public void handleNextPage(ActionEvent event) {
        try {
            // Load manual entry view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/manual_entry.fxml"));
            Parent root = loader.load();
            
            // Pass user data to controller
            ManualEntryController controller = loader.getController();
            controller.initData(currentUser);
            
            // Switch scene
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
