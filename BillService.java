package com.finance.manager.service;

import com.finance.manager.entity.Bill;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 账单服务类
 */
public class BillService {
    private List<Bill> bills;
    
    /**
     * 默认构造函数
     */
    public BillService() {
        this.bills = new ArrayList<>();
    }
    
    /**
     * 添加账单
     * @param bill 账单对象
     */
    public void addBill(Bill bill) {
        bills.add(bill);
    }
    
    /**
     * 获取所有账单
     * @return 账单列表
     */
    public List<Bill> getAllBills() {
        return bills;
    }
    
    /**
     * 根据用户ID获取账单
     * @param userId 用户ID
     * @return 该用户的账单列表
     */
    public List<Bill> getBillsByUserId(String userId) {
        return bills.stream()
                .filter(bill -> bill.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据月份获取账单
     * @param yearMonth 年月
     * @return 该月的账单列表
     */
    public List<Bill> getBillsByMonth(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        return bills.stream()
                .filter(bill -> !bill.getDate().isBefore(startDate) && !bill.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据用户ID和月份获取账单
     * @param userId 用户ID
     * @param yearMonth 年月
     * @return 该用户在该月的账单列表
     */
    public List<Bill> getBillsByUserIdAndMonth(String userId, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        return bills.stream()
                .filter(bill -> bill.getUserId().equals(userId))
                .filter(bill -> !bill.getDate().isBefore(startDate) && !bill.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取指定月份的收入总额
     * @param yearMonth 年月
     * @return 收入总额
     */
    public double getIncomeByMonth(YearMonth yearMonth) {
        return getBillsByMonth(yearMonth).stream()
                .filter(bill -> "收入".equals(bill.getType()))
                .mapToDouble(Bill::getAmount)
                .sum();
    }
    
    /**
     * 获取指定月份的支出总额
     * @param yearMonth 年月
     * @return 支出总额
     */
    public double getExpenseByMonth(YearMonth yearMonth) {
        return getBillsByMonth(yearMonth).stream()
                .filter(bill -> "支出".equals(bill.getType()))
                .mapToDouble(Bill::getAmount)
                .sum();
    }
    
    /**
     * 获取用户指定月份的分类汇总
     * @param userId 用户ID
     * @param yearMonth 年月
     * @return 分类金额映射
     */
    public Map<String, Double> getCategorySumByMonth(String userId, YearMonth yearMonth) {
        List<Bill> monthBills = getBillsByUserIdAndMonth(userId, yearMonth);
        Map<String, Double> categorySum = new HashMap<>();
        
        for (Bill bill : monthBills) {
            String category = bill.getCategory();
            double currentSum = categorySum.getOrDefault(category, 0.0);
            categorySum.put(category, currentSum + bill.getAmount());
        }
        
        return categorySum;
    }
    
    /**
     * 根据日期范围获取账单
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期范围内的账单列表
     */
    public List<Bill> getBillsByDateRange(LocalDate startDate, LocalDate endDate) {
        return bills.stream()
                .filter(bill -> !bill.getDate().isBefore(startDate) && !bill.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }
} 
