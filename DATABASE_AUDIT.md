# Đối chiếu SQL của 15 module

Ngày đối chiếu: 2026-10-28. Tài liệu này chỉ phân tích; chưa sửa schema,
không chạy script trên SQL Server và không thay đổi dữ liệu.

## 1. Nguồn đã đối chiếu

| Nhóm | Nhánh và commit | SQL chính |
|---|---|---|
| 1 | `QuanLyDatPhong` - `f788713` | `database/schema.sql` và 3 patch |
| 2 | `feature/nhom2-quan-ly-phong` - `b1fc7b1` | `N2_001_buong_phong_va_bao_tri.sql`, patch quyền |
| 3 | `quanlynhansu` - `ee43696` | `Nhom3_NhanSu.sql`, dữ liệu H2 chạy thử |
| 4 | `CungUng&VatTu` - `f0bd559` | `warehouse_supply.sql` |
| 5 | `feature/nhom5-bo-sung-controller-service-dich-vu` - `1e0107c` | 3 script Tài chính, Dịch vụ, Phản hồi |

## 2. Hiện trạng database

| Phần | Database đang được SQL/code sử dụng | Số bảng định nghĩa |
|---|---|---:|
| Nhóm 1 + Nhóm 2 | `HotelManagementDB_LocalSimple` | 18; Nhóm 2 sửa 2 bảng sẵn có |
| Nhóm 3 | `QuanLyKhachSan` | 5 |
| Nhóm 4 | `QLHDKhachSan` | 4 |
| Nhóm 5 - Tài chính | `QLHDKhachSan` | 3 |
| Nhóm 5 - Dịch vụ | `QLDichVu` | 4 |
| Nhóm 5 - Phản hồi | `QLPhanHoi` | 2 |

Kết luận: hiện có **5 tên database**. Nếu cộng các định nghĩa có 36 bảng;
sau khi tính bảng `DichVu` bị định nghĩa hai lần thì có 35 tên bảng khác nhau.
Không thể nối các file SQL hiện tại rồi chạy liên tiếp.

## 3. Danh mục bảng theo module

### Nhóm 1 - Lễ tân và lưu trú

18 bảng:

- Khách hàng/đặt phòng: `KhachHang`, `DatPhong`, `YeuCauDatPhong`,
  `MaDatPhongDaCap`, `ChuyenPhong`, `LuotLuuTru`.
- Phòng: `Phong`, `LoaiPhong`, `Tang`, `ChiTietPhong`, `PhongTienNghi`.
- Vận hành: `VeSinh`, `BaoTri`.
- Dịch vụ/ghi nhận: `DichVu`, `SuDungDichVu`.
- Bảng nhận dữ liệu: `PhongLuuTruNhan`, `PhongDanhGiaNhan`,
  `PhongPhanAnhNhan`.

Ngoài bảng còn có `SeqKhachHang` và 7 procedure đặt/nhận/trả/đổi/hủy/xóa
phòng. `schema.sql` không tự chọn database; người chạy phải chọn database trống.

### Nhóm 2 - Phòng và cơ sở vật chất

Không tạo bảng mới. Module dùng trực tiếp `Phong`, `LoaiPhong`, `Tang`,
`DatPhong`, `VeSinh`, `BaoTri` của Nhóm 1.

Patch bổ sung:

- `VeSinh`: `TrangThai`, `NgayPhanCong`, `NguoiXacNhan`, `XacNhanLuc`;
  đổi `BatDau` thành nullable.
- `BaoTri`: `TrangThai`, `MucDo`, `NguoiBao`, `NgayBao`, `ChiPhi`,
  `KetQua`; đổi `BatDau`, `NguoiPhuTrach` thành nullable.

Đây là phần tương thích tốt nhất: Nhóm 2 đã chủ động chọn schema Nhóm 1 làm
schema nền. Khi tạo master schema mới, cần gộp các cột patch thẳng vào định
nghĩa bảng thay vì bắt người dùng chạy lại patch.

