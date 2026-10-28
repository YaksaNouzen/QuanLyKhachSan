package Module_TaiChinh.model;

public class ThanhToan {

    private String maThanhToan;
    private String maHoaDon;
    private String ngayThanhToan;
    private String phuongThuc;
    private double soTien;
    private String trangThai;

    public ThanhToan() {
    }

    public ThanhToan(String maThanhToan,
            String maHoaDon,
            String ngayThanhToan,
            String phuongThuc,
            double soTien,
            String trangThai) {

        this.maThanhToan = maThanhToan;
        this.maHoaDon = maHoaDon;
        this.ngayThanhToan = ngayThanhToan;
        this.phuongThuc = phuongThuc;
        this.soTien = soTien;
        this.trangThai = trangThai;
    }

    public String getMaThanhToan() {
        return maThanhToan;
    }

    public void setMaThanhToan(String maThanhToan) {
        this.maThanhToan = maThanhToan;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(String ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}