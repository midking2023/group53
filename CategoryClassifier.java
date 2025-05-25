package com.finance.manager.util;

import com.finance.manager.entity.Category;
import com.finance.manager.model.Transaction;
import com.finance.manager.service.CategoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易分类器工具类
 */
public class CategoryClassifier {
    private static final Map<String, String> KEYWORD_MAP = initKeywordMap();
    
    /**
     * 初始化关键词映射表
     * @return 关键词到分类的映射
     */
    private static Map<String, String> initKeywordMap() {
        Map<String, String> keywordMap = new HashMap<>();
        
        // 餐饮类关键词
        keywordMap.put("餐厅", "餐饮");
        keywordMap.put("饭店", "餐饮");
        keywordMap.put("食堂", "餐饮");
        keywordMap.put("外卖", "餐饮");
        keywordMap.put("美食", "餐饮");
        keywordMap.put("麦当劳", "餐饮");
        keywordMap.put("肯德基", "餐饮");
        keywordMap.put("必胜客", "餐饮");
        keywordMap.put("奶茶", "餐饮");
        keywordMap.put("咖啡", "餐饮");
        
        // 交通类关键词
        keywordMap.put("打车", "交通");
        keywordMap.put("出租车", "交通");
        keywordMap.put("滴滴", "交通");
        keywordMap.put("公交", "交通");
        keywordMap.put("地铁", "交通");
        keywordMap.put("火车", "交通");
        keywordMap.put("飞机", "交通");
        keywordMap.put("机票", "交通");
        keywordMap.put("高铁", "交通");
        keywordMap.put("加油", "交通");
        
        // 购物类关键词
        keywordMap.put("超市", "购物");
        keywordMap.put("商场", "购物");
        keywordMap.put("淘宝", "购物");
        keywordMap.put("京东", "购物");
        keywordMap.put("拼多多", "购物");
        keywordMap.put("购物", "购物");
        keywordMap.put("衣服", "购物");
        keywordMap.put("鞋子", "购物");
        keywordMap.put("网购", "购物");
        
        // 娱乐类关键词
        keywordMap.put("电影", "娱乐");
        keywordMap.put("游戏", "娱乐");
        keywordMap.put("KTV", "娱乐");
        keywordMap.put("演唱会", "娱乐");
        keywordMap.put("剧场", "娱乐");
        keywordMap.put("酒吧", "娱乐");
        keywordMap.put("健身", "娱乐");
        
        // 居家类关键词
        keywordMap.put("房租", "居家");
        keywordMap.put("水费", "居家");
        keywordMap.put("电费", "居家");
        keywordMap.put("燃气", "居家");
        keywordMap.put("物业", "居家");
        keywordMap.put("宽带", "居家");
        keywordMap.put("网费", "居家");
        keywordMap.put("家具", "居家");
        keywordMap.put("家电", "居家");
        
        // 医疗类关键词
        keywordMap.put("医院", "医疗");
        keywordMap.put("药店", "医疗");
        keywordMap.put("药房", "医疗");
        keywordMap.put("诊所", "医疗");
        keywordMap.put("挂号", "医疗");
        keywordMap.put("药品", "医疗");
        
        // 教育类关键词
        keywordMap.put("学费", "教育");
        keywordMap.put("书店", "教育");
        keywordMap.put("课程", "教育");
        keywordMap.put("培训", "教育");
        keywordMap.put("教材", "教育");
        keywordMap.put("辅导", "教育");
        
        // A旅行类关键词
        keywordMap.put("酒店", "旅行");
        keywordMap.put("旅馆", "旅行");
        keywordMap.put("民宿", "旅行");
        keywordMap.put("景点", "旅行");
        keywordMap.put("旅游", "旅行");
        keywordMap.put("度假", "旅行");
        
        // 收入类关键词
        keywordMap.put("工资", "工资");
        keywordMap.put("薪资", "工资");
        keywordMap.put("奖金", "奖金");
        keywordMap.put("报销", "报销");
        keywordMap.put("退款", "报销");
        keywordMap.put("投资", "投资");
        keywordMap.put("分红", "投资");
        keywordMap.put("理财", "投资");
        
        return keywordMap;
    }
    
    /**
     * 根据交易描述自动分类
     * @param description 交易描述
     * @param type 交易类型（收入/支出）
     * @return 分类名称，无法分类时返回null
     */
    public static String classifyByDescription(String description, String type) {
        if (description == null || description.isEmpty()) {
            return null;
        }
        
        String lowerDesc = description.toLowerCase();
        
        for (Map.Entry<String, String> entry : KEYWORD_MAP.entrySet()) {
            if (lowerDesc.contains(entry.getKey().toLowerCase())) {
                // 确保收入和支出分类匹配
                if (type.equals("收入") && (
                    entry.getValue().equals("工资") || 
                    entry.getValue().equals("奖金") || 
                    entry.getValue().equals("报销") || 
                    entry.getValue().equals("投资"))) {
                    return entry.getValue();
                } else if (type.equals("支出") && !(
                    entry.getValue().equals("工资") || 
                    entry.getValue().equals("奖金") || 
                    entry.getValue().equals("报销") || 
                    entry.getValue().equals("投资"))) {
                    return entry.getValue();
                }
            }
        }
        
        // 无法分类时使用默认分类
        return type.equals("收入") ? "其他收入" : "其他支出";
    }
    
    /**
     * 使用分类服务对交易进行分类
     * @param transaction 交易对象
     * @param categoryService 分类服务
     * @return 是否成功分类
     */
    public static boolean classifyTransaction(Transaction transaction, CategoryService categoryService) {
        if (transaction == null || categoryService == null) {
            return false;
        }
        
        // 已有分类，不再处理
        if (transaction.getCategory() != null && !transaction.getCategory().isEmpty()) {
            return false;
        }
        
        // 使用备注或描述进行分类
        String description = transaction.getNotes();
        if (description == null || description.isEmpty()) {
            description = transaction.getDescription();
        }
        
        if (description != null && !description.isEmpty()) {
            // 先使用分类服务的分类器
            Category category = categoryService.classifyByDescription(description);
            
            if (category != null && category.getType().equals(transaction.getType())) {
                transaction.setCategory(category.getName());
                transaction.setAutoCategorized(true);
                return true;
            }
            
            // 如果分类服务没有找到匹配，使用内置关键词分类
            String categoryName = classifyByDescription(description, transaction.getType());
            if (categoryName != null) {
                transaction.setCategory(categoryName);
                transaction.setAutoCategorized(true);
                return true;
            }
        }
        
        // 没有足够信息分类
        return false;
    }
    
    /**
     * 批量对交易列表进行分类
     * @param transactions 交易列表
     * @param categoryService 分类服务
     * @return 成功分类的交易数量
     */
    public static int classifyTransactions(List<Transaction> transactions, CategoryService categoryService) {
        if (transactions == null || transactions.isEmpty() || categoryService == null) {
            return 0;
        }
        
        int count = 0;
        for (Transaction transaction : transactions) {
            if (classifyTransaction(transaction, categoryService)) {
                count++;
            }
        }
        
        return count;
    }
} 
