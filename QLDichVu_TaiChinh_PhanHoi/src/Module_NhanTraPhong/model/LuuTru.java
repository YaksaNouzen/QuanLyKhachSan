package Module_NhanTraPhong.model;

import java.sql.Timestamp;

public class LuuTru {
    private String maDatPhong;
    private String maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private String maPhong;
    private String soPhong;
    private Timestamp ngayNhanDuKien;
    private Timestamp ngayTraDuKien;
    private Timestamp ngayNhan;
    private Timestamp ngayTra;
    private String trangThai;
    private boolean quaHanTraPhong;

    public String getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(String value) { maDatPhong = value; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String value) { maKhachHang = value; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String value) { hoTen = value; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String value) { soDienThoai = value; }
    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String value) { maPhong = value; }
    public String getSoPhong() { return soPhong; }
    public void setSoPhong(String value) { soPhong = value; }
    public Timestamp getNgayNhanDuKien() { return ngayNhanDuKien; }
    public void setNgayNhanDuKien(Timestamp value) { ngayNhanDuKien = value; }
    public Timestamp getNgayTraDuKien() { return ngayTraDuKien; }
    public void setNgayTraDuKien(Timestamp value) { ngayTraDuKien = value; }
    public Timestamp getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(Timestamp value) { ngayNhan = value; }
    public Timestamp getNgayTra() { return ngayTra; }
    public void setNgayTra(Timestamp value) { ngayTra = value; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String value) { trangThai = value; }
    public boolean isQuaHanTraPhong() { return quaHanTraPhong; }
    public void setQuaHanTraPhong(boolean value) { quaHanTraPhong = value; }
}
