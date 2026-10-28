# Quy ước patch database

- `FULL_DATABASE.sql` chỉ dùng để dựng database mới.
- Mỗi thay đổi cho database đã có dữ liệu phải là file mới: `001_...sql`,
  `002_...sql`, v.v.
- Patch phải chọn `USE QuanLyKhachSan`, kiểm tra đối tượng trước khi tạo/sửa,
  dùng transaction khi có nhiều thao tác và không tự xóa dữ liệu.
- Không sửa ngược patch đã chia sẻ. Nếu phát hiện lỗi, tạo patch số tiếp theo.
- Sau khi patch được kiểm thử, cập nhật thay đổi tương ứng vào
  `FULL_DATABASE.sql`.
