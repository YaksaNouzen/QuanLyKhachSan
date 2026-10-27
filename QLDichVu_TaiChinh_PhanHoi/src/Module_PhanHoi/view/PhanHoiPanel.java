package Module_PhanHoi.view;

import Module_PhanHoi.controller.PhanHoiController;
import Module_PhanHoi.model.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PhanHoiPanel extends JPanel {
    private final PhanHoiController controller;
    private final JTextField keyword=new JTextField(18);
    private final JComboBox<Object> type=new JComboBox<>();
    private final JComboBox<Object> state=new JComboBox<>();
    private final DefaultTableModel model=PhanHoiUI.tableModel("Mã PH","Mã khách hàng","Loại", "Nội dung", "Tiếp nhận", "Trạng thái");
    private final JTable table=PhanHoiUI.table(model);
    private final JTextArea details=PhanHoiUI.area(3);
    private final JLabel info=new JLabel("Chọn một phản hồi để xem thông tin.");
    private final JLabel status=new JLabel(" ");
    private final List<JButton> actions=new ArrayList<>();
    private List<PhanHoi> rows=new ArrayList<>();
    private boolean busy;

    public PhanHoiPanel() { this(new PhanHoiController()); }
    public PhanHoiPanel(PhanHoiController controller) {
        this.controller=controller;
        setLayout(new BorderLayout(12,12));setBackground(PhanHoiUI.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(18,20,18,20));
        JPanel top=new JPanel();top.setOpaque(false);top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));
        JPanel heading=new JPanel(new BorderLayout(0,5));heading.setOpaque(false);
        JLabel title=new JLabel("QUẢN LÝ PHẢN HỒI KHÁCH HÀNG");
        title.setFont(new Font("Arial",Font.BOLD,25));title.setForeground(PhanHoiUI.BLUE);
        JLabel subtitle=new JLabel("Tiếp nhận đánh giá, khiếu nại và theo dõi lịch sử xử lý");
        subtitle.setFont(new Font("Arial",Font.PLAIN,13));subtitle.setForeground(Color.GRAY);
        heading.add(title,BorderLayout.NORTH);heading.add(subtitle,BorderLayout.SOUTH);
        top.add(heading);top.add(Box.createVerticalStrut(15));
        JPanel information=PhanHoiUI.card("Thông tin phản hồi");
        details.setEditable(false);
        JPanel content=new JPanel(new BorderLayout(5,5));content.setOpaque(false);
        content.add(info,BorderLayout.NORTH);content.add(new JScrollPane(details),BorderLayout.CENTER);
        information.add(content,BorderLayout.CENTER);top.add(information);top.add(Box.createVerticalStrut(12));
        JPanel filters=PhanHoiUI.card("Tìm kiếm và lọc phản hồi");
        type.addItem("Tất cả loại");for(LoaiPhanHoi t:LoaiPhanHoi.values())type.addItem(t);
        state.addItem("Tất cả trạng thái");for(TrangThaiPhanHoi s:TrangThaiPhanHoi.values())state.addItem(s);
        JPanel filterForm=new JPanel(new GridBagLayout());filterForm.setOpaque(false);
        GridBagConstraints c=new GridBagConstraints();c.insets=new Insets(5,6,5,6);c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx=0;c.gridy=0;filterForm.add(new JLabel("Từ khóa:"),c);
        c.gridx=1;c.weightx=1;filterForm.add(keyword,c);
        c.gridx=2;c.weightx=0;filterForm.add(type,c);c.gridx=3;filterForm.add(state,c);
        keyword.setToolTipText("Mã phản hồi, mã khách hàng hoặc nội dung");
        JButton search=action("Tìm kiếm",new Color(41,98,160),this::taiDanhSach);
        JButton clear=action("Bỏ lọc",new Color(108,117,125),()->{keyword.setText("");type.setSelectedIndex(0);state.setSelectedIndex(0);taiDanhSach();});
        JPanel filterButtons=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));filterButtons.setOpaque(false);
        filterButtons.add(search);filterButtons.add(clear);c.gridy=1;c.gridx=0;c.gridwidth=4;filterForm.add(filterButtons,c);
        filters.add(filterForm,BorderLayout.CENTER);top.add(filters);add(top,BorderLayout.NORTH);
        JPanel list=PhanHoiUI.card("Danh sách phản hồi");
        JScrollPane scroll=new JScrollPane(table);scroll.setPreferredSize(new Dimension(700,220));
        int[] widths={60,110,100,230,150,120};for(int i=0;i<widths.length;i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        list.add(scroll,BorderLayout.CENTER);add(list,BorderLayout.CENTER);
        JPanel bottom=new JPanel(new BorderLayout());bottom.setOpaque(false);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));buttons.setOpaque(false);
        buttons.add(action("Tiếp nhận mới",new Color(39,145,87),()->new TiepNhanPhanHoiDialog(owner(),controller,this::taiDanhSach).setVisible(true)));
        buttons.add(action("Cập nhật xử lý",new Color(230,151,37),this::xuLy));
        buttons.add(action("Xem lịch sử",new Color(92,73,145),this::lichSu));
        buttons.add(action("Làm mới",new Color(108,117,125),this::taiDanhSach));
        bottom.add(buttons,BorderLayout.CENTER);bottom.add(status,BorderLayout.SOUTH);add(bottom,BorderLayout.SOUTH);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())showSelected();});
        keyword.addActionListener(e->taiDanhSach());
        taiDanhSach();
    }
    private JButton action(String text,Color color,Runnable action) {
        JButton b=PhanHoiUI.button(text,color);b.addActionListener(e->action.run());actions.add(b);return b;
    }
    private Window owner() { return SwingUtilities.getWindowAncestor(this); }
    private PhanHoi selected() {
        int row=table.getSelectedRow();return row>=0 && row<rows.size()?rows.get(row):null;
    }
    private void showSelected() {
        PhanHoi p=selected();
        if(p==null){info.setText("Chọn một phản hồi để xem thông tin.");details.setText("");return;}
        info.setText("#"+p.getMaPhanHoi()+"  |  Khách: "+p.getMaKhachHang()+"  |  "+p.getLoaiPhanHoi()+"  |  "+p.getTrangThai());
        details.setText(p.getNoiDung());details.setCaretPosition(0);
    }
    private void busy(boolean value) {
        busy=value;for(JButton b:actions)b.setEnabled(!value);
        keyword.setEnabled(!value);type.setEnabled(!value);state.setEnabled(!value);table.setEnabled(!value);
    }
    public void taiDanhSach() {
        if(busy)return;
        String text=keyword.getText();
        LoaiPhanHoi t=type.getSelectedItem() instanceof LoaiPhanHoi v?v:null;
        TrangThaiPhanHoi s=state.getSelectedItem() instanceof TrangThaiPhanHoi v?v:null;
        busy(true);status.setText("Đang tải phản hồi…");
        rows=new ArrayList<>();model.setRowCount(0);showSelected();
        boolean[] loaded={false};
        PhanHoiUI.background(this,()->controller.search(text,t,s),result->{
            rows=result;for(PhanHoi p:result)model.addRow(new Object[]{p.getMaPhanHoi(),p.getMaKhachHang(),p.getLoaiPhanHoi(),
                p.getNoiDung().replace('\n',' '),PhanHoiUI.TIME.format(p.getThoiGianTiepNhan()),p.getTrangThai()});
            loaded[0]=true;status.setText(result.size()+" phản hồi");
        },()->{busy(false);if(!loaded[0])status.setText("Chưa tải được dữ liệu. Kiểm tra cấu hình rồi bấm Làm mới.");});
    }
    private void xuLy() {
        PhanHoi p=selected();if(p==null){PhanHoiUI.error(this,new IllegalArgumentException("Hãy chọn phản hồi cần xử lý."));return;}
        busy(true);
        PhanHoiUI.background(this,()->controller.findById(p.getMaPhanHoi()),fresh->{
            if(fresh.getTrangThai()==TrangThaiPhanHoi.DA_XU_LY){
                PhanHoiUI.error(this,new IllegalArgumentException("Phản hồi đã xử lý chỉ được tra cứu."));return;
            }
            // Open after this worker finishes, so its cleanup cannot unlock a later reload.
            SwingUtilities.invokeLater(()->new XuLyPhanHoiDialog(owner(),controller,fresh,this::taiDanhSach).setVisible(true));
        },()->busy(false));
    }
    private void lichSu() {
        PhanHoi p=selected();if(p==null){PhanHoiUI.error(this,new IllegalArgumentException("Hãy chọn phản hồi để xem lịch sử."));return;}
        busy(true);
        PhanHoiUI.background(this,()->controller.history(p.getMaPhanHoi()),history->
            new LichSuXuLyDialog(owner(),p.getMaPhanHoi(),history).setVisible(true),()->busy(false));
    }
}
