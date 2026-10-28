# Quản lý nhân sự - Nhóm 3

Project Java Swing quản lý phòng ban và nhân viên do Nhóm 3 cung cấp.

## Cấu hình database

Project dùng SQL Server và database mặc định `QLNhanVien`. Trước khi chạy, cấu
hình các biến môi trường sau trên máy cá nhân:

```text
HOTEL_DB_SERVER=localhost
HOTEL_DB_NAME=QLNhanVien
HOTEL_DB_USER=sa
HOTEL_DB_PASSWORD=<mật khẩu của máy bạn>
```

Không ghi mật khẩu thật vào `DBConnection.java` hoặc commit lên GitHub.

## Chạy project

Mở thư mục `QuanLyNhanVien` bằng NetBeans và chạy project. Lớp chạy chính là
`QLNhanVien`.

Các thư mục `build`, `dist` và `nbproject/private` được bỏ qua khi commit vì là
dữ liệu sinh tự động hoặc cấu hình riêng của từng máy.
