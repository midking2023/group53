package com.finance.manager.controller;

import com.finance.manager.model.User;
import com.finance.manager.utils.DataService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * 个人资料控制器
 * 处理用户个人资料设置的功能
 */
public class ProfileController {

    @FXML
    private ImageView avatarImageView;

    @FXML
    private TextField nicknameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private ComboBox<String> currencyComboBox;

    @FXML
    private ComboBox<String> regionComboBox;

    @FXML
    private CheckBox notificationCheckBox;

    @FXML
    private CheckBox alipayCheckBox;

    @FXML
    private CheckBox wechatCheckBox;

    @FXML
    private CheckBox bankCardCheckBox;

    @FXML
    private CheckBox creditCardCheckBox;

    @FXML
    private CheckBox cashCheckBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button backButton;

    @FXML
    private Text statusText;

    private User currentUser;
    private DataService dataService;
    private String avatarPath;

    /**
     * 初始化方法
     * JavaFX在加载FXML后自动调用
     */
    @FXML
    public void initialize() {
        // 获取数据服务实例
        dataService = DataService.getInstance();
        
        // 初始化状态文本
        statusText.setText("");
        
        // 设置默认值
        languageComboBox.setValue("中文");
        currencyComboBox.setValue("RMB (¥)");
        regionComboBox.setValue("中国");
    }

    /**
     * 初始化用户数据
     * @param user 当前用户
     */
    public void initData(User user) {
        this.currentUser = user;
        
        // 填充用户数据
        nicknameField.setText(user.getNickname());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        
        // 填充偏好设置
        languageComboBox.setValue(user.getLanguage());
        
        // 设置货币选项
        String currency = user.getCurrency();
        if (currency.equals("¥")) {
            currencyComboBox.setValue("RMB (¥)");
        } else if (currency.equals("$")) {
            currencyComboBox.setValue("USD ($)");
        } else if (currency.equals("€")) {
            currencyComboBox.setValue("EUR (€)");
        }
        
        // 设置地区选项
        regionComboBox.setValue(user.getRegion());
        
        // 设置通知偏好
        notificationCheckBox.setSelected(user.isNotificationEnabled());
        
        // 设置支付方式
        String paymentMethod = user.getPaymentMethod();
        if (paymentMethod != null) {
            alipayCheckBox.setSelected(paymentMethod.contains("支付宝"));
            wechatCheckBox.setSelected(paymentMethod.contains("微信"));
            bankCardCheckBox.setSelected(paymentMethod.contains("银行卡"));
            creditCardCheckBox.setSelected(paymentMethod.contains("信用卡"));
            cashCheckBox.setSelected(paymentMethod.contains("现金"));
        }
        
        // 加载头像
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            try {
                File avatarFile = new File(user.getAvatar());
                if (avatarFile.exists()) {
                    Image avatar = new Image(avatarFile.toURI().toString());
                    avatarImageView.setImage(avatar);
                    avatarPath = user.getAvatar();
                }
            } catch (Exception e) {
                System.err.println("加载头像失败: " + e.getMessage());
            }
        }
    }

    /**
     * 处理上传头像按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleUploadAvatar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择头像图片");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                Image avatar = new Image(selectedFile.toURI().toString());
                avatarImageView.setImage(avatar);
                avatarPath = selectedFile.getAbsolutePath();
                statusText.setText("头像已更新，点击保存按钮保存更改");
            } catch (Exception e) {
                statusText.setText("加载图片失败: " + e.getMessage());
            }
        }
    }

    /**
     * 处理保存按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleSave(ActionEvent event) {
        // 更新用户数据
        currentUser.setNickname(nicknameField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());
        currentUser.setPhone(phoneField.getText().trim());
        
        // 更新语言设置
        currentUser.setLanguage(languageComboBox.getValue());
        
        // 更新货币设置
        String currencySelection = currencyComboBox.getValue();
        if (currencySelection.startsWith("RMB")) {
            currentUser.setCurrency("¥");
        } else if (currencySelection.startsWith("USD")) {
            currentUser.setCurrency("$");
        } else if (currencySelection.startsWith("EUR")) {
            currentUser.setCurrency("€");
        }
        
        // 更新地区设置
        currentUser.setRegion(regionComboBox.getValue());
        
        // 更新通知设置
        currentUser.setNotificationEnabled(notificationCheckBox.isSelected());
        
        // 更新支付方式
        StringBuilder paymentMethod = new StringBuilder();
        if (alipayCheckBox.isSelected()) paymentMethod.append("支付宝,");
        if (wechatCheckBox.isSelected()) paymentMethod.append("微信,");
        if (bankCardCheckBox.isSelected()) paymentMethod.append("银行卡,");
        if (creditCardCheckBox.isSelected()) paymentMethod.append("信用卡,");
        if (cashCheckBox.isSelected()) paymentMethod.append("现金,");
        
        // 移除最后一个逗号
        if (paymentMethod.length() > 0) {
            paymentMethod.deleteCharAt(paymentMethod.length() - 1);
        }
        
        currentUser.setPaymentMethod(paymentMethod.toString());
        
        // 更新头像
        if (avatarPath != null) {
            currentUser.setAvatar(avatarPath);
        }
        
        // 保存到数据服务
        dataService.updateUser(currentUser);
        
        // 显示成功消息
        statusText.setText("个人资料已成功保存");
    }

    /**
     * 处理重置按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleReset(ActionEvent event) {
        // 重新加载用户数据
        initData(currentUser);
        statusText.setText("已重置为原始设置");
    }

    /**
     * 处理返回按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleBack(ActionEvent event) {
        try {
            // 返回主界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            MainController controller = loader.getController();
            controller.initData(currentUser);
            
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            statusText.setText("返回主界面失败: " + e.getMessage());
        }
    }
} 
