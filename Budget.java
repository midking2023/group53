package com.finance.manager.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 预算模型类
 * 包含预算的基本信息和配置
 */
public class Budget {
    private String id;
    private String userId;
    private YearMonth yearMonth; // 预算年月
    private double totalBudget; // 总预算
    private Map<String, Double> categoryBudgets; // 分类预算
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;

    /**
     * 无参构造函数
     */
    public Budget() {
        this.id = UUID.randomUUID().toString();
        this.createdDate = LocalDate.now();
        this.lastModifiedDate = this.createdDate;
        this.yearMonth = YearMonth.now();
        this.totalBudget = 0.0;
        this.categoryBudgets = new HashMap<>();
    }

    /**
     * 构造函数
     * @param userId 用户ID
     * @param yearMonth 预算年月
     * @param totalBudget 总预算
     */
    public Budget(String userId, YearMonth yearMonth, double totalBudget) {
        this();
        this.userId = userId;
        this.yearMonth = yearMonth;
        this.totalBudget = totalBudget;
    }

    /**
     * 设置分类预算
     * @param category 分类
     * @param amount 金额
     */
    public void setCategoryBudget(String category, double amount) {
        categoryBudgets.put(category, amount);
        this.lastModifiedDate = LocalDate.now();
    }

    /**
     * 获取分类预算
     * @param category 分类
     * @return 预算金额，如果未设置则返回0
     */
    public double getCategoryBudget(String category) {
        return categoryBudgets.getOrDefault(category, 0.0);
    }

    /**
     * 计算已分配预算总额
     * @return 已分配的预算总额
     */
    public double getAllocatedBudget() {
        return categoryBudgets.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * 计算未分配预算
     * @return 未分配的预算
     */
    public double getUnallocatedBudget() {
        return totalBudget - getAllocatedBudget();
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

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Map<String, Double> getCategoryBudgets() {
        return categoryBudgets;
    }

    public void setCategoryBudgets(Map<String, Double> categoryBudgets) {
        this.categoryBudgets = categoryBudgets;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * 计算预算剩余金额
     * @return 剩余金额
     */
    public double getRemainingAmount() {
        return totalBudget - getAllocatedBudget();
    }

    /**
     * 计算预算使用进度百分比
     * @return 使用百分比
     */
    public double getProgressPercentage() {
        if (totalBudget <= 0) {
            return 0;
        }
        return (getAllocatedBudget() / totalBudget) * 100;
    }

    /**
     * 检查预算是否超支
     * @return 是否超支
     */
    public boolean isOverBudget() {
        return getAllocatedBudget() > totalBudget;
    }

    /**
     * 添加支出
     * @param expenseAmount 支出金额
     */
    public void addExpense(double expenseAmount) {
        for (Map.Entry<String, Double> entry : categoryBudgets.entrySet()) {
            double categoryAmount = entry.getValue();
            categoryAmount += expenseAmount;
            entry.setValue(categoryAmount);
        }
        this.lastModifiedDate = LocalDate.now();
    }
} 
