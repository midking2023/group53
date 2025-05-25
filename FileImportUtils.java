package com.finance.manager.utils;

import com.finance.manager.model.Transaction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件导入工具类
 * 用于处理CSV等格式的交易数据导入
 */
public class FileImportUtil {
    // 默认日期格式
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // 备用日期格式
    private static final DateTimeFormatter[] DATE_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy")
    };
    
    /**
     * 从CSV文件导入交易数据
     * @param file CSV文件
     * @return 交易记录列表
     * @throws IOException 如果文件读取失败
     */
    public static List<Transaction> importFromCSV(File file) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // 跳过CSV头行
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // 解析CSV行
                String[] values = line.split(",");
                if (values.length >= 4) {
                    try {
                        // 解析日期
                        LocalDate date = LocalDate.parse(values[0].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        
                        // 解析类型
                        String type = values[1].trim();
                        
                        // 解析金额
                        double amount = Double.parseDouble(values[2].trim());
                        
                        // 解析支付方式
                        String paymentMethod = values[3].trim();
                        
                        // 创建交易记录
                        Transaction transaction = new Transaction(date, type, amount, paymentMethod);
                        
                        // 如果有备注信息
                        if (values.length >= 5) {
                            transaction.setNotes(values[4].trim());
                        }
                        
                        // 添加到列表
                        transactions.add(transaction);
                    } catch (Exception e) {
                        System.err.println("解析行失败: " + line + ", 错误: " + e.getMessage());
                    }
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * 尝试使用多种格式解析日期
     * @param dateString 日期字符串
     * @return 解析后的LocalDate对象
     * @throws DateTimeParseException 如果无法解析日期
     */
    private static LocalDate parseDate(String dateString) throws DateTimeParseException {
        // 先尝试默认格式
        try {
            return LocalDate.parse(dateString, DEFAULT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            // 尝试备用格式
            for (DateTimeFormatter formatter : DATE_FORMATS) {
                try {
                    return LocalDate.parse(dateString, formatter);
                } catch (DateTimeParseException ex) {
                    // 继续尝试下一个格式
                }
            }
            
            // 所有格式都失败，抛出异常
            throw new DateTimeParseException("无法解析日期: " + dateString, dateString, 0);
        }
    }
    
    /**
     * 判断文件是否为CSV文件
     * @param file 文件
     * @return 是否为CSV文件
     */
    public static boolean isCSVFile(File file) {
        return file != null && file.getName().toLowerCase().endsWith(".csv");
    }
} 
