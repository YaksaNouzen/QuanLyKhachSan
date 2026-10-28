package Module_TaiChinh.model;

public class HoaDon {

    private String maHoaDon;
    private String maDatPhong;
    private String ngayLap;
    private double tienGiam;
    private double tongTien;
    private String trangThai;

    public HoaDon() {
    }

    public HoaDon(String maHoaDon, String maDatPhong,
            String ngayLap, double tienGiam,
            double tongTien, String trangThai) {

        this.maHoaDon = maHoaDon;
        this.maDatPhong = maDatPhong;
        this.ngayLap = ngayLap;
        this.tienGiam = tienGiam;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(String maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public String getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(String ngayLap) {
        this.ngayLap = ngayLap;
    }

    public double getTienGiam() {
        return tienGiam;
    }

    public void setTienGiam(double tienGiam) {
        this.tienGiam = tienGiam;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}