# Nhóm 3 - Nhân sự và hệ thống

Project Java Swing gồm ba module nghiệp vụ:

1. Quản lý nhân viên.
2. Quản lý ca làm việc và phân ca.
3. Quản lý tài khoản và phân quyền.

## Yêu cầu

- JDK 25.
- SQL Server hoặc H2 2.3.232 để chạy thử.
- Microsoft JDBC Driver for SQL Server 12.8.1 (JRE 11 trở lên).

Các file JAR không được lưu trong Git. Đặt chúng trong thư mục `lib/` với tên:

```text
mssql-jdbc-12.8.1.jre11.jar
h2-2.3.232.jar
```

## Cấu hình SQL Server

Chạy `database/Nhom3_NhanSu.sql`, sau đó cấu hình biến môi trường:

```text
HOTEL_DB_SERVER=localhost
HOTEL_DB_NAME=QuanLyKhachSan
HOTEL_DB_USER=sa
HOTEL_DB_PASSWORD=<mật khẩu SQL Server trên máy>
```

Không ghi mật khẩu thật trực tiếp trong `DBConnection.java` hoặc commit lên Git.

Lớp chạy chính là `view.LoginForm`. Nếu chưa có SQL Server, chương trình cho phép
chuyển sang H2 chạy thử với dữ liệu trong `src/util/du_lieu_chay_thu.sql`.

## Cấu trúc

```text
src/model  - đối tượng dữ liệu
src/dao    - truy vấn và cập nhật CSDL
src/util   - kết nối, mã hóa, phiên đăng nhập và giao diện dùng chung
src/view   - các màn hình Swing
database   - script SQL Server
```

