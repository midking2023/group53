package com.finance.manager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用户实体类
 * 存储用户的基本信息
 */
public class User {
    private String id;
    private String username; // 用户名
    private String password; // 密码（实际开发中应加密存储）
    private String email; // 电子邮件
    private String phone; // 手机号
    private String nickname; // 昵称
    private String avatar; // 头像路径
    private String language; // 语言偏好
    private String currency; // 货币偏好
    private String region; // 地区
    private String paymentMethod; // 常用支付方式
    private boolean notificationEnabled; // 通知开关
    private String salt;
    private Map<String, String> preferences;
    private List<Transaction> transactions;
    private List<Budget> budgets;

    /**
     * 无参构造函数
     */
    public User() {
        this.transactions = new ArrayList<>();
        this.budgets = new ArrayList<>();
        this.language = "中文";
        this.currency = "¥";
        this.region = "中国";
        this.notificationEnabled = true;
        this.preferences = new HashMap<>();
    }

    /**
     * 带主要参数的构造函数
     *
     * @param username 用户名
     * @param password 密码
     * @param email 电子邮件
     * @param phone 手机号
     */
    public User(String username, String password, String email, String phone) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.nickname = username; // 默认使用用户名作为昵称
    }

    /**
     * 全参数构造函数
     * @param id 用户ID
     * @param username 用户名
     * @param password 密码
     * @param nickname 昵称
     * @param email 邮箱
     * @param phone 电话
     * @param avatar 头像路径
     * @param language 语言
     * @param currency 货币符号
     * @param region 地区
     * @param notificationEnabled 是否启用通知
     * @param paymentMethod 支付方式
     * @param salt 加密盐值
     */
    public User(String id, String username, String password, String nickname, String email, String phone, 
                String avatar, String language, String currency, String region, boolean notificationEnabled, 
                String paymentMethod, String salt) {
        this(username, password, email, phone);
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
        this.language = language;
        this.currency = currency;
        this.region = region;
        this.notificationEnabled = notificationEnabled;
        this.paymentMethod = paymentMethod;
        this.salt = salt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Map<String, String> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

    public void addPreference(String key, String value) {
        this.preferences.put(key, value);
    }

    public String getPreference(String key) {
        return this.preferences.get(key);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public List<Budget> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    public void addBudget(Budget budget) {
        this.budgets.add(budget);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
} 
