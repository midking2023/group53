package com.finance.manager.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;

/**
 * 图表工具类
 * 提供处理图表的通用方法
 */
public class ChartUtil {

    /**
     * 为饼图添加数值和百分比标签
     * @param pieChart 饼图对象
     * @param totalAmount 总金额
     */
    public static void addValueAndPercentageToPieChart(PieChart pieChart, double totalAmount) {
        ObservableList<PieChart.Data> data = pieChart.getData();
        
        // 获取饼图的父容器
        Pane parent = (Pane) pieChart.getParent();
        if (parent == null) {
            // 如果饼图还没有父容器，我们不能添加标签
            return;
        }
        
        for (PieChart.Data item : data) {
            double value = item.getPieValue();
            double percentage = (value / totalAmount) * 100;
            
            // 创建标签
            Label label = new Label(String.format("¥%.0f\n(%.1f%%)", value, percentage));
            label.setFont(Font.font("System", FontWeight.BOLD, 10));
            label.setTextFill(Color.WHITE);
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            
            // 当数据发生变化时更新标签位置
            item.getNode().layoutXProperty().addListener((obs, oldVal, newVal) -> {
                // 获取饼图数据的中心位置，转换为父容器的坐标系
                Node node = item.getNode();
                double centerX = pieChart.localToParent(
                        node.getBoundsInParent().getCenterX(),
                        node.getBoundsInParent().getCenterY()).getX();
                double centerY = pieChart.localToParent(
                        node.getBoundsInParent().getCenterX(),
                        node.getBoundsInParent().getCenterY()).getY();
                
                // 计算标签位置（稍微偏移以便更好地显示在饼图扇区内）
                double x = centerX - label.getWidth() / 2;
                double y = centerY - label.getHeight() / 2;
                
                // 设置标签位置
                label.setLayoutX(x);
                label.setLayoutY(y);
            });
            
            // 将标签添加到饼图的父容器
            parent.getChildren().add(label);
        }
        
        // 移除旧标签（如果有）
        pieChart.setLabelsVisible(false);
    }
    
    /**
     * 为饼图设置自定义颜色
     * @param pieChart 饼图对象
     * @param colors 颜色数组
     */
    public static void setCustomColors(PieChart pieChart, Color[] colors) {
        int colorIndex = 0;
        for (PieChart.Data data : pieChart.getData()) {
            Node node = data.getNode();
            Color color = colors[colorIndex % colors.length];
            
            String rgb = String.format("#%02X%02X%02X",
                    (int)(color.getRed() * 255),
                    (int)(color.getGreen() * 255),
                    (int)(color.getBlue() * 255));
                    
            node.setStyle("-fx-pie-color: " + rgb + ";");
            colorIndex++;
        }
    }
    
    /**
     * 设置饼图的统一样式
     * @param pieChart 饼图对象
     */
    public static void stylePieChart(PieChart pieChart) {
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(false);
        pieChart.setAnimated(true);
        pieChart.setClockwise(true);
        pieChart.setStartAngle(90);
    }
    
    /**
     * 创建饼图数据
     * @param dataMap 数据映射（分类 -> 金额）
     * @return 饼图数据
     */
    public static ObservableList<PieChart.Data> createPieChartData(Map<String, Double> dataMap) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            String category = entry.getKey();
            Double amount = Math.abs(entry.getValue()); // 使用绝对值
            
            if (amount > 0) {
                pieChartData.add(new PieChart.Data(category, amount));
            }
        }
        
        return pieChartData;
    }
    
    /**
     * 创建线图数据
     * @param dataMap 数据映射（X轴标签 -> Y轴值）
     * @param seriesName 系列名称
     * @return 线图数据
     */
    public static XYChart.Series<String, Number> createLineChartData(Map<String, Double> dataMap, String seriesName) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);
        
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        return series;
    }
    
    /**
     * 创建条形图数据
     * @param dataMap 数据映射（X轴标签 -> Y轴值）
     * @param seriesName 系列名称
     * @return 条形图数据
     */
    public static XYChart.Series<String, Number> createBarChartData(Map<String, Double> dataMap, String seriesName) {
        return createLineChartData(dataMap, seriesName); // 创建方法相同
    }
    
    /**
     * 生成饼图切片颜色
     * @param index 颜色索引
     * @return 颜色对象
     */
    public static Color getPieChartColor(int index) {
        Color[] colors = {
            Color.web("#f3622d"),
            Color.web("#fba71b"),
            Color.web("#57b757"),
            Color.web("#41a9c9"),
            Color.web("#4258c9"),
            Color.web("#9a42c8"),
            Color.web("#c84164"),
            Color.web("#888888")
        };
        
        return colors[index % colors.length];
    }
    
    /**
     * 设置饼图颜色样式
     * @param pieChart 饼图对象
     */
    public static void applyPieChartColorStyle(PieChart pieChart) {
        int count = pieChart.getData().size();
        
        // 为每个切片设置不同的颜色
        for (int i = 0; i < count; i++) {
            PieChart.Data slice = pieChart.getData().get(i);
            String color = String.format("#%02X%02X%02X",
                    (int) (getPieChartColor(i).getRed() * 255),
                    (int) (getPieChartColor(i).getGreen() * 255),
                    (int) (getPieChartColor(i).getBlue() * 255));
            
            String style = String.format("-fx-pie-color: %s;", color);
            slice.getNode().setStyle(style);
        }
    }
} 
