package Module_Phong.view;

import common.ui.HotelTheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import Module_Phong.controller.PhongController;
import Module_Phong.model.LoaiPhong;
import Module_Phong.model.Phong;
import Module_Phong.model.Tang;
import Module_Phong.model.TrangThaiPhong;
import static Module_Phong.model.TrangThaiPhong.BAO_TRI;
import static Module_Phong.model.TrangThaiPhong.DA_DAT;
import static Module_Phong.model.TrangThaiPhong.DANG_DON;
import static Module_Phong.model.TrangThaiPhong.DANG_SU_DUNG;
import static Module_Phong.model.TrangThaiPhong.TRONG;

/**
 * Giao dien Quan ly phong hien dai, truc quan theo chuan Java Nang Cao.
 */
public class PhongPanel extends JPanel {
    private final PhongController controller;
    private final JTabbedPane subTabs = new JTabbedPane();

    // Cac panel con
    private final JPanel pnlSoDoWrap = new JPanel(new BorderLayout(0, 10));
    private final JPanel pnlSoDoGrid = new ViewportWidthPanel();
    private final JPanel pnlNutTangSoDo = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
    private final JComboBox<Object> cboSoDoTrangThai = new JComboBox<>();
    private final JTextField txtSoDoTimKiem = new JTextField(12);
    private final JLabel lblSoDoKetQua = new JLabel();
    private final List<JToggleButton> nutTangSoDo = new ArrayList<>();
    private JScrollPane scrollSoDo;
    private String maTangSoDo = "";
    private JPanel huongDanPhongTrong;
    private final JPanel pnlDanhSach = new JPanel(new BorderLayout(0, 12));
    private final JPanel pnlLoaiPhong = new JPanel(new BorderLayout(0, 12));
    private final JPanel pnlTang = new JPanel(new BorderLayout(0, 12));

    // Models & Tables
    private final DefaultTableModel roomModel = taoModel(
            "Mã phòng", "Số phòng", "Loại phòng", "Tầng", "Giá niêm yết/đêm", "Trạng thái", "Tình trạng");
    private final DefaultTableModel typeModel = taoModel(
            "Mã loại", "Tên loại phòng", "Sức chứa tối đa", "Giá niêm yết/ngày");
    private final DefaultTableModel floorModel = taoModel(
            "Mã tầng", "Tên tầng", "Số phòng hiện có");

    private final JTable tblPhong = new JTable(roomModel);
    private JButton btnTrangThaiPhong;
    private JButton btnXoaPhong;
    private final JTable tblLoai = new JTable(typeModel);
    private final JTable tblTang = new JTable(floorModel);

    // Bo loc chung
    private final JComboBox<Choice> cboLocTang = new JComboBox<>();
    private final JComboBox<Object> cboLocTrangThai = new JComboBox<>();
    private final JTextField txtTimKiem = new JTextField(14);

    // KPI Cards
    private final JLabel lblKpiTongPhong = new JLabel("0", SwingConstants.LEFT);
    private final JLabel lblKpiSanSang = new JLabel("0", SwingConstants.LEFT);
    private final JLabel lblKpiDangDung = new JLabel("0", SwingConstants.LEFT);
    private final JLabel lblKpiCanXuLy = new JLabel("0", SwingConstants.LEFT);

    private final List<JButton> actionButtons = new ArrayList<>();
    private boolean loading;

