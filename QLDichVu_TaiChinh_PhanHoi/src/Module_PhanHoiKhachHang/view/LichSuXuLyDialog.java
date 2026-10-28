package Module_PhanHoiKhachHang.view;

import Module_PhanHoiKhachHang.model.LichSuXuLyPhanHoi;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LichSuXuLyDialog extends JDialog {
    public LichSuXuLyDialog(Window owner,long id,List<LichSuXuLyPhanHoi> history) {
        super(owner,"Lịch sử xử lý phản hồi #"+id,ModalityType.APPLICATION_MODAL);
        JPanel panel=PhanHoiUI.card("Các lần tiếp nhận và xử lý (theo thời gian)");
        DefaultTableModel model=PhanHoiUI.tableModel("Thời gian","Trạng thái trước","Trạng thái sau","Người thực hiện");
        JTable table=PhanHoiUI.table(model);
        for(LichSuXuLyPhanHoi h:history) model.addRow(new Object[]{PhanHoiUI.TIME.format(h.getThoiGianGhiNhan()),
            h.getTrangThaiTruoc()==null?"—":h.getTrangThaiTruoc(),h.getTrangThaiSau(),
            h.getNguoiThucHien()==null?"Chưa ghi nhận":h.getNguoiThucHien()});
        JTextArea note=PhanHoiUI.area(6);note.setEditable(false);
        table.getSelectionModel().addListSelectionListener(e->{
            int row=table.getSelectedRow(); if(row>=0) {note.setText(history.get(row).getNoiDungXuLy());note.setCaretPosition(0);}
        });
        JScrollPane scroll=new JScrollPane(table);scroll.setPreferredSize(new Dimension(760,240));
        panel.add(scroll,BorderLayout.CENTER);
        JPanel details=PhanHoiUI.card("Nội dung lần xử lý được chọn");details.add(new JScrollPane(note),BorderLayout.CENTER);
        panel.add(details,BorderLayout.SOUTH);
        JButton close=PhanHoiUI.button("Đóng",new Color(108,117,125));close.addActionListener(e->dispose());
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.RIGHT));buttons.add(close);
        add(panel,BorderLayout.CENTER);add(buttons,BorderLayout.SOUTH);
        if(!history.isEmpty()) table.setRowSelectionInterval(0,0);
        PhanHoiUI.configureDialog(this,850);
    }
}