### Nhóm 3 - Nhân sự và hệ thống

5 bảng: `VaiTro`, `NhanVien`, `TaiKhoan`, `CaLam`, `PhanCa`.

Quan hệ:

- `TaiKhoan.MaNV -> NhanVien.MaNV` và `TaiKhoan.MaVaiTro -> VaiTro.MaVaiTro`.
- `PhanCa.MaNV -> NhanVien.MaNV` và `PhanCa.MaCa -> CaLam.MaCa`.

Script tạo database nếu thiếu nhưng không kiểm tra bảng trước khi `CREATE` và
chèn dữ liệu mẫu ngay trong script schema; chạy lần hai sẽ lỗi.

### Nhóm 4 - Kho và cung ứng

4 bảng: `VatTu`, `NhaCungCap`, `PhieuNhap`, `ChiTietPhieuNhap`.

Quan hệ:

- `PhieuNhap.MaNhaCungCap -> NhaCungCap.MaNhaCungCap`.
- `ChiTietPhieuNhap.MaPhieuNhap -> PhieuNhap.MaPhieuNhap`.
- `ChiTietPhieuNhap.MaVatTu -> VatTu.MaVatTu`.

Script kiểm tra sự tồn tại của bảng và index, nhưng mở đầu bằng
`USE QLHDKhachSan` mà không tạo database. Nó chỉ chạy độc lập nếu database này
đã được tạo trước. Script cũng chứa dữ liệu mẫu.

### Nhóm 5 - Tài chính

3 bảng: `HoaDon`, `ChiTietHoaDon`, `ThanhToan`.

Quan hệ nội bộ:

- `ChiTietHoaDon.MaHoaDon -> HoaDon.MaHoaDon`.
- `ThanhToan.MaHoaDon -> HoaDon.MaHoaDon`.

`HoaDon.MaDatPhong`, `ChiTietHoaDon.MaPhong` và
`ChiTietHoaDon.MaDichVu` mới chỉ là giá trị tham chiếu, chưa có foreign key.
`HoaDonDAO.search` luôn `INNER JOIN DatPhong`, nên chức năng tìm hóa đơn sẽ lỗi
hoặc không có kết quả nếu `DatPhong` chưa nằm cùng database.

### Nhóm 5 - Dịch vụ

4 bảng: `LoaiDichVu`, `DichVu`, `PhieuSuDung`, `ChiTietSuDung`.

Quan hệ:

- `DichVu.MaLoaiDV -> LoaiDichVu.MaLoaiDV`.
- `ChiTietSuDung.MaDV -> DichVu.MaDV`.
- `ChiTietSuDung.MaPhieuSD -> PhieuSuDung.MaPhieuSD`.

Script là bản export nguyên database: chứa đường dẫn `.mdf/.ldf` cố định dưới
`C:\Program Files\Microsoft SQL Server\MSSQL17.MSSQLSERVER`, nhiều thiết lập
server và dữ liệu mẫu. Script không portable và không thể dùng làm một phần
của master schema.

### Nhóm 5 - Phản hồi

2 bảng: `PhanHoi`, `LichSuXuLyPhanHoi`.

- `LichSuXuLyPhanHoi.MaPhanHoi -> PhanHoi.MaPhanHoi`.
- `PhanHoi.MaKhachHang` mới là tham chiếu logic, chưa có foreign key.
- Script có transaction và bảo vệ dữ liệu bằng cách dừng nếu bảng đã tồn tại.

## 4. Xung đột phải giải quyết

### C1 - Nhiều database tách rời (nghiêm trọng)

Các DAO dùng năm database khác nhau. Foreign key không thể nối xuyên database
theo cách thiết kế hiện tại; `HoaDonDAO.search` cũng yêu cầu `DatPhong` nằm
cùng database. Toàn bài cần chọn một database đích duy nhất.

