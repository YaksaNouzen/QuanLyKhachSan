# Kế hoạch tích hợp 15 module Quản lý khách sạn

## 1. Baseline của nhánh tích hợp

- Nhánh: `integration/quan-ly-khach-san`.
- Điểm bắt đầu: commit `1e0107c` của nhánh
  `feature/nhom5-bo-sung-controller-service-dich-vu`.
- Ba module hiện có: Tài chính, Dịch vụ và Phản hồi khách hàng.
- Chưa thay đổi cấu trúc bảng hoặc dữ liệu của bất kỳ module nào.

Nhóm 5 được chọn làm baseline vì đã có `MainFrame` và giao diện mà toàn bộ
ứng dụng sẽ đồng bộ theo. Việc chọn baseline không có nghĩa là giữ nguyên
nhiều kết nối database hoặc nhiều điểm chạy khi hoàn tất tích hợp.

## 2. Nguồn của 15 module

| Nhóm | Nhánh nguồn | Module |
|---|---|---|
| 1 | `QuanLyDatPhong` | 1. Khách hàng; 2. Đặt phòng; 3. Nhận/trả phòng |
| 2 | `feature/nhom2-quan-ly-phong` | 4. Phòng; 5. Buồng phòng; 6. Bảo trì |
| 3 | `quanlynhansu` | 7. Nhân viên; 8. Ca làm việc; 9. Tài khoản và phân quyền |
| 4 | `CungUng&VatTu` | 10. Kho vật tư; 11. Nhà cung cấp; 12. Nhập hàng |
| 5 | `feature/nhom5-bo-sung-controller-service-dich-vu` | 13. Tài chính; 14. Dịch vụ; 15. Phản hồi khách hàng |

Nhánh `feature/nhom5-tai-chinh-dich-vu-phan-hoi` là bản nhóm 5 cũ và không
được dùng làm nguồn tích hợp nữa.

## 3. Quy tắc bắt buộc khi gộp

1. Không merge thẳng toàn bộ gốc của năm nhánh vào nhau.
2. Chỉ có một `MainFrame`, một điểm chạy ứng dụng và một bộ cấu hình build.
3. Không chép đè `DBConnection`; kết nối dùng chung chỉ được tạo sau khi đã
   đối chiếu toàn bộ database và được xác nhận.
4. Không sửa hoặc đổi tên bảng SQL trong lúc nhập source của một module.
5. Mỗi module phải biên dịch và kiểm thử độc lập trước khi nhập module kế tiếp.
6. Không đưa `build/`, `dist/`, `*.class`, cấu hình `nbproject/private/` hoặc
   thông tin đăng nhập database lên GitHub.
7. Nếu hai module dùng bảng cùng tên nhưng khác cột, phải lập bảng đối chiếu
   và thống nhất trước; không tự chọn một bản để ghi đè.

## 4. Thứ tự thực hiện

### Bước 1 - Tạo baseline tích hợp

- Tạo nhánh tích hợp từ nhóm 5 mới.
- Xác nhận đủ ba module của nhóm 5.
- Biên dịch toàn bộ source và chạy kiểm thử tầng Dịch vụ.
- Chưa nhập code hoặc SQL từ bốn nhóm còn lại.

### Bước 2 - Đối chiếu cơ sở dữ liệu

- Lập danh sách bảng, khóa chính, khóa ngoại và database name của năm nhóm.
- Phát hiện bảng trùng tên hoặc quan hệ chưa tương thích.
- Đề xuất một script tổng nhưng chỉ sửa sau khi được xác nhận.
- Hoàn thành: xem `DATABASE_AUDIT.md`. Bốn quyết định schema đã được xác nhận;
  master schema có thể được xây dựng theo các quyết định này.

### Bước 3 - Nhập nhóm 1 và nhóm 2

- Nhập Khách hàng, Đặt phòng, Nhận/trả phòng trước.
- Nhập Phòng, Buồng phòng, Bảo trì sau khi khóa liên kết phòng được xác định.
- Kết nối luồng đặt phòng với trạng thái phòng nhưng chưa nối tài chính vội.

Trạng thái ngày 2026-10-28:

