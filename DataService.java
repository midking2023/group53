package com.finance.manager.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finance.manager.model.Transaction;
import com.finance.manager.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data Service Class
 * Responsible for data storage and retrieval
 */
public class DataService {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.json";
    private static final String TRANSACTIONS_DIR = DATA_DIR + "/transactions";
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private static DataService instance;
    
    private Map<String, User> users = new HashMap<>();
    private Map<String, List<Transaction>> userTransactions = new HashMap<>();
    
    /**
     * Private constructor, singleton pattern
     */
    private DataService() {
        // Configure ObjectMapper
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Initialize data directory
        initDataDirectory();
        
        // Load user data
        loadUsers();
        
        // If no users exist, create default user
        if (users.isEmpty()) {
            // Create default user
            User defaultUser = new User("admin", "admin", "Administrator", "admin@example.com");
            defaultUser.setCurrency("$");
            addUser(defaultUser);
            System.out.println("Default user created: admin/admin");
        }
    }
    
    /**
     * Get DataService instance
     * @return DataService instance
     */
    public static synchronized DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }
    
    /**
     * Initialize data directory
     */
    private void initDataDirectory() {
        // Create main data directory
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Create transactions data directory
        File transactionsDir = new File(TRANSACTIONS_DIR);
        if (!transactionsDir.exists()) {
            transactionsDir.mkdirs();
        }
    }
    
    /**
     * Load user data
     */
    private void loadUsers() {
        File usersFile = new File(USERS_FILE);
        if (usersFile.exists()) {
            try {
                List<User> userList = objectMapper.readValue(usersFile, new TypeReference<List<User>>() {});
                for (User user : userList) {
                    users.put(user.getUsername(), user);
                    // Load user's transaction data
                    loadUserTransactions(user.getUsername());
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Create empty user map if file read fails
                users = new HashMap<>();
            }
        }
    }
    
    /**
     * Load specified user's transaction data
     * @param username Username
     */
    private void loadUserTransactions(String username) {
        File userTransactionsFile = new File(TRANSACTIONS_DIR + "/" + username + ".json");
        if (userTransactionsFile.exists()) {
            try {
                List<Transaction> transactions = objectMapper.readValue(userTransactionsFile, 
                                               new TypeReference<List<Transaction>>() {});
                userTransactions.put(username, transactions);
            } catch (IOException e) {
                e.printStackTrace();
                // Create empty transaction list if file read fails
                userTransactions.put(username, new ArrayList<>());
            }
        } else {
            userTransactions.put(username, new ArrayList<>());
        }
    }
    
    /**
     * Save user data
     */
    private void saveUsers() {
        try {
            List<User> userList = new ArrayList<>(users.values());
            objectMapper.writeValue(new File(USERS_FILE), userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Save specified user's transaction data
     * @param username Username
     */
    private void saveUserTransactions(String username) {
        List<Transaction> transactions = userTransactions.getOrDefault(username, new ArrayList<>());
        try {
            objectMapper.writeValue(new File(TRANSACTIONS_DIR + "/" + username + ".json"), transactions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Add new user
     * @param user User object
     * @return Whether addition was successful
     */
    public boolean addUser(User user) {
        // Check if username already exists
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        
        // Add user
        users.put(user.getUsername(), user);
        userTransactions.put(user.getUsername(), new ArrayList<>());
        
        // Save user data
        saveUsers();
        
        return true;
    }
    
    /**
     * Authenticate user login
     * @param username Username
     * @param password Password
     * @return Whether login was successful
     */
    public boolean authenticateUser(String username, String password) {
        User user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }
    
    /**
     * Get user information
     * @param username Username
     * @return User object
     */
    public User getUser(String username) {
        return users.get(username);
    }
    
    /**
     * Update user information
     * @param user User object
     */
    public void updateUser(User user) {
        users.put(user.getUsername(), user);
        saveUsers();
    }
    
    /**
     * Add transaction record
     * @param username Username
     * @param transaction Transaction record
     */
    public void addTransaction(String username, Transaction transaction) {
        List<Transaction> transactions = userTransactions.getOrDefault(username, new ArrayList<>());
        transactions.add(transaction);
        userTransactions.put(username, transactions);
        saveUserTransactions(username);
    }
    
    /**
     * Get all transaction records for a user
     * @param username Username
     * @return List of transaction records
     */
    public List<Transaction> getUserTransactions(String username) {
        return userTransactions.getOrDefault(username, new ArrayList<>());
    }
    
    /**
     * Get user's transactions within a date range
     * @param username Username
     * @param startDate Start date
     * @param endDate End date
     * @return List of transaction records
     */
    public List<Transaction> getUserTransactions(String username, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = userTransactions.getOrDefault(username, new ArrayList<>());
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Get user's transactions by type
     * @param username Username
     * @param type Transaction type
     * @return List of transaction records
     */
    public List<Transaction> getUserTransactionsByType(String username, String type) {
        List<Transaction> transactions = userTransactions.getOrDefault(username, new ArrayList<>());
        return transactions.stream()
                .filter(t -> t.getType().equals(type))
                .collect(Collectors.toList());
    }
    
    /**
     * Update transaction record
     * @param username Username
     * @param transaction Transaction record
     */
    public void updateTransaction(String username, Transaction transaction) {
        List<Transaction> transactions = userTransactions.getOrDefault(username, new ArrayList<>());
        
        // Find and update the transaction record
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId().equals(transaction.getId())) {
                transactions.set(i, transaction);
                break;
            }
        }
        
        saveUserTransactions(username);
    }
    
    /**
     * Delete transaction record
     * @param username Username
     * @param transactionId Transaction record ID
     */
    public void deleteTransaction(String username, String transactionId) {
        List<Transaction> transactions = userTransactions.getOrDefault(username, new ArrayList<>());
        transactions.removeIf(t -> t.getId().equals(transactionId));
        saveUserTransactions(username);
    }
    
    /**
     * Import transaction records
     * @param username Username
     * @param transactions List of transaction records
     */
    public void importTransactions(String username, List<Transaction> transactions) {
        List<Transaction> userTransactionList = userTransactions.getOrDefault(username, new ArrayList<>());
        userTransactionList.addAll(transactions);
        userTransactions.put(username, userTransactionList);
        saveUserTransactions(username);
    }
} 