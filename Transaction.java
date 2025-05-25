package com.finance.manager.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Transaction record model class
 */
public class Transaction {
    private String id;
    private String userId;
    private double amount;
    private String category;
    private String description;
    private String type;
    private LocalDate date;
    private LocalTime time;
    private String paymentMethod;
    private String location;
    private boolean recurring;
    private String attachmentPath;
    private String notes;
    private boolean autoCategorized;

    /**
     * Default constructor
     */
    public Transaction() {
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.type = "Expense";
        this.recurring = false;
        this.notes = "";
        this.category = "";
        this.autoCategorized = false;
    }

    /**
     * Create transaction record (simplified version)
     * @param date Date
     * @param type Type
     * @param amount Amount
     * @param paymentMethod Payment method
     */
    public Transaction(LocalDate date, String type, double amount, String paymentMethod) {
        this();
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    /**
     * Create transaction record (with notes)
     * @param date Date
     * @param type Type
     * @param amount Amount
     * @param paymentMethod Payment method
     * @param notes Notes
     */
    public Transaction(LocalDate date, String type, double amount, String paymentMethod, String notes) {
        this(date, type, amount, paymentMethod);
        this.notes = notes;
    }

    /**
     * Create transaction record (complete parameters)
     * @param date Date
     * @param type Type
     * @param amount Amount
     * @param paymentMethod Payment method
     * @param notes Notes
     * @param category Category
     * @param autoCategorized Whether automatically categorized
     */
    public Transaction(LocalDate date, String type, double amount, String paymentMethod, String notes, String category, boolean autoCategorized) {
        this(date, type, amount, paymentMethod, notes);
        this.category = category;
        this.autoCategorized = autoCategorized;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isAutoCategorized() {
        return autoCategorized;
    }

    public void setAutoCategorized(boolean autoCategorized) {
        this.autoCategorized = autoCategorized;
    }

    /**
     * Get the signed amount (expense is negative, income is positive)
     * @return Signed amount
     */
    public double getSignedAmount() {
        return type.equals("Expense") ? -amount : amount;
    }

    /**
     * Get formatted display string for the transaction
     * @return Formatted display string
     */
    @Override
    public String toString() {
        return String.format("%s: %s %.2f - %s", 
                date.toString(), 
                type,
                amount, 
                description != null ? description : category);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 
