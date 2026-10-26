package Module_QuanLyNhapHang.model;

import java.math.BigDecimal;

public class ChiTietPhieuNhap {
    private String maPhieuNhap;
    private String maVatTu;
    private String tenVatTu;
    private int soLuong;
    private BigDecimal donGia;

    public ChiTietPhieuNhap() {
        donGia = BigDecimal.ZERO;
    }

    public ChiTietPhieuNhap(String maPhieuNhap, String maVatTu,
                            String tenVatTu, int soLuong,
                            BigDecimal donGia) {
        this.maPhieuNhap = maPhieuNhap;
        this.maVatTu = maVatTu;
        this.tenVatTu = tenVatTu;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(String value) { this.maPhieuNhap = value; }
    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String value) { this.maVatTu = value; }
    public String getTenVatTu() { return tenVatTu; }
    public void setTenVatTu(String value) { this.tenVatTu = value; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int value) { this.soLuong = value; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal value) { this.donGia = value; }

    public BigDecimal getThanhTien() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }
}