Đề xuất chờ xác nhận: dùng tên `QuanLyKhachSan`, vì trung tính và đúng tên bài.

### C2 - Hai bảng `DichVu` không tương thích (nghiêm trọng)

| Thuộc tính | Nhóm 1 | Nhóm 5 |
|---|---|---|
| Khóa chính | `MaDichVu VARCHAR(20)` | `MaDV INT IDENTITY` |
| Tên | `TenDichVu NVARCHAR(100)` | `TenDV NVARCHAR(150)` |
| Loại dịch vụ | Không có | `MaLoaiDV INT` |
| Mô tả | Không có | `MoTa NVARCHAR(255)` |
| Trạng thái | `NgungSuDung BIT` | `TrangThai BIT` |

Không thể giữ cả hai bảng cùng tên. Đề xuất chờ xác nhận: lấy bảng Nhóm 5 làm
danh mục chuẩn vì module 14 đang CRUD trực tiếp schema này; đổi
`SuDungDichVu.MaDichVu` và phần tài chính theo khóa `INT`. Không áp dụng trước
khi nhóm duyệt vì lựa chọn này kéo theo sửa Java của phần liên kết.

### C3 - `MaDatPhong` lệch kiểu và độ dài (nghiêm trọng)

- Nhóm 1: `DatPhong.MaDatPhong VARCHAR(20)`.
- Tài chính: `HoaDon.MaDatPhong VARCHAR(30)`.
- Dịch vụ: `PhieuSuDung.MaDatPhong INT`.

`INT` không thể tạo foreign key tới `VARCHAR(20)`. Đề xuất giữ khóa gốc của
Nhóm 1 là `VARCHAR(20)`, đổi các bảng phụ thuộc về cùng kiểu và độ dài.

### C4 - Khóa phòng/dịch vụ của chi tiết hóa đơn (nghiêm trọng)

- `Phong.MaPhong` là `VARCHAR(20)` nhưng `ChiTietHoaDon.MaPhong` là
  `VARCHAR(30)`.
- `ChiTietHoaDon.MaDichVu` là `VARCHAR(30)` nhưng `DichVu.MaDV` của Nhóm 5
  là `INT`.

Chưa được thêm foreign key trước khi thống nhất hai kiểu này.

### C5 - Phản hồi bị chồng ý nghĩa (cần xác nhận nghiệp vụ)

Nhóm 1 có `PhongDanhGiaNhan` và `PhongPhanAnhNhan`; Nhóm 5 có `PhanHoi` và
`LichSuXuLyPhanHoi`. Đây không trùng tên bảng nhưng cùng xử lý đánh giá/phản
ánh. Cần xác nhận bảng Nhóm 1 chỉ là dữ liệu nhận từ module khác hay vẫn là
chức năng độc lập. Nếu cùng một nghiệp vụ, giữ cả hai sẽ sinh dữ liệu trùng.

### C6 - Nhân viên mới chỉ được lưu bằng tên tự do

Các cột `VeSinh.NguoiPhuTrach`, `VeSinh.NguoiXacNhan`,
`BaoTri.NguoiBao`, `BaoTri.NguoiPhuTrach`, `PhieuNhap.NguoiLap` và
`LichSuXuLyPhanHoi.NguoiThucHien` là `NVARCHAR`, chưa liên kết
`NhanVien.MaNV VARCHAR(10)`. Đây chưa gây lỗi khi chạy nhưng không đảm bảo
toàn vẹn dữ liệu nhân sự.

### C7 - Script và dữ liệu mẫu chưa tách

- Nhóm 3, Nhóm 4 và Dịch vụ Nhóm 5 trộn dữ liệu mẫu vào script schema.
- Dịch vụ Nhóm 5 có đường dẫn file database phụ thuộc máy.
- Patch xóa foreign key Nhóm 1 dùng tên constraint tự sinh của một database
  cụ thể; không được đưa vào master schema mới.
