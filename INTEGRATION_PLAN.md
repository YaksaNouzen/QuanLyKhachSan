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

### Bước 4 - Nhập nhóm 3 và nhóm 4

- Nhập Nhân viên, Ca làm việc, Tài khoản/phân quyền.
- Nhập Kho vật tư, Nhà cung cấp, Nhập hàng.
- Giữ nghiệp vụ từng nhóm độc lập trong lần nhập đầu.

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
