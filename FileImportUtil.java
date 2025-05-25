package com.finance.manager.util;

import com.finance.manager.model.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件导入工具类
 */
public class FileImportUtil {
    
    /**
     * 从CSV文件导入交易记录
     * @param file CSV文件
     * @return 交易记录列表
     * @throws IOException 文件读取错误
     */
    public static List<Transaction> importFromCSV(File file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (FileReader reader = new FileReader(file);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
                
            for (CSVRecord record : csvParser) {
                try {
                    // 解析日期
                    LocalDate date;
                    try {
                        date = LocalDate.parse(record.get("日期"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } catch (DateTimeParseException e) {
                        try {
                            date = LocalDate.parse(record.get("日期"), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                        } catch (DateTimeParseException ex) {
                            date = LocalDate.now(); // 使用默认日期
                        }
                    }
                    
                    // 解析类型
                    String type = record.get("类型");
                    if (!type.equals("支出") && !type.equals("收入")) {
                        type = "支出"; // 默认为支出
                    }
                    
                    // 解析金额
                    double amount;
                    try {
                        amount = Double.parseDouble(record.get("金额"));
                    } catch (NumberFormatException e) {
                        amount = 0.0; // 默认金额
                    }
                    
                    // 支付方式
                    String paymentMethod = record.get("支付方式");
                    if (paymentMethod == null || paymentMethod.isEmpty()) {
                        paymentMethod = "其他";
                    }
                    
                    // 备注
                    String notes = "";
                    if (record.isMapped("备注")) {
                        notes = record.get("备注");
                    }
                    
                    // 分类
                    String category = "";
                    if (record.isMapped("分类")) {
                        category = record.get("分类");
                    }
                    
                    // 创建交易对象
                    Transaction transaction = new Transaction(date, type, amount, paymentMethod, notes, category, false);
                    transactions.add(transaction);
                    
                } catch (Exception e) {
                    // 跳过有问题的记录
                    continue;
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * 从支付宝账单导入交易记录
     * @param file 支付宝账单文件
     * @return 交易记录列表
     * @throws IOException 文件读取错误
     */
    public static List<Transaction> importFromAlipay(File file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (FileReader reader = new FileReader(file);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
                
            for (CSVRecord record : csvParser) {
                try {
                    // 解析日期（支付宝格式：yyyy-MM-dd HH:mm:ss）
                    String dateTimeStr = record.get("交易创建时间");
                    LocalDate date;
                    try {
                        date = LocalDate.parse(dateTimeStr.split(" ")[0]);
                    } catch (Exception e) {
                        date = LocalDate.now();
                    }
                    
                    // 交易类型
                    String tradeType = record.get("交易类型");
                    String type;
                    if (tradeType.contains("收入") || tradeType.contains("退款")) {
                        type = "收入";
                    } else {
                        type = "支出";
                    }
                    
                    // 金额
                    double amount;
                    try {
                        String amountStr = record.get("金额");
                        amount = Double.parseDouble(amountStr.replaceAll("[¥,]", ""));
                    } catch (Exception e) {
                        amount = 0.0;
                    }
                    
                    // 其他信息
                    String paymentMethod = "支付宝";
                    String notes = record.get("商品名称");
                    String category = ""; // 需要自动分类
                    
                    // 创建交易对象
                    Transaction transaction = new Transaction(date, type, amount, paymentMethod, notes, category, false);
                    transactions.add(transaction);
                    
                } catch (Exception e) {
                    // 跳过有问题的记录
                    continue;
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * 从微信账单导入交易记录
     * @param file 微信账单文件
     * @return 交易记录列表
     * @throws IOException 文件读取错误
     */
    public static List<Transaction> importFromWechat(File file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (FileReader reader = new FileReader(file);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
                
            for (CSVRecord record : csvParser) {
                try {
                    // 解析日期（微信格式：yyyy-MM-dd HH:mm:ss）
                    String dateTimeStr = record.get("交易时间");
                    LocalDate date;
                    try {
                        date = LocalDate.parse(dateTimeStr.split(" ")[0]);
                    } catch (Exception e) {
                        date = LocalDate.now();
                    }
                    
                    // 交易类型
                    String tradeType = record.get("收/支");
                    String type;
                    if (tradeType.contains("收入")) {
                        type = "收入";
                    } else {
                        type = "支出";
                    }
                    
                    // 金额
                    double amount;
                    try {
                        String amountStr = record.get("金额(元)");
                        amount = Double.parseDouble(amountStr.replaceAll("[¥,]", ""));
                    } catch (Exception e) {
                        amount = 0.0;
                    }
                    
                    // 其他信息
                    String paymentMethod = "微信";
                    String notes = record.get("商品");
                    String category = ""; // 需要自动分类
                    
                    // 创建交易对象
                    Transaction transaction = new Transaction(date, type, amount, paymentMethod, notes, category, false);
                    transactions.add(transaction);
                    
                } catch (Exception e) {
                    // 跳过有问题的记录
                    continue;
                }
            }
        }
        
        return transactions;
    }
} 
