package QLDichVu.controller;

import QLDichVu.service.DichVuService;
import QLDichVu.model.DichVu;
import QLDichVu.model.LoaiDichVu;
import java.sql.SQLException;
import java.util.List;

public class DichVuController {
    private final DichVuService service;

    public DichVuController() { this(new DichVuService()); }
    public DichVuController(DichVuService service) {
        this.service = java.util.Objects.requireNonNull(service);
    }

    public List<DichVu> getAll() throws SQLException { return service.getAll(); }
    public List<LoaiDichVu> getDanhSachLoai() throws SQLException { return service.getDanhSachLoai(); }
    public DichVu getById(int maDV) throws SQLException { return service.getById(maDV); }
    public List<DichVu> searchByName(String tuKhoa) throws SQLException {
        return service.searchByName(tuKhoa);
    }
    public DichVu taoDuLieu(LoaiDichVu loai, String ten, String gia,
            String donVi, String moTa, boolean trangThai) {
        return service.taoDuLieu(loai, ten, gia, donVi, moTa, trangThai);
    }
    public boolean insert(DichVu dv) throws SQLException { return service.insert(dv); }
    public boolean update(DichVu dv) throws SQLException { return service.update(dv); }
    public boolean delete(int maDV) throws SQLException { return service.delete(maDV); }
    public boolean restore(int maDV) throws SQLException { return service.restore(maDV); }
}