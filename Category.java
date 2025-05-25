package com.finance.manager.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易分类实体类
 */
public class Category {
    private String id;
    private String name;
    private String type; // 收入/支出
    private String color;
    private String icon;
    private String description;
    private String userId;
    private boolean isDefault;
    private List<String> keywords;
    
    /**
     * 默认构造函数
     */
    public Category() {
        this.keywords = new ArrayList<>();
        this.isDefault = false;
    }
    
    /**
     * 带名称和类型的构造函数
     * @param name 分类名称
     * @param type 分类类型（收入/支出）
     */
    public Category(String name, String type) {
        this();
        this.name = name;
        this.type = type;
    }
    
    /**
     * 完整参数构造函数
     * @param id 分类ID
     * @param name 分类名称
     * @param type 分类类型
     * @param color 显示颜色
     * @param icon 图标
     * @param description 描述
     * @param userId 用户ID
     * @param isDefault 是否为默认分类
     */
    public Category(String id, String name, String type, String color, String icon, 
                   String description, String userId, boolean isDefault) {
        this(name, type);
        this.id = id;
        this.color = color;
        this.icon = icon;
        this.description = description;
        this.userId = userId;
        this.isDefault = isDefault;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public List<String> getKeywords() {
        return keywords;
    }
    
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    
    public void addKeyword(String keyword) {
        this.keywords.add(keyword);
    }
    
    @Override
    public String toString() {
        return name;
    }
} 
