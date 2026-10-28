package QLDichVu.controller;

import QLDichVu.model.DichVu;
import QLDichVu.model.LoaiDichVu;
import QLDichVu.service.DichVuService;

import java.math.BigDecimal;
import java.util.List;

/** Dieu phoi du lieu giua DichVuPanel va DichVuService. */
public class DichVuController {

    private final DichVuService service;

    public DichVuController() {
        this(new DichVuService());
    }

    public DichVuController(DichVuService service) {
        if (service == null) {
            throw new IllegalArgumentException(
                    "Service dich vu khong hop le.");
        }
        this.service = service;
    }

    public List<DichVu> layTatCa() {
        return service.layTatCa();
    }

    public List<LoaiDichVu> layDanhSachLoai() {
        return service.layDanhSachLoai();
    }

    public List<DichVu> timTheoTen(String tuKhoa) {
        return service.timTheoTen(tuKhoa);
    }

    public boolean themDichVu(int maLoaiDichVu,
                              String tenDichVu,
                              String donGia,
                              String donViTinh,
                              String moTa,
                              boolean trangThai) {
        DichVu dichVu = taoDichVu(0, maLoaiDichVu, tenDichVu,
                donGia, donViTinh, moTa, trangThai);
        return service.them(dichVu);
    }

    public boolean capNhatDichVu(int maDichVu,
                                 int maLoaiDichVu,
                                 String tenDichVu,
                                 String donGia,
                                 String donViTinh,
                                 String moTa,
                                 boolean trangThai) {
        DichVu dichVu = taoDichVu(maDichVu, maLoaiDichVu,
                tenDichVu, donGia, donViTinh, moTa, trangThai);
        return service.capNhat(dichVu);
    }

    public boolean ngungHoatDong(int maDichVu) {
        return service.ngungHoatDong(maDichVu);
    }

    public boolean khoiPhuc(int maDichVu) {
        return service.khoiPhuc(maDichVu);
    }

    private DichVu taoDichVu(int maDichVu,
                             int maLoaiDichVu,
                             String tenDichVu,
                             String giaTriDonGia,
                             String donViTinh,
                             String moTa,
                             boolean trangThai) {
        BigDecimal donGia;
        String gia = giaTriDonGia == null
                ? "" : giaTriDonGia.trim();
        if (gia.isEmpty()) {
            throw new IllegalArgumentException(
                    "Don gia khong duoc de trong.");
        }
        try {
            donGia = new BigDecimal(gia);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Don gia phai la so hop le, vi du: 50000.");
        }

        return new DichVu(maDichVu, maLoaiDichVu,
                tenDichVu, donGia, donViTinh, moTa, trangThai);
    }
}
