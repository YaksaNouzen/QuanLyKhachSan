
package QLDichVu.model;

import java.math.BigDecimal;

public class DichVu {

    private int maDV;
    private int maLoaiDV;
    private String tenDV;
    private BigDecimal donGia;
    private String donViTinh;
    private String moTa;
    private boolean trangThai;

    public DichVu() {
    }

    public DichVu(int maLoaiDV, String tenDV,
                  BigDecimal donGia, String donViTinh,
                  String moTa, boolean trangThai) {
        this.maLoaiDV = maLoaiDV;
        this.tenDV = tenDV;
        this.donGia = donGia;
        this.donViTinh = donViTinh;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public DichVu(int maDV, int maLoaiDV, String tenDV,
                  BigDecimal donGia, String donViTinh,
                  String moTa, boolean trangThai) {
        this.maDV = maDV;
        this.maLoaiDV = maLoaiDV;
        this.tenDV = tenDV;
        this.donGia = donGia;
        this.donViTinh = donViTinh;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public int getMaDV() {
        return maDV;
    }

    public void setMaDV(int maDV) {
        this.maDV = maDV;
    }

    public int getMaLoaiDV() {
        return maLoaiDV;
    }

    public void setMaLoaiDV(int maLoaiDV) {
        this.maLoaiDV = maLoaiDV;
    }

    public String getTenDV() {
        return tenDV;
    }

    public void setTenDV(String tenDV) {
        this.tenDV = tenDV;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
}
