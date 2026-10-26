# Module Kho & Cung ứng

Project Java Swing độc lập gồm ba module nghiệp vụ trước khi gộp vào hệ
thống quản lý khách sạn:

- `Module_QuanLyVatTu`: quản lý vật tư và cảnh báo sắp hết.
- `Module_QuanLyNhaCungCap`: quản lý nhà cung cấp, lịch sử nhập và vật tư đã cung cấp.
- `Module_QuanLyNhapHang`: quản lý phiếu nhập nhiều dòng; lưu nháp, xác nhận và hủy.

Mỗi module có các package con `model`, `Dao`, `service`, `controller`, `view`.
Module nhập hàng sử dụng danh mục từ module vật tư và nhà cung cấp. Các thành
phần hạ tầng dùng chung (`DBConnection`, màu giao diện và `MainFrame`) nằm dưới
package `kho.cungung`, không phải module nghiệp vụ thứ tư.

## Chuẩn bị database

1. Mở SQL Server Management Studio.
2. Chọn database `QLHDKhachSan`.
3. Chạy `database/warehouse_supply.sql` đúng một lần.
4. Kiểm tra thông tin kết nối tại
   `src/kho/cungung/database/DBConnection.java`.

Khi xác nhận phiếu nhập, chương trình khóa phiếu, kiểm tra trạng thái, cộng tồn
cho từng vật tư và đổi trạng thái trong cùng một transaction. Nếu một bước lỗi,
toàn bộ thay đổi được rollback nên không xảy ra cộng tồn một phần hoặc cộng hai
lần.

## Khi gộp vào project chính

Sao chép ba package `Module_QuanLyVatTu`, `Module_QuanLyNhaCungCap`,
`Module_QuanLyNhapHang` và file SQL vào repository chung. Sau đó:

1. Đổi import/lớp `DBConnection` sang lớp kết nối dùng chung của nhóm.
2. Gắn ba panel `VatTuPanel`, `NhaCungCapPanel`, `PhieuNhapPanel` vào
   `MainFrame`/`CardLayout` chung.
3. Không sao chép `kho.cungung.KhoCungUng`; đây chỉ là điểm chạy độc lập.

Trường `NguoiLap` hiện dùng `NVARCHAR(100)` vì module tài khoản/nhân viên chung
chưa được cung cấp. Khi nhóm chốt bảng nhân viên, có thể bổ sung khóa ngoại bằng
một migration SQL riêng.
