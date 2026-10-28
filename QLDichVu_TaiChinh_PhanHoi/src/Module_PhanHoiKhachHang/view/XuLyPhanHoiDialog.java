package Module_PhanHoiKhachHang.view;

import Module_PhanHoiKhachHang.controller.PhanHoiController;
import Module_PhanHoiKhachHang.model.*;
import java.awt.*;
import javax.swing.*;

public class XuLyPhanHoiDialog extends JDialog {
    public XuLyPhanHoiDialog(Window owner, PhanHoiController controller, PhanHoi current, Runnable saved) {
        super(owner,"Xử lý phản hồi #"+current.getMaPhanHoi(),ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(12,12));
        JPanel form=new JPanel(new GridBagLayout()); form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(15,15,5,15));
        JComboBox<TrangThaiPhanHoi> status=new JComboBox<>(new TrangThaiPhanHoi[]{
            TrangThaiPhanHoi.DANG_XU_LY,TrangThaiPhanHoi.DA_XU_LY});
        JTextArea original=PhanHoiUI.area(3); original.setText(current.getNoiDung()); original.setEditable(false);
        JTextArea note=PhanHoiUI.area(5); JTextField actor=new JTextField(25);
        PhanHoiUI.row(form,"Khách hàng:",new JLabel(current.getMaKhachHang()),0);
        PhanHoiUI.row(form,"Nội dung phản hồi:",new JScrollPane(original),1);
        PhanHoiUI.row(form,"Trạng thái hiện tại:",new JLabel(current.getTrangThai().toString()),2);
        PhanHoiUI.row(form,"Trạng thái sau xử lý:",status,3);
        PhanHoiUI.row(form,"Nội dung xử lý:",new JScrollPane(note),4);
        PhanHoiUI.row(form,"Người thực hiện (tùy chọn):",actor,5);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        JButton save=PhanHoiUI.button("Lưu xử lý",new Color(230,151,37));
        JButton cancel=PhanHoiUI.button("Đóng",new Color(108,117,125));
        buttons.add(save);buttons.add(cancel);cancel.addActionListener(e->dispose());
        save.addActionListener(e->{
            String text=note.getText(), user=actor.getText();
            TrangThaiPhanHoi target=(TrangThaiPhanHoi)status.getSelectedItem();
            save.setEnabled(false);cancel.setEnabled(false);setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            PhanHoiUI.background(this,()->{
                controller.capNhatXuLy(current.getMaPhanHoi(),current.getPhienBan(),target,text,user);return true;
            },ok->{JOptionPane.showMessageDialog(this,"Đã lưu tiến độ và lịch sử xử lý.");dispose();saved.run();},
                ()->{save.setEnabled(true);cancel.setEnabled(true);setDefaultCloseOperation(DISPOSE_ON_CLOSE);});
        });
        add(form,BorderLayout.CENTER);add(buttons,BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);PhanHoiUI.configureDialog(this,700);
    }
}
