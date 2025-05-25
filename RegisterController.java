package com.finance.manager.controller;

import com.finance.manager.model.User;
import com.finance.manager.utils.DataService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 注册界面控制器
 * 处理用户注册功能
 */
public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button backToLoginButton;

    @FXML
    private Text errorText;

    private DataService dataService;

    /**
     * 初始化方法
     * JavaFX在加载FXML后自动调用
     */
    @FXML
    public void initialize() {
        // 获取数据服务实例
        dataService = DataService.getInstance();
    }

    /**
     * 处理注册按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleRegister(ActionEvent event) {
        // 获取输入值
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // 验证输入
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("所有字段都必须填写");
            return;
        }
        
        // 验证密码一致性
        if (!password.equals(confirmPassword)) {
            showError("两次输入的密码不一致");
            return;
        }
        
        // 验证用户名是否已存在
        if (dataService.getUser(username) != null) {
            showError("用户名已存在");
            return;
        }
        
        // 创建用户对象
        User user = new User(username, password, email, phone);
        
        // 添加用户
        boolean success = dataService.addUser(user);
        if (success) {
            try {
                // 注册成功，返回登录界面
                handleBackToLogin(event);
            } catch (Exception e) {
                e.printStackTrace();
                showError("注册成功，但返回登录界面失败");
            }
        } else {
            showError("注册失败，请稍后再试");
        }
    }

    /**
     * 处理返回登录按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleBackToLogin(ActionEvent event) {
        try {
            // 加载登录界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("加载登录界面失败");
        }
    }

    /**
     * 显示错误信息
     * @param message 错误信息
     */
    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
        errorText.setManaged(true);
    }
} 