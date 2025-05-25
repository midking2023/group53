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
 * 忘记密码界面控制器
 * 处理用户找回密码功能
 */
public class ForgotPasswordController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button resetButton;

    @FXML
    private Button backToLoginButton;

    @FXML
    private Text errorText;

    @FXML
    private Text successText;

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
     * 处理重置密码按钮点击事件
     * @param event 事件对象
     */
    @FXML
    public void handleReset(ActionEvent event) {
        // 隐藏之前的消息
        errorText.setVisible(false);
        errorText.setManaged(false);
        successText.setVisible(false);
        successText.setManaged(false);
        
        // 获取输入值
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // 验证输入
        if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("所有字段都必须填写");
            return;
        }
        
        // 验证密码一致性
        if (!newPassword.equals(confirmPassword)) {
            showError("两次输入的密码不一致");
            return;
        }
        
        // 查找用户
        User user = dataService.getUser(username);
        if (user == null) {
            showError("用户不存在");
            return;
        }
        
        // 验证邮箱
        if (!email.equals(user.getEmail())) {
            showError("邮箱与注册信息不匹配");
            return;
        }
        
        // 更新密码
        user.setPassword(newPassword);
        dataService.updateUser(user);
        
        // 显示成功消息
        showSuccess("密码重置成功，请返回登录");
        
        // 禁用重置按钮，防止重复提交
        resetButton.setDisable(true);
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

    /**
     * 显示成功信息
     * @param message 成功信息
     */
    private void showSuccess(String message) {
        successText.setText(message);
        successText.setVisible(true);
        successText.setManaged(true);
    }
} 