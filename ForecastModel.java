package com.finance.manager.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消费预测模型
 * 用于生成和存储消费预测数据
 */
public class ForecastModel {
    private String id;
    private String userId;
    private YearMonth targetMonth;
    private LocalDate createdDate;
    private double forecastedAmount;
    private double confidenceLevel;  // 0-100 置信度
    private double forecastChangeRate;  // 与历史相比变化率
    private ForecastStatus status;
    
    private Map<String, Double> categoryForecasts; // 类别别预测金额
    private List<Transaction> historicalTransactions; // 用于预测的历史交易
    
    /**
     * 预测状态枚举
     */
    public enum ForecastStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
    
    /**
     * 无参构造函数
     */
    public ForecastModel() {
        this.id = UUID.randomUUID().toString();
        this.createdDate = LocalDate.now();
        this.status = ForecastStatus.PENDING;
        this.categoryForecasts = new HashMap<>();
        this.historicalTransactions = new ArrayList<>();
    }
    
    /**
     * 构造函数
     * @param userId 用户ID
     * @param targetMonth 目标月份
     */
    public ForecastModel(String userId, YearMonth targetMonth) {
        this();
        this.userId = userId;
        this.targetMonth = targetMonth;
    }
    
    /**
     * 根据历史交易数据生成预测
     * @param transactions 历史交易数据
     * @return 是否成功生成预测
     */
    public boolean generateForecast(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            this.status = ForecastStatus.FAILED;
            return false;
        }
        
