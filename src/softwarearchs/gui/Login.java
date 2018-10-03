package softwarearchs.gui;

import softwarearchs.Main;
import softwarearchs.enums.Role;
import softwarearchs.facade.Facade;
import softwarearchs.user.User;
//import sun.rmi.runtime.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Login extends JFrame{
    private JPanel rootPanel;
    private JButton signInButton;
    private JPasswordField userPassword;
    private JTextField userLogin;

    private Facade facade = Main.facade;
    public Login() {
        Main.frameInit(this, rootPanel, 300, 150);
        setHandler();
        setVisible(true);
    }

    private void setHandler(){
        Login thisFrame = this;
        signInButton.addActionListener(ev -> {
            String login = userLogin.getText();
            String pwd = new String(userPassword.getPassword());
            if(login.isEmpty() || pwd.isEmpty()){
                Main.showErrorMessage("Enter login and password");
                return;
            }
            try{
                try{
                    Main.currentUser = facade.signIn(login, pwd);
                    Main.currentUserClass = Main.currentUser.getClassName();
                    Main.closeFrame(thisFrame);
                    Main.showReceiptForm();
                }
                catch (Exception e){
                    Main.showErrorMessage(e.toString());
                }

            }
            catch (Exception e){
                Main.showErrorMessage(e.getClass() + e.getMessage() + " " + e.getCause());
            }
        });
    }
}
