# Tổng quan database tích hợp

Database đích: `QuanLyKhachSan`, gồm 31 bảng cho 15 module.

| Nhóm | Module | Bảng chính |
|---|---|---|
| 1 | Khách hàng | `KhachHang`, `LuotLuuTru` |
| 1 | Đặt phòng | `DatPhong`, `YeuCauDatPhong`, `MaDatPhongDaCap` |
| 1 | Nhận/trả phòng | `ChuyenPhong`, `PhongLuuTruNhan` |
| 2 | Phòng | `Tang`, `LoaiPhong`, `Phong`, `ChiTietPhong`, `PhongTienNghi` |
| 2 | Buồng phòng | `VeSinh` |
| 2 | Bảo trì | `BaoTri` |
| 3 | Nhân viên | `NhanVien` |
| 3 | Ca làm | `CaLam`, `PhanCa` |
| 3 | Tài khoản/phân quyền | `TaiKhoan`, `VaiTro` |
| 4 | Kho vật tư | `VatTu` |
| 4 | Nhà cung cấp | `NhaCungCap` |
| 4 | Nhập hàng | `PhieuNhap`, `ChiTietPhieuNhap` |
| 5 | Tài chính | `HoaDon`, `ChiTietHoaDon`, `ThanhToan` |
| 5 | Dịch vụ | `LoaiDichVu`, `DichVu`, `SuDungDichVu` |
| 5 | Phản hồi | `PhanHoi`, `LichSuXuLyPhanHoi` |

## Luồng quan hệ xuyên module

```text
KhachHang -> DatPhong -> HoaDon -> ThanhToan
                 |          |
                 |          +-> ChiTietHoaDon -> Phong / DichVu
                 +-> SuDungDichVu -> DichVu -> LoaiDichVu

Phong -> VeSinh / BaoTri
NhanVien -> TaiKhoan / PhanCa -> CaLam
NhaCungCap -> PhieuNhap -> ChiTietPhieuNhap -> VatTu
KhachHang -> PhanHoi -> LichSuXuLyPhanHoi
```

## Quy tắc mã

- Khóa số (`MaDV`, `MaLoaiDV`, `MaPhanHoi`, `MaLichSu`, `MaPhanCa`) dùng
  `IDENTITY`; người dùng không nhập mã.
- Khóa chuỗi dùng `dbo.sp_LayMaTuDong`, ví dụ `KH000001`, `HD000001`.
- Cột `MaDVHienThi`, `MaLoaiDVHienThi`, `MaPhanHoiHienThi` là mã trình bày;
  khóa liên kết bên trong vẫn là số để giữ đúng quyết định schema.
- Mã tự động chống trùng khóa. Các `UNIQUE INDEX/CONSTRAINT` riêng mới chống
  trùng khách hàng, nhân viên, nhà cung cấp, phòng, vật tư và dịch vụ theo
  thông tin nghiệp vụ.

## Hai cột nghiệp vụ mới nhưng không phá code cũ

- `NhanVien.SoGiayTo`: nullable, dùng để nhận diện một người và có unique
  index khi được nhập.
- `NhaCungCap.MaSoThue`: nullable, dùng để nhận diện một doanh nghiệp và có
  unique index khi được nhập.

Hai cột để `NULL` nên DAO cũ chưa truyền giá trị vẫn chạy. Khi tích hợp giao
diện, nên bổ sung ô nhập hai trường này để việc chống trùng phát huy tác dụng.