        try {
            this.status = ForecastStatus.IN_PROGRESS;
            
            // 过滤出支出交易
            List<Transaction> expenseTransactions = transactions.stream()
                    .filter(t -> "支出".equals(t.getType()))
                    .collect(Collectors.toList());
            
            if (expenseTransactions.isEmpty()) {
                this.status = ForecastStatus.FAILED;
                return false;
            }
            
            // 保存历史交易
            this.historicalTransactions = new ArrayList<>(expenseTransactions);
            
            // 计算基本预测
            generateBasicForecast(expenseTransactions);
            
            // 生成类别预测
            generateCategoryForecasts(expenseTransactions);
            
            // 计算置信度
            calculateConfidenceLevel(expenseTransactions);
            
            this.status = ForecastStatus.COMPLETED;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            this.status = ForecastStatus.FAILED;
            return false;
        }
    }
    
    /**
     * 生成基本预测
     * @param transactions 支出交易
     */
    private void generateBasicForecast(List<Transaction> transactions) {
        // 按月分组计算平均消费
        Map<YearMonth, Double> monthlyTotals = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            YearMonth month = YearMonth.from(transaction.getDate());
            monthlyTotals.put(month, 
                    monthlyTotals.getOrDefault(month, 0.0) + transaction.getAmount());
        }
        
        // 计算总预测（基于最近3个月平均）
        List<Double> recentMonthlyTotals = getRecentMonthlyTotals(monthlyTotals, 3);
        
        if (!recentMonthlyTotals.isEmpty()) {
            // 计算平均值
            double average = recentMonthlyTotals.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            // 加上季节性和趋势调整
            this.forecastedAmount = adjustForTrendsAndSeasons(average, monthlyTotals);
            
            // 计算变化率
            calculateChangeRate(average);
        } else {
            // 无足够数据，使用所有月份平均
            double average = monthlyTotals.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            
            this.forecastedAmount = average;
            this.forecastChangeRate = 0.0;
        }
    }
    
    /**
     * 获取最近几个月的总支出
     * @param monthlyTotals 按月汇总的支出
     * @param monthCount 月份数
     * @return 最近几个月的支出列表
     */
    private List<Double> getRecentMonthlyTotals(Map<YearMonth, Double> monthlyTotals, int monthCount) {
        return monthlyTotals.entrySet().stream()
                .sorted(Map.Entry.<YearMonth, Double>comparingByKey().reversed())
                .limit(monthCount)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据趋势和季节性调整预测
     * @param baseAmount 基础金额
     * @param monthlyTotals 月度总支出
     * @return 调整后的金额
     */
    private double adjustForTrendsAndSeasons(double baseAmount, Map<YearMonth, Double> monthlyTotals) {
        // 检查是否有足够的数据来分析趋势
        if (monthlyTotals.size() < 6) {
            return baseAmount; // 数据不足，返回原值
        }
        
        // 获取最近6个月按时间排序的数据
        List<Map.Entry<YearMonth, Double>> sortedEntries = monthlyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .skip(Math.max(0, monthlyTotals.size() - 6))
                .collect(Collectors.toList());
        
        // 计算最简单的线性趋势（平均月增长率）
        double trendFactor = 0.0;
        if (sortedEntries.size() > 1) {
            double firstMonth = sortedEntries.get(0).getValue();
            double lastMonth = sortedEntries.get(sortedEntries.size() - 1).getValue();
            int monthSpan = sortedEntries.size() - 1;
            
            if (firstMonth > 0) {
                // 月平均增长率
                trendFactor = (lastMonth / firstMonth - 1) / monthSpan;
            }
        }
        
        // 季节性因子（简化版）：查找去年同月或近似月份的调整因子
        double seasonalFactor = 1.0;
        YearMonth lastYear = targetMonth.minusYears(1);
        
        // 查找去年同月的数据
        if (monthlyTotals.containsKey(lastYear)) {
            double yearAgoAmount = monthlyTotals.get(lastYear);
            double yearAverageAmount = monthlyTotals.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(yearAgoAmount);
            
            if (yearAverageAmount > 0) {
                seasonalFactor = yearAgoAmount / yearAverageAmount;
            }
        }
        
        // 应用趋势和季节性调整
        // 趋势：假设线性趋势延续到目标月
        // 季节性：应用季节性因子
        return baseAmount * (1 + trendFactor) * seasonalFactor;
    }
    
    /**
     * 计算与历史平均的变化率
     * @param historicalAverage 历史平均值
     */
    private void calculateChangeRate(double historicalAverage) {
        if (historicalAverage > 0) {
            this.forecastChangeRate = ((this.forecastedAmount - historicalAverage) / historicalAverage) * 100;
        } else {
            this.forecastChangeRate = 0.0;
        }
    }
    
    /**
     * 生成类别预测
     * @param transactions 支出交易
     */
    private void generateCategoryForecasts(List<Transaction> transactions) {
        // 按类别分组计算每个类别的历史消费
        Map<String, List<Double>> categoryAmounts = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            String category = transaction.getCategory();
            if (!categoryAmounts.containsKey(category)) {
                categoryAmounts.put(category, new ArrayList<>());
            }
            categoryAmounts.get(category).add(transaction.getAmount());
        }
        
        // 为每个类别生成预测
        double totalPredicted = 0.0;
        
        for (Map.Entry<String, List<Double>> entry : categoryAmounts.entrySet()) {
            String category = entry.getKey();
            List<Double> amounts = entry.getValue();
            
            // 计算该类别的平均消费
            double average = amounts.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
                    
            // 计算该类别在总支出中的占比
            double proportion = amounts.stream()
                    .mapToDouble(Double::doubleValue)
                    .sum() / transactions.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();
                    
            // 分配预测金额
            double categoryForecast = this.forecastedAmount * proportion;
            
            // 添加到类别预测
            this.categoryForecasts.put(category, categoryForecast);
            totalPredicted += categoryForecast;
        }
        
        // 校正总金额
        if (totalPredicted > 0) {
            double correctionFactor = this.forecastedAmount / totalPredicted;
            
            for (String category : this.categoryForecasts.keySet()) {
                this.categoryForecasts.put(category, 
                        this.categoryForecasts.get(category) * correctionFactor);
            }
        }
    }
    
    /**
     * 计算预测置信度
     * @param transactions 支出交易
     */
    private void calculateConfidenceLevel(List<Transaction> transactions) {
        // 基础置信度
        double baseConfidence = 50.0;  // 默认起点
        
        // 数据量因子 (0-20)
        double dataSizeFactor = Math.min(20.0, transactions.size() / 5.0);
        
        // 一致性因子 (0-15)
        double consistencyFactor = calculateConsistencyFactor(transactions);
        
        // 历史周期覆盖因子 (0-15)
        double coverageFactor = calculateCoverageFactor(transactions);
        
        // 计算总置信度 (满分100)
        this.confidenceLevel = Math.min(100.0, baseConfidence + dataSizeFactor + consistencyFactor + coverageFactor);
    }
    
    /**
     * 计算数据一致性因子
     * @param transactions 支出交易
     * @return 一致性因子 (0-15)
     */
    private double calculateConsistencyFactor(List<Transaction> transactions) {
        // 按月分组计算波动
        Map<YearMonth, Double> monthlyTotals = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            YearMonth month = YearMonth.from(transaction.getDate());
            monthlyTotals.put(month, 
                    monthlyTotals.getOrDefault(month, 0.0) + transaction.getAmount());
        }
        
        if (monthlyTotals.size() < 2) {
            return 0.0;  // 数据不足
        }
        
        // 计算月度消费的变异系数 (标准差/平均值)
        double mean = monthlyTotals.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
                
        if (mean <= 0) {
            return 0.0;
        }
        
        double variance = monthlyTotals.values().stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .sum() / monthlyTotals.size();
                
        double stdDev = Math.sqrt(variance);
        double cv = stdDev / mean;  // 变异系数
        
        // 变异系数越小，一致性越高
        // 假设CV < 0.2为高一致性，CV > 0.5为低一致性
        if (cv < 0.2) {
            return 15.0;  // 高一致性
        } else if (cv < 0.3) {
            return 10.0;
        } else if (cv < 0.5) {
            return 5.0;
        } else {
            return 0.0;  // 低一致性
        }
    }
    
    /**
     * 计算历史周期覆盖因子
     * @param transactions 支出交易
     * @return 覆盖因子 (0-15)
     */
    private double calculateCoverageFactor(List<Transaction> transactions) {
        // 检查交易的时间跨度
        if (transactions.isEmpty()) {
            return 0.0;
        }
        
        // 找到最早和最晚的交易日期
        LocalDate earliestDate = transactions.stream()
                .map(Transaction::getDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
                
        LocalDate latestDate = transactions.stream()
                .map(Transaction::getDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
                
        // 计算月份跨度
        long monthSpan = YearMonth.from(latestDate).compareTo(YearMonth.from(earliestDate)) + 1;
        
        // 计算实际有数据的月份数
        Set<YearMonth> coveredMonths = transactions.stream()
                .map(t -> YearMonth.from(t.getDate()))
                .collect(Collectors.toSet());
                
        int actualMonths = coveredMonths.size();
        
        // 计算覆盖率
        double coverage = Math.min(1.0, (double) actualMonths / Math.max(1, monthSpan));
        
        // 时间跨度因子 - 覆盖至少12个月得满分
        double timeSpanFactor = Math.min(7.5, actualMonths / 12.0 * 7.5);
        
        // 覆盖率因子
        double coverageRateFactor = coverage * 7.5;
        
        return timeSpanFactor + coverageRateFactor;
    }
    
    /**
     * 获取类别预测金额
     * @param category 类别
     * @return 预测金额
     */
    public double getCategoryForecastAmount(String category) {
        return categoryForecasts.getOrDefault(category, 0.0);
    }
    
    // Getters and Setters
    
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
    
    public YearMonth getTargetMonth() {
        return targetMonth;
    }
    
    public void setTargetMonth(YearMonth targetMonth) {
        this.targetMonth = targetMonth;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public double getForecastedAmount() {
        return forecastedAmount;
    }
    
    public void setForecastedAmount(double forecastedAmount) {
        this.forecastedAmount = forecastedAmount;
    }
    
    public double getConfidenceLevel() {
        return confidenceLevel;
    }
    
    public void setConfidenceLevel(double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }
    
    public double getForecastChangeRate() {
        return forecastChangeRate;
    }
    
    public void setForecastChangeRate(double forecastChangeRate) {
        this.forecastChangeRate = forecastChangeRate;
    }
    
    public ForecastStatus getStatus() {
        return status;
    }
    
    public void setStatus(ForecastStatus status) {
        this.status = status;
    }
    
    public Map<String, Double> getCategoryForecasts() {
        return categoryForecasts;
    }
    
    public void setCategoryForecasts(Map<String, Double> categoryForecasts) {
        this.categoryForecasts = categoryForecasts;
    }
    
    public List<Transaction> getHistoricalTransactions() {
        return historicalTransactions;
    }
    
    public void setHistoricalTransactions(List<Transaction> historicalTransactions) {
        this.historicalTransactions = historicalTransactions;
    }
} 
