package Module_PhanHoi.view;

import Module_PhanHoi.controller.PhanHoiController;
import Module_PhanHoi.model.LoaiPhanHoi;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import javax.swing.*;

public class TiepNhanPhanHoiDialog extends JDialog {
    public TiepNhanPhanHoiDialog(Window owner, PhanHoiController controller, Runnable saved) {
        super(owner,"Tiếp nhận phản hồi",ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(12,12));
        JPanel form=new JPanel(new GridBagLayout()); form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(15,15,5,15));
        JTextField customer=new JTextField(25), actor=new JTextField(25);
        JTextField time=new JTextField(PhanHoiUI.TIME.format(LocalDateTime.now().withNano(0)));
        JComboBox<LoaiPhanHoi> type=new JComboBox<>(LoaiPhanHoi.values());
        JTextArea content=PhanHoiUI.area(6);
        PhanHoiUI.row(form,"Mã khách hàng:",customer,0);
        PhanHoiUI.row(form,"Loại phản hồi:",type,1);
        PhanHoiUI.row(form,"Tiếp nhận (dd/MM/yyyy HH:mm:ss):",time,2);
        PhanHoiUI.row(form,"Nội dung:",new JScrollPane(content),3);
        PhanHoiUI.row(form,"Người tiếp nhận (tùy chọn):",actor,4);
        JLabel hint=new JLabel("Mã khách hàng được nhập trực tiếp, chưa đối chiếu danh sách khách.");
        hint.setForeground(Color.GRAY); PhanHoiUI.row(form,"",hint,5);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        JButton save=PhanHoiUI.button("Lưu phản hồi",new Color(39,145,87));
        JButton cancel=PhanHoiUI.button("Đóng",new Color(108,117,125));
        buttons.add(save); buttons.add(cancel); cancel.addActionListener(e->dispose());
        save.addActionListener(e->{
            LocalDateTime date;
            try { date=LocalDateTime.parse(time.getText().trim(),PhanHoiUI.TIME); }
            catch(DateTimeParseException ex) {
                PhanHoiUI.error(this,new IllegalArgumentException("Thời gian phải có dạng dd/MM/yyyy HH:mm:ss và là ngày hợp lệ.")); return;
            }
            String customerText=customer.getText(), contentText=content.getText(), actorText=actor.getText();
            LoaiPhanHoi chosen=(LoaiPhanHoi)type.getSelectedItem();
            save.setEnabled(false); cancel.setEnabled(false); setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            PhanHoiUI.background(this,()->controller.tiepNhan(customerText,chosen,contentText,date,actorText),id->{
                JOptionPane.showMessageDialog(this,"Đã tiếp nhận phản hồi #"+id);
                dispose(); saved.run();
            },()->{save.setEnabled(true);cancel.setEnabled(true);setDefaultCloseOperation(DISPOSE_ON_CLOSE);});
        });
        add(form,BorderLayout.CENTER); add(buttons,BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save); PhanHoiUI.configureDialog(this,700);
    }
}
