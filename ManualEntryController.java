package com.finance.manager.controller;

import com.finance.manager.model.Transaction;
import com.finance.manager.model.User;
import com.finance.manager.utils.CategoryClassifier;
import com.finance.manager.utils.DataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * 手动录入控制器
 * 处理手动录入交易数据的功能
 */
public class ManualEntryController implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<String> paymentMethodComboBox;

    @FXML
    private TextArea notesArea;

    @FXML
    private Text errorText;

    @FXML
    private Text successText;

    @FXML
    private Button clearButton;

    @FXML
    private Button saveButton;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button refreshButton;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Button nextButton;

    @FXML
    private TableView<Transaction> transactionsTable;

    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> paymentMethodColumn;

    @FXML
    private TableColumn<Transaction, String> notesColumn;

    private User currentUser;
    private DataService dataService;
    private List<Transaction> transactions;

    // 预定义的交易类型
    private final String[] TRANSACTION_TYPES = {
            "餐饮", "购物", "交通", "住房", "娱乐", "教育", "医疗", "通讯", "工资", "投资", "其他"
    };

    // 预定义的支付方式
    private final String[] PAYMENT_METHODS = {
            "现金", "支付宝", "微信", "银行卡", "信用卡", "其他"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 获取数据服务实例
        dataService = DataService.getInstance();
        
        // 初始化日期选择器
        datePicker.setValue(LocalDate.now());
        
        // 初始化交易类型下拉框
        typeComboBox.setItems(FXCollections.observableArrayList(TRANSACTION_TYPES));
        
        // 初始化支付方式下拉框
        paymentMethodComboBox.setItems(FXCollections.observableArrayList(PAYMENT_METHODS));
        
        // 初始化过滤器下拉框
        ObservableList<String> filterItems = FXCollections.observableArrayList("所有类型");
        filterItems.addAll(Arrays.asList(TRANSACTION_TYPES));
        filterComboBox.setItems(filterItems);
        filterComboBox.setValue("所有类型");
        
        // 设置过滤器监听
        filterComboBox.setOnAction(event -> filterTransactions());
        
        // 设置表格列
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }

    /**
     * 初始化用户数据
     * @param user 当前用户
     */
    public void initData(User user) {
        this.currentUser = user;
    }

    /**
     * 处理保存按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleSave(ActionEvent event) {
        // 隐藏之前的消息
        errorText.setVisible(false);
        errorText.setManaged(false);
        successText.setVisible(false);
        successText.setManaged(false);
        
        // 验证表单
        if (!validateForm()) {
            return;
        }
        
        try {
            // 获取表单数据
            LocalDate date = datePicker.getValue();
            String type = typeComboBox.getValue();
            double amount = Double.parseDouble(amountField.getText().trim());
            String paymentMethod = paymentMethodComboBox.getValue();
            String notes = notesArea.getText().trim();
            
            // 创建交易记录
            Transaction transaction = new Transaction(date, type, amount, paymentMethod, notes, "", false);
            
            // 应用自动分类
            transaction = CategoryClassifier.autoClassify(transaction);
            
            // 保存交易记录
            dataService.addTransaction(currentUser.getUsername(), transaction);
            
            // 显示成功消息
            showSuccess("交易记录保存成功");
            
            // 清空表单
            clearForm();
            
            // 刷新表格
            loadTransactionData();
            updateTransactionsTable();
            
        } catch (NumberFormatException e) {
            showError("金额格式不正确，请输入有效数字");
        } catch (Exception e) {
            showError("保存失败：" + e.getMessage());
        }
    }

    /**
     * 处理清空按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleClear(ActionEvent event) {
        clearForm();
    }

    /**
     * 处理刷新按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleRefresh(ActionEvent event) {
        // 重新加载数据
        loadTransactionData();
        
        // 更新表格
        updateTransactionsTable();
    }

    /**
     * 清空表单
     */
    private void clearForm() {
        datePicker.setValue(LocalDate.now());
        typeComboBox.setValue(null);
        amountField.clear();
        paymentMethodComboBox.setValue(null);
        notesArea.clear();
        
        // 隐藏消息
        errorText.setVisible(false);
        errorText.setManaged(false);
        successText.setVisible(false);
        successText.setManaged(false);
    }

    /**
     * 验证表单
     * @return 表单是否有效
     */
    private boolean validateForm() {
        // 验证日期
        if (datePicker.getValue() == null) {
            showError("请选择交易日期");
            return false;
        }
        
        // 验证交易类型
        if (typeComboBox.getValue() == null || typeComboBox.getValue().isEmpty()) {
            showError("请选择交易类型");
            return false;
        }
        
        // 验证金额
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            showError("请输入交易金额");
            return false;
        }
        
        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showError("交易金额必须大于0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("金额格式不正确，请输入有效数字");
            return false;
        }
        
        // 验证支付方式
        if (paymentMethodComboBox.getValue() == null || paymentMethodComboBox.getValue().isEmpty()) {
            showError("请选择支付方式");
            return false;
        }
        
        return true;
    }

    /**
     * 显示错误信息
     * @param message 错误信息
     */
    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
        errorText.setManaged(true);
        successText.setVisible(false);
        successText.setManaged(false);
    }

    /**
     * 显示成功信息
     * @param message 成功信息
     */
    private void showSuccess(String message) {
        successText.setText(message);
        successText.setVisible(true);
        successText.setManaged(true);
        errorText.setVisible(false);
        errorText.setManaged(false);
    }

    /**
     * 加载交易数据
     */
    private void loadTransactionData() {
        // 获取用户的所有交易记录
        transactions = dataService.getUserTransactions(currentUser.getUsername());
    }

    /**
     * 更新交易记录表格
     */
    private void updateTransactionsTable() {
        // 应用过滤
        filterTransactions();
    }

    /**
     * 过滤交易记录
     */
    private void filterTransactions() {
        if (transactions == null) {
            return;
        }
        
        String filter = filterComboBox.getValue();
        
        List<Transaction> filteredTransactions;
        if (filter == null || filter.equals("所有类型")) {
            // 不过滤
            filteredTransactions = transactions;
        } else {
            // 按类型过滤
            filteredTransactions = transactions.stream()
                    .filter(t -> filter.equals(t.getType()))
                    .collect(Collectors.toList());
        }
        
        // 按日期降序排序
        filteredTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        
        // 更新表格
        transactionsTable.setItems(FXCollections.observableArrayList(filteredTransactions));
    }

    /**
     * 处理前一页按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handlePrevPage(ActionEvent event) {
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
     * 处理下一页按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleNextPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auto_import.fxml"));
            Parent root = loader.load();
            
            AutoImportController controller = loader.getController();
            controller.initData(currentUser);
            
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
