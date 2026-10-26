# Cơ sở dữ liệu Hotel Management

`schema.sql` là bản DDL chuẩn để thành viên trong nhóm xem và dựng cấu trúc
database. File chỉ chứa schema (bảng, khóa, constraint, index, sequence và stored
procedure), không chứa dữ liệu thật, tài khoản đăng nhập hoặc mật khẩu.

## Cách sử dụng

1. Tạo một database **mới và hoàn toàn trống** trên SQL Server 2022 trở lên.
2. Chọn đúng database trống vừa tạo trong SSMS hoặc Azure Data Studio.
3. Chạy toàn bộ `schema.sql` đúng **một lần**, từ trên xuống.
4. Tự tạo login/user phù hợp cho từng máy; không commit mật khẩu vào Git.

Không chạy `schema.sql` trên `HotelManagementDB_LocalSimple` hiện tại hoặc trên
database đã có bảng. File sẽ phát hiện trường hợp này, dừng trước khi tạo đối
tượng và hướng dẫn dùng patch. Các lỗi “There is already an object named ...”
nghĩa là database đã được khởi tạo, không phải schema bị thiếu.

## Database đã tồn tại

- Giữ nguyên dữ liệu và schema hiện có.
- Chỉ chạy file thay đổi mới trong `database/patches/` theo đúng thứ tự.
- Không xóa bảng hoặc database để chạy lại baseline nếu còn dữ liệu cần giữ.

Nếu đã chạy nhầm phiên bản baseline cũ trên `HotelManagementDB_LocalSimple`,
hãy dùng `patches/001_remove_duplicate_foreign_keys.sql`. Patch này chỉ loại các
foreign key trùng được tạo ngày 26/10/2026; constraint gốc và dữ liệu được giữ.

Sau khi thêm module Khách hàng, chạy
`patches/002_grant_customer_module_permissions.sql` bằng tài khoản quản trị để
cấp quyền tối thiểu cho `hotelbooking_app`. Không cần cấp role `db_datawriter`.

Chạy `patches/003_allow_in_stay_room_transfer.sql` để cho phép đổi phòng với
đơn `DANG_O`, tách lịch sử thành hai chặng và lấy giá theo phòng mới.

Schema được đối chiếu từ `HotelManagementDB_LocalSimple` ngày 26/10/2026.
Mọi thay đổi tiếp theo cần được thực hiện bằng patch riêng trong `patches/`, sau
đó cập nhật lại `schema.sql` khi patch đã được xác minh.