- Hai patch quyền phụ thuộc user `hotelbooking_app`; không nên chạy mặc định
  trên máy thành viên.

## 5. Các điểm có thể gộp an toàn

- Schema Nhóm 1 với patch Nhóm 2, vì Nhóm 2 được viết trực tiếp trên schema
  nền Nhóm 1.
- Bốn bảng Nhóm 3 không trùng tên với các nhóm khác; `TaiKhoan` liên kết nội
  bộ rõ ràng. (Tổng phần Nhóm 3 là 5 bảng.)
- Bốn bảng Nhóm 4 không trùng tên với các nhóm khác.
- Ba bảng tài chính không trùng tên và có khóa nội bộ rõ ràng.
- `PhanHoi.MaKhachHang VARCHAR(20)` đã khớp chính xác
  `KhachHang.MaKhachHang VARCHAR(20)`; có thể thêm FK sau khi dùng chung DB.

## 6. Khung master SQL đề xuất, chưa tạo

Sau khi duyệt các quyết định ở mục 7, bộ database tích hợp nên tách như sau:

1. `00_create_database.sql`: chỉ tạo/chọn một database.
2. `01_le_tan_luu_tru.sql`: bảng lõi Nhóm 1.
3. `02_phong_co_so_vat_chat.sql`: cột cuối của `VeSinh`, `BaoTri` và index.
4. `03_nhan_su_he_thong.sql`: 5 bảng Nhóm 3, không chứa dữ liệu mẫu.
5. `04_kho_cung_ung.sql`: 4 bảng Nhóm 4, không chứa dữ liệu mẫu.
6. `05_tai_chinh_dich_vu_phan_hoi.sql`: schema Nhóm 5 đã chuẩn hóa khóa.
7. `06_foreign_keys.sql`: các FK xuyên module sau khi mọi bảng đã tồn tại.
8. `07_procedures_indexes.sql`: procedure, sequence và index.
9. `08_sample_data.sql`: dữ liệu demo có thể chạy riêng.
10. `FULL_DATABASE.sql`: bản ghép theo đúng thứ tự để thành viên dễ dựng DB.

## 7. Quyết định schema đã được xác nhận

Các quyết định sau được xác nhận ngày 2026-10-28:

1. Database chung dùng tên `QuanLyKhachSan`.
2. Bảng `DichVu` lấy schema Nhóm 5, khóa chính `MaDV INT IDENTITY`.
3. Dùng `SuDungDichVu` của Nhóm 1 để ghi nhận dịch vụ khách dùng; đổi khóa
   dịch vụ của bảng này sang `INT`. Không đưa `PhieuSuDung` và
   `ChiTietSuDung` vào master schema.
4. Module `PhanHoi`/`LichSuXuLyPhanHoi` của Nhóm 5 là chức năng phản hồi
   chính. Không đưa `PhongDanhGiaNhan` và `PhongPhanAnhNhan` vào master schema.

Theo bốn quyết định này, master schema dự kiến có 31 bảng.

## 8. Cách bổ sung database sau này

- `FULL_DATABASE.sql` là bản đầy đủ mới nhất để dựng database mới.
- Database đã có dữ liệu chỉ được thay đổi bằng patch đánh số tăng dần trong
  `database/patches`, ví dụ `001_them_cot.sql`, `002_them_bang.sql`.
- Patch phải chọn đúng database, kiểm tra đối tượng đã tồn tại, dùng
  transaction khi có nhiều thao tác và không tự xóa dữ liệu.
- Sau khi patch được kiểm thử, thay đổi tương ứng phải được cập nhật vào các
  script thành phần và `FULL_DATABASE.sql`.
- Không sửa ngược một patch đã được chia sẻ; nếu cần sửa tiếp thì tạo patch
  mới để mọi máy có cùng lịch sử thay đổi.
