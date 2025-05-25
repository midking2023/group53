package com.finance.manager.controller;

import com.finance.manager.model.Transaction;
import com.finance.manager.model.User;
import com.finance.manager.utils.DataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支出分析控制器
 * 处理支出分析相关功能
 */
public class ExpenseController {

    @FXML
    private Label totalExpenseLabel;

    @FXML
    private Label topCategoryLabel;

    @FXML
    private Label topCategoryPercentLabel;

    @FXML
    private PieChart categoryPieChart;

    @FXML
    private BarChart<String, Number> categoryBarChart;

    @FXML
    private TableView<Transaction> expenseTable;

    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> categoryColumn;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Button nextButton;

    private User currentUser;
    private DataService dataService;
    private List<Transaction> transactions;

    /**
     * 初始化方法
     * JavaFX在加载FXML后自动调用
     */
    @FXML
    public void initialize() {
        // 获取数据服务实例
        dataService = DataService.getInstance();
        
        // 设置表格列
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
    }

    /**
     * 初始化用户数据
     * @param user 当前用户
     */
    public void initData(User user) {
        this.currentUser = user;
        
        // 加载交易数据
        loadTransactionData();
        
        // 更新UI
        updateUI();
    }
    
    /**
     * 处理前一页按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handlePrevPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent root = loader.load();
            
            HomeController controller = loader.getController();
            controller.initData(currentUser);
            
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 处理下一页按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleNextPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/category.fxml"));
            Parent root = loader.load();
            
            CategoryController controller = loader.getController();
            controller.initData(currentUser);
            
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载交易数据
     */
    private void loadTransactionData() {
        // 获取用户的所有交易记录
        transactions = dataService.getUserTransactions(currentUser.getUsername());
    }

    /**
     * 更新UI显示
     */
    private void updateUI() {
        if (transactions == null || transactions.isEmpty()) {
            // 没有交易数据
            totalExpenseLabel.setText("0.00 " + currentUser.getCurrency());
            topCategoryLabel.setText("无数据");
            topCategoryPercentLabel.setText("0%");
            
            // 清空图表和表格
            categoryPieChart.setData(FXCollections.observableArrayList());
            categoryBarChart.getData().clear();
            expenseTable.setItems(FXCollections.observableArrayList());
            
            return;
        }
        
        // 计算总支出
        double totalExpense = transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        totalExpenseLabel.setText(String.format("%.2f %s", totalExpense, currentUser.getCurrency()));
        
        // 按类别分组统计
        Map<String, Double> categoryExpenses = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> {
                            String category = transaction.getCategory();
                            return category == null || category.isEmpty() ? "未分类" : category;
                        },
                        Collectors.summingDouble(Transaction::getAmount)
                ));
        
        // 更新类别饼图
        updateCategoryPieChart(categoryExpenses);
        
        // 更新类别条形图
        updateCategoryBarChart(categoryExpenses);
        
        // 更新表格
        updateExpenseTable();
        
        // 更新顶部类别标签
        updateTopCategoryLabel(categoryExpenses, totalExpense);
    }

    /**
     * 更新类别饼图
     * @param categoryExpenses 类别支出数据
     */
    private void updateCategoryPieChart(Map<String, Double> categoryExpenses) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        categoryExpenses.forEach((category, amount) -> {
            pieChartData.add(new PieChart.Data(category, amount));
        });
        
        categoryPieChart.setData(pieChartData);
    }

    /**
     * 更新类别条形图
     * @param categoryExpenses 类别支出数据
     */
    private void updateCategoryBarChart(Map<String, Double> categoryExpenses) {
        categoryBarChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("类别支出");
        
        // 按金额降序排序并取前10个类别
        categoryExpenses.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });
        
        categoryBarChart.getData().add(series);
    }

    /**
     * 更新支出表格
     */
    private void updateExpenseTable() {
        // 按日期降序排序
        List<Transaction> sortedTransactions = transactions.stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .collect(Collectors.toList());
        
        expenseTable.setItems(FXCollections.observableArrayList(sortedTransactions));
    }

    /**
     * 更新顶部类别标签
     * @param categoryExpenses 类别支出数据
     * @param totalExpense 总支出
     */
    private void updateTopCategoryLabel(Map<String, Double> categoryExpenses, double totalExpense) {
        if (categoryExpenses.isEmpty()) {
            topCategoryLabel.setText("无数据");
            topCategoryPercentLabel.setText("0%");
            return;
        }
        
        // 获取支出最高的类别
        Map.Entry<String, Double> topCategory = categoryExpenses.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        
        if (topCategory != null) {
            topCategoryLabel.setText(topCategory.getKey());
            
            // 计算百分比
            double percent = (topCategory.getValue() / totalExpense) * 100;
            topCategoryPercentLabel.setText(String.format("%.2f%%", percent));
        }
    }
}
