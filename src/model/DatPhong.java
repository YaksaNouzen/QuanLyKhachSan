
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DatPhong {

    private String maDatPhong;
    private String maKhachHang;
    private String maPhong;
    private BigDecimal giaPhongChot;

    private Timestamp ngayDat;
    private Timestamp ngayNhan;
    private Timestamp ngayTra;

    private Timestamp ngayNhanDuKien;
    private Timestamp ngayTraDuKien;

    private String trangThai;

    public DatPhong() {
    }

    public DatPhong(
            String maDatPhong,
            String maKhachHang,
            String maPhong,
            BigDecimal giaPhongChot,
            Timestamp ngayDat,
            Timestamp ngayNhan,
            Timestamp ngayTra,
            Timestamp ngayNhanDuKien,
            Timestamp ngayTraDuKien,
            String trangThai) {

        this.maDatPhong = maDatPhong;
        this.maKhachHang = maKhachHang;
        this.maPhong = maPhong;
        this.giaPhongChot = giaPhongChot;
        this.ngayDat = ngayDat;
        this.ngayNhan = ngayNhan;
        this.ngayTra = ngayTra;
        this.ngayNhanDuKien = ngayNhanDuKien;
        this.ngayTraDuKien = ngayTraDuKien;
        this.trangThai = trangThai;
    }
    
    
public DatPhong(
        String maDatPhong,
        String maKhachHang,
        String maPhong,
        BigDecimal giaPhongChot,
        Timestamp ngayNhanDuKien,
        Timestamp ngayTraDuKien,
        String trangThai) {

    this.maDatPhong = maDatPhong;
    this.maKhachHang = maKhachHang;
    this.maPhong = maPhong;
    this.giaPhongChot = giaPhongChot;
    this.ngayNhanDuKien = ngayNhanDuKien;
    this.ngayTraDuKien = ngayTraDuKien;
    this.trangThai = trangThai;
}


    public String getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(String maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong = maPhong;
    }

    public BigDecimal getGiaPhongChot() {
        return giaPhongChot;
    }

    public void setGiaPhongChot(BigDecimal giaPhongChot) {
        this.giaPhongChot = giaPhongChot;
    }

    public Timestamp getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(Timestamp ngayDat) {
        this.ngayDat = ngayDat;
    }

    public Timestamp getNgayNhan() {
        return ngayNhan;
    }

    public void setNgayNhan(Timestamp ngayNhan) {
        this.ngayNhan = ngayNhan;
    }

    public Timestamp getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(Timestamp ngayTra) {
        this.ngayTra = ngayTra;
    }

    public Timestamp getNgayNhanDuKien() {
        return ngayNhanDuKien;
    }

    public void setNgayNhanDuKien(Timestamp ngayNhanDuKien) {
        this.ngayNhanDuKien = ngayNhanDuKien;
    }

    public Timestamp getNgayTraDuKien() {
        return ngayTraDuKien;
    }

    public void setNgayTraDuKien(Timestamp ngayTraDuKien) {
        this.ngayTraDuKien = ngayTraDuKien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
