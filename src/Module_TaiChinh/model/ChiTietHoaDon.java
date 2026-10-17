package Module_TaiChinh.model;

public class ChiTietHoaDon {

    private String maChiTiet;
    private String maHoaDon;
    private String maPhong;
    private String maDichVu;
    private String loaiChiPhi;
    private String moTa;
    private int soLuong;
    private double donGia;
    private double thanhTien;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maChiTiet,
            String maHoaDon,
            String maPhong,
            String maDichVu,
            String loaiChiPhi,
            String moTa,
            int soLuong,
            double donGia,
            double thanhTien) {

        this.maChiTiet = maChiTiet;
        this.maHoaDon = maHoaDon;
        this.maPhong = maPhong;
        this.maDichVu = maDichVu;
        this.loaiChiPhi = loaiChiPhi;
        this.moTa = moTa;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public String getMaChiTiet() {
        return maChiTiet;
    }

    public void setMaChiTiet(String maChiTiet) {
        this.maChiTiet = maChiTiet;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public String getMaDichVu() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu = maDichVu;
    }

    public String getLoaiChiPhi() {
        return loaiChiPhi;
    }

    public void setLoaiChiPhi(String loaiChiPhi) {
        this.loaiChiPhi = loaiChiPhi;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }
}