- Đã nhập sáu package nghiệp vụ `khachhang`, `datphong`, `luutru`, `phong`,
  `buongphong`, `baotri` vào project nền Nhóm 5; không nhập hai `MainFrame`
  cũ và không chép đè cấu hình build.
- Đã tạo một kết nối dùng chung cho Nhóm 1-2, trỏ tới database
  `QuanLyKhachSan`; không lưu tài khoản hoặc mật khẩu trong source.
- Đã nhúng sáu màn hình vào `Module_TaiChinh.view.MainFrame`. Sidebar có
  thanh cuộn để tiếp tục nhận các module Nhóm 3-4.
- Đã bổ sung 7 procedure đặt/nhận/trả/đổi phòng vào master schema. Nhận phòng
  chuyển phòng sang `DANG_SU_DUNG`; trả phòng chuyển sang `DANG_DON` để module
  buồng phòng xử lý.
- Đã áp dụng mã tự động cho khách hàng và đặt phòng. Các kiểm tra dữ liệu,
  giấy tờ khách, phòng và lịch được chạy trước khi lấy số sequence nhằm hạn
  chế nhảy mã.
- 97 file source cùng toàn bộ test hiện có biên dịch thành công. Các test
  `PhongServiceTest`, `BuongPhongServiceTest`, `BaoTriServiceTest`,
  `Nhom2UiRenderTest` và `DichVuLayerTest` đều PASS.
- Còn cần chạy smoke test thật với SQL Server sau khi dịch vụ database trên
  máy hoạt động; hiện máy không kết nối được instance SQL Server cục bộ.

### Bước 4 - Nhập nhóm 3 và nhóm 4

- Nhập Nhân viên, Ca làm việc, Tài khoản/phân quyền.
- Nhập Kho vật tư, Nhà cung cấp, Nhập hàng.
- Giữ nghiệp vụ từng nhóm độc lập trong lần nhập đầu.

Trạng thái ngày 2026-10-28:

- Đã nhập các lớp nghiệp vụ, DAO và JPanel của cả sáu module vào project
  tích hợp; chỉ giữ `MainFrame` chung. Nhóm 3 được đặt dưới namespace `nhom3`
  để tránh xung đột tên package tổng quát `dao/model/view/util`.
- Các DAO Nhóm 3-4 đã dùng `common.database.DatabaseConnection` và schema
  `QuanLyKhachSan`; không nhập lớp kết nối có mật khẩu cố định của Nhóm 4
  hoặc chế độ H2 riêng của Nhóm 3.
- Đã sửa `NhanVienDAO.them` để nêu rõ cột khi INSERT, vì schema chung bổ sung
  cột `SoGiayTo` nullable. Mã Admin đầu tiên được cấp bằng sequence `NHAN_VIEN`.
- Khi database chưa có tài khoản, giao diện Nhóm 3 cho phép tạo Admin đầu tiên
  trong một transaction; từ đó đăng nhập và kiểm tra quyền Admin/Kế toán.
  Phân quyền cho toàn bộ 15 module còn thuộc Bước 5.
- 133 file Java và toàn bộ test hiện có biên dịch; năm kiểm thử offline đang
  có đều PASS. CRUD Nhóm 3-4 vẫn cần thử trực tiếp trên SQL Server và với dữ
  liệu thực. Mã tự động cho các form thêm mới còn lại thuộc Bước 5.

### Bước 5 - Liên kết nghiệp vụ và hoàn thiện

- Nhận/trả phòng -> Dịch vụ -> Hóa đơn -> Thanh toán -> Thống kê.
- Nhân viên -> Ca làm việc và quyền truy cập.
- Nhà cung cấp -> Phiếu nhập -> Tồn kho.
- Chạy kiểm thử tích hợp, tạo dữ liệu mẫu và kiểm tra toàn bộ 15 module.

## 5. Tiêu chí hoàn thành bước 1

- Nhánh tích hợp tồn tại độc lập và không làm thay đổi các nhánh thành viên.
- Baseline biên dịch được bằng Java 21.
- `DichVuLayerTest` trả về `DICH_VU_LAYER_TEST_OK`.
- Không có thay đổi SQL ngoài ba script nguyên bản của nhóm 5.
