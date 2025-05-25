package com.finance.manager.entity;

import java.time.LocalDate;

/**
 * 账单实体类
 */
public class Bill {
    private String id;
    private String userId;
    private double amount;
    private String type;
    private String category;
    private String categoryId;
    private String description;
    private LocalDate date;
    private String paymentMethod;
    
    /**
     * 默认构造函数
     */
    public Bill() {
        this.date = LocalDate.now();
    }
    
    /**
     * 带基本参数的构造函数
     * @param userId 用户ID
     * @param amount 金额
     * @param type 类型（收入/支出）
     * @param category 分类
     */
    public Bill(String userId, double amount, String type, String category) {
        this();
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }
    
    /**
     * 完整构造函数
     * @param id ID
     * @param userId 用户ID
     * @param amount 金额
     * @param type 类型
     * @param category 分类
     * @param description 描述
     * @param date 日期
     * @param paymentMethod 支付方式
     */
    public Bill(String id, String userId, double amount, String type, String category, 
               String description, LocalDate date, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.description = description;
        this.date = date;
        this.paymentMethod = paymentMethod;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    /**
     * 获取带符号的金额（支出为负，收入为正）
     * @return 带符号的金额
     */
    public double getSignedAmount() {
        return "支出".equals(type) ? -amount : amount;
    }
    
    /**
     * 获取分类ID
     * @return 分类ID
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * 设置分类ID
     * @param categoryId 分类ID
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    // For backward compatibility with integer IDs in ForecastController
    public int getCategoryId_asInt() {
        try {
            return Integer.parseInt(categoryId);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s %.2f - %s", 
                date.toString(), 
                type,
                amount, 
                description != null ? description : category);
    }
} 
