# SQL Server - Module Phong Nhom 2

## Schema nen va thu tu chay

Schema nen la `database/schema.sql` tren nhanh `QuanLyDatPhong` cua repo `QuanLyKhachSan`. O thoi diem ban giao, nhanh `main` chua chua schema nay. Cac bang Nhom 2 su dung: `Phong`, `LoaiPhong`, `Tang`, `DatPhong`, `VeSinh`, `BaoTri`.

Tren database test da tao schema nen:

1. Chay `patches/N2_001_buong_phong_va_bao_tri.sql`.
2. Chay `patches/N2_002_grant_quyen_nhom2.sql` bang tai khoan co quyen cap GRANT.
3. Chay lai `SchemaCheckNhom2` tu ung dung de doc cac bang/cot can thiet.

Patch N2_001 idempotent va co transaction. Patch them trang thai, ngay phan cong/xac nhan, muc do, nguoi bao, ngay bao, chi phi va ket qua; backfill viec cu dua tren `KetThuc`. Hai cot `BatDau` va `BaoTri.NguoiPhuTrach` duoc doi sang nullable de phieu cho/chua phan cong duoc luu dung nghiep vu. Gia tri cu khong bi xoa.

## Anh xa cot

- `Phong`: `MaPhong`, `SoPhong`, `MaLoaiPhong`, `MaTang`, `GiaPhong`, `TrangThai`, `NgungSuDung`.
- `DatPhong` (chi doc): `MaDatPhong`, `MaPhong`, `TrangThai`, `NgayTraDuKien`; guard dung `DANG_O` va `GIU_PHONG`.
- `VeSinh`: cac cot nen `MaVeSinh`, `MaPhong`, `NoiDung`, `NguoiPhuTrach`, `BatDau`, `KetThuc`; N2_001 bo sung `TrangThai`, `NgayPhanCong`, `NguoiXacNhan`, `XacNhanLuc`.
- `BaoTri`: cac cot nen `MaBaoTri`, `MaPhong`, `NoiDung`, `NguoiPhuTrach`, `BatDau`, `KetThuc`; N2_001 bo sung `TrangThai`, `MucDo`, `NguoiBao`, `NgayBao`, `ChiPhi`, `KetQua`.

Trang thai service ghi chuoi enum khong dau: ve sinh `CHO_DON`, `DANG_DON`, `HOAN_THANH`, `HUY`; bao tri `BAO_HONG`, `DANG_SUA`, `HOAN_TAT`, `HUY`; muc do `THAP`, `TRUNG_BINH`, `CAO`.

## Quyen

`hotelbooking_app` can co SELECT tren schema chung. N2_002 cap INSERT/UPDATE cho `Phong`, `VeSinh`, `BaoTri`; INSERT/UPDATE/DELETE cho `LoaiPhong`, `Tang`. Module khong xoa phong hay xoa phieu; khong sua `DatPhong` hoac `KhachHang`.

Cac patch trong thu muc nay chua duoc chay tren database trong qua trinh port. Luon chon database test dung schema va sao luu truoc khi ap dung vao moi truong nhom.
