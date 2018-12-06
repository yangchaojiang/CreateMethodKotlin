package com.aac.jiang.view;

import com.aac.jiang.modle.MethodBeam;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldMedthod;
    private JTextField textFieldBean;
    private JTextField textFieldUri;
    private JComboBox comboBoxRxType;
    private JTextField textField1Key;
    private JComboBox comboBox1;
    private JComboBox dataTypeChe;
    private JLabel dataTypelabel;

    private CallBack callBack;
    public EditDialog(CallBack callBack) {
        this.callBack=callBack;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        MethodBeam methodBeam=new MethodBeam();
        if (textFieldBean.getText().isEmpty()){
            Messages.showInfoMessage("方法名称好像啥也没填！","提示");
            return;
        }
        if (textFieldUri.getText().isEmpty()){
            Messages.showInfoMessage("访问链接好像啥也没填！","提示");
            return;
        }
        if (textFieldBean.getText().isEmpty()){
            Messages.showInfoMessage("类的类型好像啥也没填！","提示");
            return;
        }
        if (textField1Key.getText().isEmpty()){
            methodBeam.setKeyName("-1");
        }else {
            methodBeam.setKeyName(textField1Key.getText().trim());
        }
        methodBeam.setBeanSName(textFieldBean.getText().trim());
        methodBeam.setUriName(textFieldUri.getText().trim());
        methodBeam.setRxType(comboBoxRxType.getSelectedIndex());
        methodBeam.setMethodName(textFieldMedthod.getText().trim());
        methodBeam.setHttpType(comboBox1.getSelectedIndex());
        methodBeam.setConverterType(dataTypeChe.getSelectedIndex());
        callBack.callBack(methodBeam);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
  public  interface  CallBack{
        void callBack(MethodBeam beam);
  }
    public static void main(String[] args) {
        EditDialog dialog = new EditDialog(new CallBack() {
            @Override
            public void callBack(MethodBeam beam) {

            }
        });
        dialog.pack();
        dialog.setSize(400, 300);
        Toolkit kit = Toolkit.getDefaultToolkit();    // 定义工具包
        Dimension screenSize = kit.getScreenSize();   // 获取屏幕的尺寸
        int screenWidth = screenSize.width / 2;         // 获取屏幕的宽
        int screenHeight = screenSize.height / 2;       // 获取屏幕的高
        int height = 500;
        int width = 500;
        dialog.setLocation(screenWidth - width / 2, screenHeight - height / 2);
        dialog.setVisible(true);
        System.exit(0);
    }
}