    public PhongPanel(PhongController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(0, 14));
        setBackground(HotelTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Top Dashboard
        add(taoTopDashboard(), BorderLayout.NORTH);

        // Sub Tabs
        taoTabSoDo();
        taoTabDanhSach();
        taoTabLoai();
        taoTabTang();

        subTabs.setFont(HotelTheme.FONT_BOLD);
        subTabs.setBackground(HotelTheme.BACKGROUND);
        subTabs.setOpaque(false);
        subTabs.addTab("Sơ đồ phòng trực quan", pnlSoDoWrap);
        subTabs.addTab("Danh sách phòng", pnlDanhSach);
        subTabs.addTab("Hạng loại phòng", pnlLoaiPhong);
        subTabs.addTab("Quản lý tầng", pnlTang);
        add(subTabs, BorderLayout.CENTER);

        // Lang nghe controller
        controller.onChange(this::taiLaiGiaoDien);
        controller.onError(message -> JOptionPane.showMessageDialog(
                this, message, "Thông báo kiểm tra", JOptionPane.WARNING_MESSAGE));
        controller.onBusy(value -> {
            actionButtons.forEach(button -> button.setEnabled(!value));
            if (!value) capNhatNutPhong();
            setCursor(Cursor.getPredefinedCursor(value ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
        });

        controller.refresh();
    }

    public void taiLai() {
        controller.refresh();
    }

    /**
     * Dashboard tren cung gom tieu de va 4 KPI cards.
     */
    private JPanel taoTopDashboard() {
        JPanel container = new JPanel(new BorderLayout(0, 12));
        container.setOpaque(false);

        // Row 1: Tieu de va mo ta ngan
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel("Quản Lý Phòng Nghỉ");
        title.setFont(HotelTheme.FONT_HEADER);
        title.setForeground(HotelTheme.TITLE_BLUE);

        JLabel sub = new JLabel("Theo dõi tình trạng phòng thực tế, quản lý phân hạng và cơ cấu phòng");
        sub.setFont(HotelTheme.FONT_SMALL);
        sub.setForeground(HotelTheme.TEXT_SECONDARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(sub);
        titleBar.add(textPanel, BorderLayout.WEST);

        JButton btnTaiLai = nut("Tải lại dữ liệu", HotelTheme.ButtonType.SECONDARY, controller::refresh);
        titleBar.add(btnTaiLai, BorderLayout.EAST);
        container.add(titleBar, BorderLayout.NORTH);

        // Row 2: 4 The thong ke nhanh
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setOpaque(false);

        kpiGrid.add(taoTheKpi("Tổng số phòng", lblKpiTongPhong, "Tất cả phòng trong hệ thống", HotelTheme.PRIMARY_BLUE));
        kpiGrid.add(taoTheKpi("Phòng sẵn sàng", lblKpiSanSang, "Trống và sẵn sàng đón khách", HotelTheme.SUCCESS_TEXT));
        kpiGrid.add(taoTheKpi("Đang có khách / Đã đặt", lblKpiDangDung, "Phòng đang lưu trú hoặc giữ chỗ", HotelTheme.PURPLE_TEXT));
        kpiGrid.add(taoTheKpi("Đang dọn / Bảo trì", lblKpiCanXuLy, "Cần dọn dẹp hoặc sửa chữa", HotelTheme.WARNING_TEXT));

        container.add(kpiGrid, BorderLayout.SOUTH);
        return container;
    }

    private JPanel taoTheKpi(String tieuDe, JLabel lblGiaTri, String moTa, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new HotelTheme.RoundedBorder(HotelTheme.BORDER, 1, 8),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel title = new JLabel(tieuDe);
        title.setFont(HotelTheme.FONT_SMALL);
        title.setForeground(HotelTheme.TEXT_SECONDARY);

        lblGiaTri.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGiaTri.setForeground(accentColor);

        JLabel desc = new JLabel(moTa);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        desc.setForeground(HotelTheme.TEXT_MUTED);

        card.add(title, BorderLayout.NORTH);
        card.add(lblGiaTri, BorderLayout.CENTER);
        card.add(desc, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Tab Sơ đồ phòng trực quan.
     */
    private void taoTabSoDo() {
        pnlSoDoWrap.setOpaque(false);
        pnlSoDoWrap.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        pnlNutTangSoDo.setOpaque(false);
        pnlNutTangSoDo.setAlignmentX(LEFT_ALIGNMENT);
        controls.add(pnlNutTangSoDo);

        JPanel boLocSoDo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        boLocSoDo.setOpaque(false);
        boLocSoDo.add(new JLabel("Trạng thái:"));
        cboSoDoTrangThai.addItem("Tất cả trạng thái");
        for (TrangThaiPhong state : TrangThaiPhong.values()) cboSoDoTrangThai.addItem(state);
        cboSoDoTrangThai.setPreferredSize(new Dimension(160, 30));
        cboSoDoTrangThai.addActionListener(event -> capNhatSoDo());
        boLocSoDo.add(cboSoDoTrangThai);
        boLocSoDo.add(new JLabel("Tìm phòng:"));
        txtSoDoTimKiem.setPreferredSize(new Dimension(135, 30));
        txtSoDoTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { capNhatSoDo(); }
            public void removeUpdate(DocumentEvent e) { capNhatSoDo(); }
            public void changedUpdate(DocumentEvent e) { capNhatSoDo(); }
        });
        boLocSoDo.add(txtSoDoTimKiem);
        lblSoDoKetQua.setForeground(HotelTheme.TEXT_SECONDARY);
        boLocSoDo.add(lblSoDoKetQua);
        boLocSoDo.setAlignmentX(LEFT_ALIGNMENT);
        controls.add(boLocSoDo);

        // Thanh chu thich mau sac (Legend)
        JPanel legendBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        legendBar.setOpaque(false);
        legendBar.add(taoLegendPill("Sẵn sàng", HotelTheme.SUCCESS_BG, HotelTheme.SUCCESS_TEXT));
        legendBar.add(taoLegendPill("Đang có khách", HotelTheme.INFO_BG, HotelTheme.INFO_TEXT));
        legendBar.add(taoLegendPill("Đã đặt trước", HotelTheme.PURPLE_BG, HotelTheme.PURPLE_TEXT));
        legendBar.add(taoLegendPill("Đang dọn dẹp", HotelTheme.WARNING_BG, HotelTheme.WARNING_TEXT));
        legendBar.add(taoLegendPill("Đang bảo trì", HotelTheme.DANGER_BG, HotelTheme.DANGER_TEXT));
        legendBar.add(taoLegendPill("Ngừng sử dụng", HotelTheme.GRAY_BG, HotelTheme.GRAY_TEXT));

        legendBar.setAlignmentX(LEFT_ALIGNMENT);
        controls.add(legendBar);
        pnlSoDoWrap.add(taoThe(controls), BorderLayout.NORTH);

        pnlSoDoGrid.setOpaque(false);
        pnlSoDoGrid.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 6));
        pnlSoDoGrid.setLayout(new BoxLayout(pnlSoDoGrid, BoxLayout.Y_AXIS));

        scrollSoDo = new JScrollPane(pnlSoDoGrid);
        scrollSoDo.setBorder(BorderFactory.createEmptyBorder());
        scrollSoDo.getViewport().setBackground(HotelTheme.BACKGROUND);
        scrollSoDo.getVerticalScrollBar().setUnitIncrement(16);
        scrollSoDo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollSoDo.getViewport().addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent event) { capNhatSoDo(); }
        });

        pnlSoDoWrap.add(taoThe(scrollSoDo), BorderLayout.CENTER);
    }

    private JLabel taoLegendPill(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel("● " + text);
        lbl.setFont(HotelTheme.FONT_BADGE);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return lbl;
    }

    /**
     * Tab Danh sách phòng dạng bảng và thanh công cụ.
     */
    private void taoTabDanhSach() {
        pnlDanhSach.setOpaque(false);
        pnlDanhSach.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Bo loc va tim kiem nam trong the rieng.
        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        leftFilters.setOpaque(false);

        leftFilters.add(new JLabel("Tìm số phòng:"));
        txtTimKiem.setPreferredSize(new Dimension(130, 32));
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { locDanhSach(); }
            public void removeUpdate(DocumentEvent e) { locDanhSach(); }
            public void changedUpdate(DocumentEvent e) { locDanhSach(); }
        });
        leftFilters.add(txtTimKiem);

        leftFilters.add(new JLabel("Tầng:"));
        cboLocTang.setPreferredSize(new Dimension(150, 32));
        leftFilters.add(cboLocTang);

        leftFilters.add(new JLabel("Trạng thái:"));
        cboLocTrangThai.setPreferredSize(new Dimension(170, 32));
        leftFilters.add(cboLocTrangThai);

        // Nut hanh dong
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        rightActions.setOpaque(false);

        JButton btnThem = nut("+ Thêm phòng", HotelTheme.ButtonType.SUCCESS, () -> suaPhong(null));
        JButton btnSua = nut("Cập nhật", HotelTheme.ButtonType.WARNING, this::capNhatPhong);
        btnTrangThaiPhong = nut("Ngừng sử dụng", HotelTheme.ButtonType.DANGER,
                this::doiTrangThaiPhong);
        btnXoaPhong = nut("Xóa phòng chưa dùng", HotelTheme.ButtonType.DANGER,
                this::xoaPhongChuaDung);

        rightActions.add(btnThem);
        rightActions.add(btnSua);
        rightActions.add(btnTrangThaiPhong);
        rightActions.add(btnXoaPhong);

        pnlDanhSach.add(taoThe(leftFilters), BorderLayout.NORTH);

        cauHinhBang(tblPhong);
        tblPhong.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) capNhatNutPhong();
        });
        capNhatNutPhong();
        JScrollPane roomScroll = new JScrollPane(tblPhong);
        roomScroll.setBorder(BorderFactory.createEmptyBorder());
        roomScroll.getViewport().setBackground(Color.WHITE);
        pnlDanhSach.add(taoThe(roomScroll), BorderLayout.CENTER);
        pnlDanhSach.add(taoThe(rightActions), BorderLayout.SOUTH);

        cboLocTang.addActionListener(event -> locPhongTheoBoLoc());
        cboLocTrangThai.addActionListener(event -> locPhongTheoBoLoc());
    }

    /**
     * Tab Hạng loại phòng.
     */
    private void taoTabLoai() {
        pnlLoaiPhong.setOpaque(false);
        pnlLoaiPhong.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel info = new JLabel("Danh sách các hạng phòng, sức chứa và đơn giá niêm yết theo ngày");
        info.setFont(HotelTheme.FONT_SMALL);
        info.setForeground(HotelTheme.TEXT_SECONDARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actions.setOpaque(false);
        actions.add(nut("+ Thêm hạng phòng", HotelTheme.ButtonType.SUCCESS, () -> suaLoai(null)));
        actions.add(nut("Cập nhật", HotelTheme.ButtonType.WARNING, this::capNhatLoai));
        actions.add(nut("Xóa hạng phòng", HotelTheme.ButtonType.DANGER, this::xoaLoai));

        pnlLoaiPhong.add(taoThe(info), BorderLayout.NORTH);

        cauHinhBang(tblLoai);
        JScrollPane typeScroll = new JScrollPane(tblLoai);
        typeScroll.setBorder(BorderFactory.createEmptyBorder());
        typeScroll.getViewport().setBackground(Color.WHITE);
        pnlLoaiPhong.add(taoThe(typeScroll), BorderLayout.CENTER);
        pnlLoaiPhong.add(taoThe(actions), BorderLayout.SOUTH);
    }

    /**
     * Tab Quản lý tầng.
     */
    private void taoTabTang() {
        pnlTang.setOpaque(false);
        pnlTang.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel info = new JLabel("Danh mục các tầng và tổng số phòng bố trí trên từng tầng");
        info.setFont(HotelTheme.FONT_SMALL);
        info.setForeground(HotelTheme.TEXT_SECONDARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actions.setOpaque(false);
        actions.add(nut("+ Thêm tầng mới", HotelTheme.ButtonType.SUCCESS, () -> suaTang(null)));
        actions.add(nut("Cập nhật", HotelTheme.ButtonType.WARNING, this::capNhatTang));
        actions.add(nut("Xóa tầng", HotelTheme.ButtonType.DANGER, this::xoaTang));

        pnlTang.add(taoThe(info), BorderLayout.NORTH);

        cauHinhBang(tblTang);
        JScrollPane floorScroll = new JScrollPane(tblTang);
        floorScroll.setBorder(BorderFactory.createEmptyBorder());
        floorScroll.getViewport().setBackground(Color.WHITE);
        pnlTang.add(taoThe(floorScroll), BorderLayout.CENTER);
        pnlTang.add(taoThe(actions), BorderLayout.SOUTH);
    }

    private JPanel taoThe(JComponent noiDung) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HotelTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        card.add(noiDung, BorderLayout.CENTER);
        return card;
    }

    private void taiLaiGiaoDien() {
        loading = true;
        String tangCu = maDangChon(cboLocTang);
        cboLocTang.removeAllItems();
        cboLocTang.addItem(new Choice("", "Tất cả tầng"));
        for (Tang tang : controller.floors()) {
            cboLocTang.addItem(new Choice(tang.maTang(), tang.tenTang()));
        }
        chonTang(tangCu);

        if (cboLocTrangThai.getItemCount() == 0) {
            cboLocTrangThai.addItem("Tất cả trạng thái");
            for (TrangThaiPhong state : TrangThaiPhong.values()) {
                cboLocTrangThai.addItem(state);
            }
        }
        loading = false;

        capNhatNutTangSoDo();
        capNhatBangLoai();
        capNhatBangTang();
        locDanhSach();
        capNhatSoDo();
        capNhatKpi();
    }

    private void capNhatKpi() {
        int tong = controller.rooms().size();
        long sanSang = controller.rooms().stream()
                .filter(p -> !p.ngungSuDung() && p.trangThai() == TRONG).count();
        long dangDung = controller.rooms().stream()
                .filter(p -> !p.ngungSuDung() && (p.trangThai() == DANG_SU_DUNG || p.trangThai() == DA_DAT)).count();
        long canXuLy = controller.rooms().stream()
                .filter(p -> p.trangThai() == DANG_DON || p.trangThai() == BAO_TRI).count();

        lblKpiTongPhong.setText(tong + " phòng");
        lblKpiSanSang.setText(sanSang + " phòng");
        lblKpiDangDung.setText(dangDung + " phòng");
        lblKpiCanXuLy.setText(canXuLy + " phòng");
    }

    private void chonTang(String maTang) {
        for (int i = 0; i < cboLocTang.getItemCount(); i++) {
            Choice choice = cboLocTang.getItemAt(i);
            if (choice.id().equals(maTang)) {
                cboLocTang.setSelectedIndex(i);
                return;
            }
        }
    }

    private void locDanhSach() {
        if (loading) return;
        roomModel.setRowCount(0);
        String maTang = maDangChon(cboLocTang);
        Object selected = cboLocTrangThai.getSelectedItem();
        String keyword = txtTimKiem.getText().trim().toLowerCase();

        for (Phong phong : controller.rooms()) {
            if (!maTang.isBlank() && !maTang.equals(phong.maTang())) continue;
            if (selected instanceof TrangThaiPhong state && state != phong.trangThai()) continue;
            if (!keyword.isEmpty() && !phong.soPhong().toLowerCase().contains(keyword)
                    && !phong.maPhong().toLowerCase().contains(keyword)) {
                continue;
            }

            LoaiPhong loai = controller.type(phong.maLoaiPhong());
            Tang tang = controller.floor(phong.maTang());
            BigDecimal gia = phong.giaPhong() == null ? (loai == null ? BigDecimal.ZERO : loai.giaTheoNgay()) : phong.giaPhong();
            String tenLoai = loai == null ? phong.maLoaiPhong() : loai.tenLoai();
            String tenTang = tang == null ? phong.maTang() : tang.tenTang();

            roomModel.addRow(new Object[] {
                    phong.maPhong(),
                    phong.soPhong(),
                    tenLoai,
                    tenTang,
                    gia,
                    phong.trangThai().name(),
                    phong.ngungSuDung() ? "Ngừng SD" : "Hoạt động"
            });
        }
    }

    private void locPhongTheoBoLoc() {
        locDanhSach();
    }

    private void capNhatNutTangSoDo() {
        if (!maTangSoDo.isEmpty() && controller.floors().stream()
                .noneMatch(t -> maTangSoDo.equals(t.maTang()))) maTangSoDo = "";
        pnlNutTangSoDo.removeAll();
        nutTangSoDo.clear();
        taoNutTangSoDo("Tất cả tầng", "");
        for (Tang tang : controller.floors()) taoNutTangSoDo(tang.tenTang(), tang.maTang());
        pnlNutTangSoDo.revalidate();
        pnlNutTangSoDo.repaint();
    }

    private void taoNutTangSoDo(String ten, String maTang) {
        JToggleButton button = new JToggleButton(ten);
        button.setFont(HotelTheme.FONT_BOLD);
        button.setFocusPainted(false);
        button.setSelected(maTangSoDo.equals(maTang));
        button.setBackground(button.isSelected() ? HotelTheme.PRIMARY_BLUE : Color.WHITE);
        button.setForeground(button.isSelected() ? Color.WHITE : HotelTheme.TITLE_BLUE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HotelTheme.BORDER),
                BorderFactory.createEmptyBorder(7, 13, 7, 13)));
        button.addActionListener(event -> {
            maTangSoDo = maTang;
            for (JToggleButton nut : nutTangSoDo) {
                nut.setSelected(nut == button);
                nut.setBackground(nut == button ? HotelTheme.PRIMARY_BLUE : Color.WHITE);
                nut.setForeground(nut == button ? Color.WHITE : HotelTheme.TITLE_BLUE);
            }
            capNhatSoDo();
        });
        nutTangSoDo.add(button);
        pnlNutTangSoDo.add(button);
    }

    private void capNhatBangLoai() {
        typeModel.setRowCount(0);
        for (LoaiPhong loai : controller.types()) {
            typeModel.addRow(new Object[] {
                    loai.maLoaiPhong(),
                    loai.tenLoai(),
                    loai.soNguoiToiDa() + " khách",
                    loai.giaTheoNgay()
            });
        }
    }

    private void capNhatBangTang() {
        floorModel.setRowCount(0);
        for (Tang tang : controller.floors()) {
            long soPhong = controller.rooms().stream()
                    .filter(phong -> phong.maTang().equals(tang.maTang())).count();
            floorModel.addRow(new Object[] {
                    tang.maTang(),
                    tang.tenTang(),
                    soPhong + " phòng"
            });
        }
    }

    /**
     * Ve lai So do phong dang cac the card cao cap.
     */
    private void capNhatSoDo() {
        pnlSoDoGrid.removeAll();
        if (controller.rooms().isEmpty()) {
            if (huongDanPhongTrong == null) huongDanPhongTrong = taoHuongDanPhongTrong();
            pnlSoDoGrid.add(huongDanPhongTrong);
            lblSoDoKetQua.setText("Đang hiển thị 0/0 phòng");
            pnlSoDoGrid.revalidate();
            pnlSoDoGrid.repaint();
            return;
        }
        Object selected = cboSoDoTrangThai.getSelectedItem();
        String keyword = txtSoDoTimKiem.getText().trim().toLowerCase();
        Map<String, List<Phong>> theoTang = new LinkedHashMap<>();
        for (Tang tang : controller.floors()) theoTang.put(tang.maTang(), new ArrayList<>());

        for (Phong phong : controller.rooms()) {
            if (!maTangSoDo.isBlank() && !maTangSoDo.equals(phong.maTang())) continue;
            if (selected instanceof TrangThaiPhong state && state != phong.trangThai()) continue;
            if (!keyword.isEmpty() && !phong.soPhong().toLowerCase().contains(keyword)
                    && !phong.maPhong().toLowerCase().contains(keyword)) {
                continue;
            }
            theoTang.computeIfAbsent(phong.maTang(), key -> new ArrayList<>()).add(phong);
        }

        int hienThi = 0;
        int width = scrollSoDo == null ? 900 : scrollSoDo.getViewport().getWidth();
        int cot = Math.max(1, Math.min(7, Math.max(320, width - 24) / 190));
        for (Map.Entry<String, List<Phong>> entry : theoTang.entrySet()) {
            List<Phong> phongs = entry.getValue();
            if (phongs.isEmpty()) continue;
            phongs.sort(Comparator.comparing(Phong::soPhong));
            hienThi += phongs.size();
            Tang tang = controller.floor(entry.getKey());

            JPanel section = new JPanel(new BorderLayout(0, 8));
            section.setOpaque(false);
            section.setBorder(BorderFactory.createEmptyBorder(10, 0, 16, 0));
            JLabel title = new JLabel((tang == null ? entry.getKey() : tang.tenTang())
                    + "  ·  " + phongs.size() + " phòng");
            title.setFont(HotelTheme.FONT_BOLD);
            title.setForeground(HotelTheme.TITLE_BLUE);
            section.add(title, BorderLayout.NORTH);

            JPanel grid = new JPanel(new GridLayout(0, cot, 10, 10));
            grid.setOpaque(false);
            int hang = (phongs.size() + cot - 1) / cot;
            grid.setPreferredSize(new Dimension(0, hang * 120 + Math.max(0, hang - 1) * 10));
            for (Phong phong : phongs) {
                LoaiPhong loai = controller.type(phong.maLoaiPhong());
                BigDecimal gia = phong.giaPhong() == null
                        ? (loai == null ? BigDecimal.ZERO : loai.giaTheoNgay()) : phong.giaPhong();
                grid.add(taoCardPhong(phong, loai, gia));
            }
            section.add(grid, BorderLayout.CENTER);
            section.setAlignmentX(LEFT_ALIGNMENT);
            section.setMaximumSize(new Dimension(Integer.MAX_VALUE, section.getPreferredSize().height));
            pnlSoDoGrid.add(section);
        }

        if (hienThi == 0) {
            JLabel empty = new JLabel("Không có phòng phù hợp. Hãy chọn tầng hoặc bộ lọc khác.");
            empty.setForeground(HotelTheme.TEXT_SECONDARY);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));
            pnlSoDoGrid.add(empty);
        }
        lblSoDoKetQua.setText("Đang hiển thị " + hienThi + "/" + controller.rooms().size() + " phòng");
        pnlSoDoGrid.revalidate();
        pnlSoDoGrid.repaint();
    }

    private JPanel taoHuongDanPhongTrong() {
        JPanel guide = new JPanel(new BorderLayout(0, 12));
        guide.setBackground(Color.WHITE);
        guide.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HotelTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        JLabel text = new JLabel("<html><b>Chưa có phòng trong database.</b><br>"
                + "Tạo tầng, tạo hạng phòng rồi thêm phòng để có mã phòng cho Đặt phòng. "
                + "Tải lại dữ liệu chỉ đọc dữ liệu đã lưu.</html>");
        text.setForeground(HotelTheme.TITLE_BLUE);
        text.setFont(HotelTheme.FONT_REGULAR);
        guide.add(text, BorderLayout.NORTH);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(nut("1. Tạo tầng", HotelTheme.ButtonType.PRIMARY, () -> suaTang(null)));
        actions.add(nut("2. Tạo hạng phòng", HotelTheme.ButtonType.PRIMARY, () -> suaLoai(null)));
        actions.add(nut("3. Thêm phòng", HotelTheme.ButtonType.SUCCESS, () -> suaPhong(null)));
        guide.add(actions, BorderLayout.SOUTH);
        guide.setAlignmentX(LEFT_ALIGNMENT);
        guide.setMaximumSize(new Dimension(Integer.MAX_VALUE, guide.getPreferredSize().height));
        return guide;
    }

    /**
     * Tao the phong phong cach Hotel PMS hien dai.
     */
    private JPanel taoCardPhong(Phong phong, LoaiPhong loai, BigDecimal gia) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color statusColor = phong.ngungSuDung() ? HotelTheme.GRAY_BORDER : mauTrangThai(phong.trangThai());

        // Vien the voi vien tren dam mau the hien trang thai
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 1, 1, 1, statusColor),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        // Header the: So phong to & Loai phong
        JPanel cardHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        cardHeader.setOpaque(false);

        JLabel lblRoom = new JLabel("Phòng " + phong.soPhong());
        lblRoom.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblRoom.setForeground(HotelTheme.TEXT_PRIMARY);

        JLabel lblType = new JLabel(loai == null ? "" : loai.tenLoai());
        lblType.setFont(HotelTheme.FONT_SMALL);
        lblType.setForeground(HotelTheme.TEXT_SECONDARY);

        cardHeader.add(lblRoom);
        cardHeader.add(lblType);

        JLabel lblGia = new JLabel(HotelTheme.formatVnd(gia) + "/đêm");
        lblGia.setFont(HotelTheme.FONT_SMALL);
        lblGia.setForeground(HotelTheme.PRIMARY_BLUE);

        // Footer the: Pill badge trang thai
        JLabel lblStatus = new JLabel("● " + tenHienThiTrangThai(phong), SwingConstants.CENTER);
        lblStatus.setFont(HotelTheme.FONT_BADGE);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(mauNenTrangThai(phong));
        lblStatus.setForeground(mauChuTrangThai(phong));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(lblGia, BorderLayout.CENTER);
        card.add(lblStatus, BorderLayout.SOUTH);
        card.setPreferredSize(new Dimension(175, 120));

        // Double click chuot vao the de mo thong tin sua
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    suaPhong(phong);
                }
            }
        });

        return card;
    }

    private String tenHienThiTrangThai(Phong phong) {
        if (phong.ngungSuDung()) return "Ngừng sử dụng";
        return switch (phong.trangThai()) {
            case TRONG -> "Sẵn sàng (Trống)";
            case DA_DAT -> "Đã đặt trước";
            case DANG_SU_DUNG -> "Đang có khách";
            case DANG_DON -> "Đang dọn dẹp";
            case BAO_TRI -> "Đang bảo trì";
        };
    }

    private Color mauTrangThai(TrangThaiPhong state) {
        return switch (state) {
            case TRONG -> new Color(16, 185, 129); // Emerald
            case DA_DAT -> new Color(139, 92, 246); // Purple
            case DANG_SU_DUNG -> new Color(2, 132, 199); // Sky
            case DANG_DON -> new Color(245, 158, 11); // Amber
            case BAO_TRI -> new Color(239, 68, 68); // Red
        };
    }

    private Color mauNenTrangThai(Phong phong) {
        if (phong.ngungSuDung()) return HotelTheme.GRAY_BG;
        return switch (phong.trangThai()) {
            case TRONG -> HotelTheme.SUCCESS_BG;
            case DA_DAT -> HotelTheme.PURPLE_BG;
            case DANG_SU_DUNG -> HotelTheme.INFO_BG;
            case DANG_DON -> HotelTheme.WARNING_BG;
            case BAO_TRI -> HotelTheme.DANGER_BG;
        };
    }

    private Color mauChuTrangThai(Phong phong) {
        if (phong.ngungSuDung()) return HotelTheme.GRAY_TEXT;
        return switch (phong.trangThai()) {
            case TRONG -> HotelTheme.SUCCESS_TEXT;
            case DA_DAT -> HotelTheme.PURPLE_TEXT;
            case DANG_SU_DUNG -> HotelTheme.INFO_TEXT;
            case DANG_DON -> HotelTheme.WARNING_TEXT;
            case BAO_TRI -> HotelTheme.DANGER_TEXT;
        };
    }

    private void suaPhong(Phong current) {
        if (current == null && (controller.floors().isEmpty() || controller.types().isEmpty())) {
            boolean thieuTang = controller.floors().isEmpty();
            subTabs.setSelectedComponent(thieuTang ? pnlTang : pnlLoaiPhong);
            thongBao(thieuTang
                    ? "Chưa có tầng. Hãy thêm tầng trước khi tạo phòng."
                    : "Chưa có hạng phòng. Hãy thêm hạng phòng trước khi tạo phòng.");
            return;
        }
        JTextField ma = new JTextField(current == null ? "" : current.maPhong());
        JTextField so = new JTextField(current == null ? "" : current.soPhong());
        JTextField gia = new JTextField(current == null || current.giaPhong() == null
                ? "" : current.giaPhong().toPlainString());
        JComboBox<Choice> loai = new JComboBox<>();
        JComboBox<Choice> tang = new JComboBox<>();

        for (LoaiPhong value : controller.types()) {
            loai.addItem(new Choice(value.maLoaiPhong(), value.tenLoai()));
        }
        for (Tang value : controller.floors()) {
            tang.addItem(new Choice(value.maTang(), value.tenTang()));
        }
        if (current != null) {
            chon(loai, current.maLoaiPhong());
            chon(tang, current.maTang());
        }
        ma.setEditable(current == null);

        JPanel form = taoFormDialog(new String[] {
                "Mã phòng (ví dụ P101):",
                "Số phòng (hiển thị cửa):",
                "Hạng loại phòng:",
                "Vị trí tầng:",
                "Giá riêng (VNĐ/đêm - để trống dùng giá gốc):"
        }, new JComponent[] {ma, so, loai, tang, gia});

        String title = current == null ? "Thêm Phòng Mới" : "Cập Nhật Thông Tin Phòng " + current.soPhong();
        if (JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Choice loaiChon = (Choice) loai.getSelectedItem();
            Choice tangChon = (Choice) tang.getSelectedItem();
            Phong phong = new Phong(
                    ma.getText().trim().toUpperCase(),
                    so.getText().trim(),
                    loaiChon == null ? null : loaiChon.id(),
                    tangChon == null ? null : tangChon.id(),
                    gia.getText().isBlank() ? null : new BigDecimal(gia.getText().trim()),
                    current == null ? TRONG : current.trangThai(),
                    current != null && current.ngungSuDung()
            );
            controller.saveRoom(phong, current != null, () -> {});
        } catch (NumberFormatException ex) {
            thongBao("Đơn giá phòng phải là số hợp lệ.");
        }
    }

    private void capNhatPhong() {
        Phong current = phongDangChon();
        if (current == null) {
            thongBao("Vui lòng chọn một phòng trong bảng để cập nhật.");
            return;
        }
        suaPhong(current);
    }

    private void suaLoai(LoaiPhong current) {
        JTextField ma = new JTextField(current == null ? "" : current.maLoaiPhong());
        JTextField ten = new JTextField(current == null ? "" : current.tenLoai());
        JTextField sucChua = new JTextField(current == null ? "" : "" + current.soNguoiToiDa());
        JTextField gia = new JTextField(current == null ? "" : current.giaTheoNgay().toPlainString());
        ma.setEditable(current == null);

        JPanel form = taoFormDialog(new String[] {
                "Mã loại phòng (ví dụ STD, DLX):",
                "Tên loại phòng:",
                "Sức chứa tối đa (người):",
                "Đơn giá niêm yết theo ngày (VNĐ):"
        }, new JComponent[] {ma, ten, sucChua, gia});

        String title = current == null ? "Thêm Loại Phòng Mới" : "Cập Nhật Loại Phòng " + current.tenLoai();
        if (JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            controller.saveType(new LoaiPhong(
                    ma.getText().trim().toUpperCase(),
                    ten.getText().trim(),
                    Integer.parseInt(sucChua.getText().trim()),
                    new BigDecimal(gia.getText().trim())
            ), current != null, () -> {});
        } catch (NumberFormatException ex) {
            thongBao("Sức chứa và giá phòng phải là số hợp lệ.");
        }
    }

    private void capNhatLoai() {
        LoaiPhong current = loaiDangChon();
        if (current == null) {
            thongBao("Vui lòng chọn một hạng phòng để cập nhật.");
            return;
        }
        suaLoai(current);
    }

    private void suaTang(Tang current) {
        JTextField ma = new JTextField(current == null ? "" : current.maTang());
        JTextField ten = new JTextField(current == null ? "" : current.tenTang());
        ma.setEditable(current == null);

        JPanel form = taoFormDialog(new String[] {
                "Mã tầng (ví dụ T1, T2):",
                "Tên hiển thị tầng (ví dụ Tầng 1):"
        }, new JComponent[] {ma, ten});

        String title = current == null ? "Thêm Tầng Mới" : "Cập Nhật Tầng " + current.tenTang();
        if (JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            controller.saveFloor(new Tang(ma.getText().trim().toUpperCase(), ten.getText().trim()),
                    current != null, () -> {});
        }
    }

    private void capNhatTang() {
        Tang current = tangDangChon();
        if (current == null) {
            thongBao("Vui lòng chọn một tầng để cập nhật.");
            return;
        }
        suaTang(current);
    }

    private void capNhatNutPhong() {
        if (btnTrangThaiPhong == null || btnXoaPhong == null) return;
        Phong phong = phongDangChon();
        btnTrangThaiPhong.setText(phong != null && phong.ngungSuDung()
                ? "Kích hoạt lại" : "Ngừng sử dụng");
        btnTrangThaiPhong.setBackground(phong != null && phong.ngungSuDung()
                ? HotelTheme.BUTTON_SUCCESS : HotelTheme.BUTTON_DANGER);
        btnTrangThaiPhong.setEnabled(phong != null && !controller.isBusy());
        btnXoaPhong.setEnabled(phong != null && !controller.isBusy());
    }

    private void doiTrangThaiPhong() {
        Phong phong = phongDangChon();
        if (phong == null) {
            thongBao("Vui lòng chọn phòng cần đổi trạng thái.");
            return;
        }
        boolean kichHoat = phong.ngungSuDung();
        if (JOptionPane.showConfirmDialog(this,
                "Xác nhận " + (kichHoat ? "kích hoạt lại" : "ngừng sử dụng")
                        + " phòng " + phong.soPhong() + "?\n(Chỉ thực hiện được khi phòng đang trống)",
                "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (kichHoat) controller.reactivateRoom(phong.maPhong(), () -> {});
            else controller.disableRoom(phong.maPhong(), () -> {});
        }
    }

    private void xoaPhongChuaDung() {
        Phong phong = phongDangChon();
        if (phong == null) {
            thongBao("Vui lòng chọn phòng cần xóa.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this,
                "Xóa hẳn phòng " + phong.soPhong() + " (" + phong.maPhong() + ")?\n"
                        + "Chỉ xóa được phòng trống và chưa có dữ liệu liên quan. "
                        + "Phòng đã có lịch sử nên dùng Ngừng sử dụng.",
                "Xác nhận xóa phòng mẫu", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            controller.deleteUnusedRoom(phong.maPhong(), () -> {});
        }
    }

    private void xoaLoai() {
        String ma = id(tblLoai);
        if (ma != null && JOptionPane.showConfirmDialog(this,
                "Xác nhận xóa hạng phòng " + ma + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.deleteType(ma, () -> {});
        }
    }

    private void xoaTang() {
        String ma = id(tblTang);
        if (ma != null && JOptionPane.showConfirmDialog(this,
                "Xác nhận xóa tầng " + ma + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.deleteFloor(ma, () -> {});
        }
    }

    private Phong phongDangChon() {
        String ma = id(tblPhong);
        return ma == null ? null : controller.room(ma);
    }

    private LoaiPhong loaiDangChon() {
        String ma = id(tblLoai);
        return ma == null ? null : controller.type(ma);
    }

    private Tang tangDangChon() {
        String ma = id(tblTang);
        return ma == null ? null : controller.floor(ma);
    }

    private String id(JTable table) {
        int selected = table.getSelectedRow();
        return selected < 0 ? null : table.getModel().getValueAt(
                table.convertRowIndexToModel(selected), 0).toString();
    }

    private JButton nut(String label, HotelTheme.ButtonType type, Runnable action) {
        JButton button = HotelTheme.createButton(label, type, action);
        if (type != HotelTheme.ButtonType.SECONDARY) {
            actionButtons.add(button);
        }
        return button;
    }

    private void cauHinhBang(JTable table) {
        HotelTheme.table(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private static DefaultTableModel taoModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel taoFormDialog(String[] labels, JComponent[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.35;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(HotelTheme.FONT_BOLD);
            panel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.65;
            fields[i].setPreferredSize(new Dimension(220, 32));
            panel.add(fields[i], gbc);
        }
        return panel;
    }

    private void chon(JComboBox<Choice> combo, String id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id().equals(id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private String maDangChon(JComboBox<Choice> combo) {
        Choice choice = (Choice) combo.getSelectedItem();
        return choice == null ? "" : choice.id();
    }

    private void thongBao(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    private static final class ViewportWidthPanel extends JPanel implements Scrollable {
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) { return 20; }
        @Override public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return Math.max(100, visible.height - 40);
        }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    private record Choice(String id, String ten) {
        @Override
        public String toString() {
            return ten;
        }
    }
}
