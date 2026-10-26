package Module_QuanLyNhapHang.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhap {
    private String maPhieuNhap;
    private String maNhaCungCap;
    private String tenNhaCungCap;
    private LocalDateTime ngayNhap;
    private BigDecimal tongTien = BigDecimal.ZERO;
    private String trangThai;
    private String nguoiLap;
    private String ghiChu;
    private final List<ChiTietPhieuNhap> chiTiet = new ArrayList<>();

    public String getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(String value) { this.maPhieuNhap = value; }
    public String getMaNhaCungCap() { return maNhaCungCap; }
    public void setMaNhaCungCap(String value) { this.maNhaCungCap = value; }
    public String getTenNhaCungCap() { return tenNhaCungCap; }
    public void setTenNhaCungCap(String value) { this.tenNhaCungCap = value; }
    public LocalDateTime getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDateTime value) { this.ngayNhap = value; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal value) { this.tongTien = value; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String value) { this.trangThai = value; }
    public String getNguoiLap() { return nguoiLap; }
    public void setNguoiLap(String value) { this.nguoiLap = value; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String value) { this.ghiChu = value; }
    public List<ChiTietPhieuNhap> getChiTiet() { return chiTiet; }

    public void tinhTongTien() {
        tongTien = chiTiet.stream()
                .map(ChiTietPhieuNhap::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
