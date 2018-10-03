package softwarearchs.gui;

import softwarearchs.Main;
import softwarearchs.enums.Role;
import softwarearchs.exceptions.InvalidUser;
import softwarearchs.facade.Facade;
import softwarearchs.user.Client;
import softwarearchs.user.Master;
import softwarearchs.user.Receiver;
import softwarearchs.user.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Executable;
import java.util.*;

public class UserInfo extends JFrame{
    private JPanel rootPanel;
    private JTable usersTable;
    private JTextField userName;
    private JTextField userSurname;
    private JTextField userPhoneNumber;
    private JTextField userPatronymic;
    private JTextField userEmail;
    private JTextField userLogin;
    private JButton newButton;
    private JButton exitButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JComboBox userRole;
    private JPasswordField userPassword;
    private JPasswordField repeatUserPassword;

    private Facade facade = Main.facade;
    private User currentUser;
    private Role currentUserClass;
    private boolean window;
    private AbstractMap<String, User> users;
    private int clickedRow;

    public UserInfo(boolean window){
        Main.frameInit(this, rootPanel, 800, 350);
        this.currentUser = Main.currentUser;
        this.currentUserClass = Main.currentUserClass;
        this.window = window;

        if(Role.Receiver.equals(currentUserClass)){
            updateButton.setVisible(true);
            newButton.setVisible(true);
            deleteButton.setVisible(true);
        }

        for(Role role : Role.values())
            userRole.addItem(role);

        fillTable();
        setInfo();
        setHandlers();
        setVisible(true);
    }

    private void setHandlers(){
        JFrame currentFrame = this;
        exitButton.addActionListener(ev -> {
            Main.closeFrame(currentFrame);
            if(window)
                Main.showReceiptForm();
        });

        updateButton.addActionListener(ev -> {
            if(userName.getText().isEmpty() || userSurname.getText().isEmpty() ||
                    userPatronymic.getText().isEmpty() || userEmail.getText().isEmpty() ||
                    userEmail.getText().isEmpty() || userPhoneNumber.getText().isEmpty() ||
                    userLogin.getText().isEmpty()) {
                Main.showErrorMessage("Fill all fields to update user");
                return;
            }
            User user;
            try {
                user = facade.getUser(userLogin.getText());
            } catch (Exception e){
                Main.showErrorMessage(e.toString());
                return;
            }
            user.setName(userName.getText());
            user.setSurname(userSurname.getText());
            user.setPatronymic(userPatronymic.getText());
            user.seteMail(userEmail.getText());
            user.setPhoneNumber(userPhoneNumber.getText());
            user.setLogin(userLogin.getText());

            try {
                facade.updateUser(user);
            }catch (InvalidUser e){
                Main.showErrorMessage("User update failed. Cause: " + e.toString());
                return;
            }
            users.replace(user.getLogin(), user);
            replaceRow(user);
            Main.showInformationMessage("User was updated");
        });

        newButton.addActionListener(ev -> {
            if("New".equals(newButton.getText())) {
                newButton.setText("Add user");

                userName.setText("");
                userSurname.setText("");
                userPatronymic.setText("");
                userEmail.setText("");
                userPhoneNumber.setText("");
                userLogin.setText("");
                userRole.setSelectedItem(Role.Client);

                return;
            }
            if("Add user".equals(newButton.getText())){
                addUser();
                newButton.setText("New");
            }
        });

        deleteButton.addActionListener(ev -> {
            String login = userLogin.getText();
            try{
                facade.deleteUser(login);
            } catch (Exception e){
                Main.showErrorMessage("Delete user failure. Case: " + e.toString());
                return;
            }
            removeTableRow();
            Main.showInformationMessage("User with login: " + login + " was deleted");
        });

        usersTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickedRow = usersTable.rowAtPoint(e.getPoint());
                if(clickedRow < 0)
                    return;
                TableModel model = usersTable.getModel();
                User selectedUser = users.get(model.getValueAt(clickedRow, 0));
                selectedUserInfo(selectedUser);
                updateButton.setEnabled(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    private void setInfo(){
        userName.setText(currentUser.getName());
        userSurname.setText(currentUser.getSurname());
        userPatronymic.setText(currentUser.getPatronymic());
        userEmail.setText(currentUser.geteMail());
        userPhoneNumber.setText(currentUser.getPhoneNumber());
        userLogin.setText(currentUser.getLogin());
        userRole.setSelectedItem(currentUserClass);
    }

    private void addUser(){
        if(userName.getText().isEmpty() || userSurname.getText().isEmpty() ||
                userEmail.getText().isEmpty() || userPhoneNumber.getText().isEmpty() ||
                userLogin.getText().isEmpty() || userPassword.getPassword().length == 0 ||
                repeatUserPassword.getPassword().length == 0){
            Main.showErrorMessage("Fill in all fields");
            return;
        }

        if(!new String(userPassword.getPassword()).equals(new String(repeatUserPassword.getPassword()))){
            Main.showErrorMessage("Different passwords");
            return;
        }

        User user;
        try{
            user = Main.facade.addUser(userRole.getSelectedItem().toString(), userName.getText(),
                    userSurname.getText(), userPatronymic.getText(), userLogin.getText(),
                    userPhoneNumber.getText(), userEmail.getText(),
                    new String(userPassword.getPassword()), new String(repeatUserPassword.getPassword()));
        } catch(Exception e){
            Main.showErrorMessage(e.toString());
            return;
        }

        users.put(user.getLogin(), user);
        addTableRow(user);
        Main.showInformationMessage("User was added");
    }

    private void selectedUserInfo(User selectedUser){
        userName.setText(selectedUser.getName());
        userSurname.setText(selectedUser.getSurname());
        userPatronymic.setText(selectedUser.getPatronymic());
        userEmail.setText(selectedUser.geteMail());
        userPhoneNumber.setText(selectedUser.getPhoneNumber());
        userLogin.setText(selectedUser.getLogin());
        userRole.setSelectedItem(selectedUser.getClass().getSimpleName());
        userPassword.setText("");
        repeatUserPassword.setText("");
    }

    private void fillTable(){
        try {
            users = facade.getAllUsers();
        }catch(Exception e){
            Main.showErrorMessage(e.toString());
            return;
        }
        DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
        model.setColumnCount(3);
        Object[] cols = new Object[]{"Login", "User", "Role"};
        model.setColumnIdentifiers(cols);
        if(Role.Receiver.equals(currentUserClass))
            for(Map.Entry<String, User> user : users.entrySet())
                model.addRow(new Object[]{user.getKey(), user.getValue().getFIO(),
                        user.getValue().getClass().getSimpleName()});
        else
            model.addRow(new Object[]{currentUser.getLogin(), currentUser.getFIO(),
                    currentUserClass.toString()});

        usersTable.setModel(model);
    }

    private void addTableRow(User user){
        DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
        model.addRow(new Object[]{user.getLogin(), user.getFIO(), user.getClass().getSimpleName()});
        usersTable.setModel(model);
    }

    private void removeTableRow(){
        DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
        model.removeRow(clickedRow);
        usersTable.setModel(model);
    }

    private void replaceRow(User user){
        TableModel model = usersTable.getModel();
        model.setValueAt(user.getLogin(), clickedRow, 0);
        model.setValueAt(user.getFIO(), clickedRow, 1);
        model.setValueAt(user.getClass().getSimpleName(), clickedRow, 2);
        usersTable.setModel(model);
    }
}
