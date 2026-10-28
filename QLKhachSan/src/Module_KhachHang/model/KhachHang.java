package Module_KhachHang.model;

import java.sql.Timestamp;
import java.time.LocalDate;

public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private String loaiGiayTo;
    private String soGiayTo;
    private String quocGiaCap;
    private String soDienThoai;
    private String email;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String quocTich;
    private String diaChi;
    private Timestamp ngayTao;
    private String trangThai;

    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String value) { maKhachHang = value; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String value) { hoTen = value; }
    public String getLoaiGiayTo() { return loaiGiayTo; }
    public void setLoaiGiayTo(String value) { loaiGiayTo = value; }
    public String getSoGiayTo() { return soGiayTo; }
    public void setSoGiayTo(String value) { soGiayTo = value; }
    public String getQuocGiaCap() { return quocGiaCap; }
    public void setQuocGiaCap(String value) { quocGiaCap = value; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String value) { soDienThoai = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { email = value; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate value) { ngaySinh = value; }
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String value) { gioiTinh = value; }
    public String getQuocTich() { return quocTich; }
    public void setQuocTich(String value) { quocTich = value; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String value) { diaChi = value; }
    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp value) { ngayTao = value; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String value) { trangThai = value; }
}
